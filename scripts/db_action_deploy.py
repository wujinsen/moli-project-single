#!/usr/bin/env python3
"""Deploy / verify action permission SQL against local moli DB (application-dev.yml)."""
from __future__ import annotations

import re
import sys
from pathlib import Path

import pymysql

ROOT = Path(__file__).resolve().parents[1]
SQL_DIR = ROOT / "docs" / "sql"

DB = dict(
    host="localhost",
    port=3306,
    user="root",
    password="12345678",
    database="moli",
    charset="utf8mb4",
)


def connect():
    return pymysql.connect(**DB)


def next_id(conn, table: str) -> int:
    cur = conn.cursor()
    cur.execute(f"SELECT COALESCE(MAX(id), 0) + 1 FROM {table}")
    return int(cur.fetchone()[0])


def run_sql_file(conn, path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    # strip line comments; split on semicolon
    lines = []
    for line in text.splitlines():
        stripped = line.strip()
        if stripped.startswith("--"):
            continue
        lines.append(line)
    body = "\n".join(lines)
    statements = [s.strip() for s in body.split(";") if s.strip()]
    cur = conn.cursor()
    executed = 0
    for stmt in statements:
        cur.execute(stmt)
        executed += 1
    conn.commit()
    return executed


def query_status(conn):
    cur = conn.cursor()
    cur.execute("SELECT perm_code FROM sys_action ORDER BY perm_code")
    actions = [r[0] for r in cur.fetchall()]
    cur.execute("SELECT role_id, COUNT(*) FROM sys_role_action GROUP BY role_id ORDER BY role_id")
    role_counts = cur.fetchall()
    cur.execute("SELECT COUNT(*) FROM sys_menu WHERE menu_type = 'F'")
    f_count = cur.fetchone()[0]
    cur.execute("SELECT COUNT(*) FROM sys_menu WHERE component = 'system/action/index'")
    action_menu = cur.fetchone()[0]
    cur.execute(
        "SELECT perm_code FROM sys_action WHERE perm_code LIKE 'system:dict:%' ORDER BY perm_code"
    )
    dict_actions = [r[0] for r in cur.fetchall()]
    return {
        "action_count": len(actions),
        "actions": actions,
        "role_action_counts": role_counts,
        "f_menu_count": f_count,
        "action_menu_count": action_menu,
        "dict_actions": dict_actions,
    }


def ensure_action_menu(conn):
    cur = conn.cursor()
    cur.execute("SELECT COUNT(*) FROM sys_menu WHERE component = 'system/action/index'")
    if cur.fetchone()[0]:
        print("action catalog menu already exists")
        return
    cur.execute(
        "SELECT id, parent_id, order_num FROM sys_menu "
        "WHERE perms = 'system:menu:list' AND menu_type = 'C' LIMIT 1"
    )
    row = cur.fetchone()
    if not row:
        print("SKIP action menu: no system:menu:list C page")
        return
    _, parent_id, order_num = row
    cur.execute("SELECT COALESCE(MAX(id), 0) + 1 FROM sys_menu")
    new_id = cur.fetchone()[0]
    cur.execute(
        "INSERT INTO sys_menu (id, menu_name, parent_id, order_num, path, component, route_name, "
        "menu_type, perms, status, create_time) "
        "VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW())",
        (
            new_id,
            "动作目录",
            parent_id,
            int(order_num or 0) + 1,
            "action",
            "system/action/index",
            "ActionManage",
            "C",
            "system:menu:list",
            1,
        ),
    )
    conn.commit()
    print(f"action catalog menu inserted id={new_id}")


def sync_role1_actions(conn):
    """Grant role id=1 all system/operation actions (idempotent)."""
    cur = conn.cursor()
    cur.execute(
        """
        INSERT IGNORE INTO sys_role_action (role_id, perm_code)
        SELECT 1, a.perm_code FROM sys_action a
        WHERE a.perm_code LIKE 'system:%' OR a.perm_code LIKE 'operation:%'
        """
    )
    conn.commit()
    cur.execute("SELECT COUNT(*) FROM sys_role_action WHERE role_id = 1")
    print(f"role_1_action_count={cur.fetchone()[0]}")


def ensure_smoke_role(conn):
    """Role: dict_list_only — page dict:list, no dict:add/edit/remove."""
    cur = conn.cursor()
    cur.execute("SELECT id FROM sys_menu WHERE perms = 'system:dict:list' AND menu_type = 'C' LIMIT 1")
    row = cur.fetchone()
    if not row:
        print("WARN: no C menu with system:dict:list — skip smoke role")
        return None
    menu_id = row[0]

    role_name = "dict_list_only_smoke"
    cur.execute("SELECT id FROM sys_role WHERE role_name = %s", (role_name,))
    existing = cur.fetchone()
    if existing:
        role_id = existing[0]
        cur.execute("DELETE FROM sys_role_menu WHERE role_id = %s", (role_id,))
        cur.execute("DELETE FROM sys_role_action WHERE role_id = %s", (role_id,))
    else:
        cur.execute("SELECT COALESCE(MAX(id), 0) + 1 FROM sys_role")
        role_id = cur.fetchone()[0]
        cur.execute(
            "INSERT INTO sys_role (id, role_name, order_num, status, remark, create_time) "
            "VALUES (%s, %s, %s, 1, %s, NOW())",
            (role_id, role_name, "99", "smoke: dict list only, no add/edit/remove"),
        )

    rm_id = next_id(conn, "sys_role_menu")
    cur.execute(
        "INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES (%s, %s, %s)",
        (rm_id, role_id, menu_id),
    )
    conn.commit()
    print(f"smoke_role_id={role_id} role_name={role_name} menu_id={menu_id}")
    return role_id


def assign_smoke_user(conn, role_id: int, username: str = "ry"):
    """Assign test user to smoke role (keeps other roles; adds one more)."""
    cur = conn.cursor()
    cur.execute("SELECT id FROM sys_user WHERE user_name = %s AND status = 1 LIMIT 1", (username,))
    row = cur.fetchone()
    if not row:
        cur.execute(
            "SELECT id, user_name FROM sys_user WHERE status = 1 AND user_name NOT IN ('superadmin','admin') "
            "ORDER BY id LIMIT 1"
        )
        row = cur.fetchone()
        if not row:
            print("WARN: no assignable user found")
            return None
        username = row[1]
        print(f"WARN: requested user not found; using {username}")
    user_id = row[0]
    cur.execute(
        "SELECT 1 FROM sys_user_role WHERE user_id = %s AND role_id = %s",
        (user_id, role_id),
    )
    if not cur.fetchone():
        ur_id = next_id(conn, "sys_user_role")
        cur.execute(
            "INSERT INTO sys_user_role (id, user_id, role_id) VALUES (%s, %s, %s)",
            (ur_id, user_id, role_id),
        )
        conn.commit()
        print(f"assigned user_id={user_id} user_name={username} -> role_id={role_id}")
    else:
        print(f"user {username} already has role_id={role_id}")
    return user_id


def main():
    mode = sys.argv[1] if len(sys.argv) > 1 else "deploy"

    with connect() as conn:
        if mode == "status":
            s = query_status(conn)
            print("=== status ===")
            for k, v in s.items():
                if k != "actions":
                    print(f"{k}: {v}")
            return

        before = query_status(conn)
        print(f"before: {before['action_count']} actions, dict={before['dict_actions']}")

        # Fresh DB: use docs/sql/00_schema.sql + 01_baseline_data.sql (see README.md)
        migrate = SQL_DIR / "migrate_sys_action.sql"
        if migrate.exists():
            n = run_sql_file(conn, migrate)
            print(f"OK {migrate.name}: {n} statements")
        else:
            print("INFO: no migrate_sys_action.sql; baseline is docs/sql/00_schema.sql + 01_baseline_data.sql")
        try:
            ensure_action_menu(conn)
        except Exception as e:
            print(f"WARN action menu: {e}")

        after = query_status(conn)
        print(f"after: {after['action_count']} actions")
        print(f"dict actions: {after['dict_actions']}")
        print(f"F menus: {after['f_menu_count']}")
        print(f"action catalog menu: {after['action_menu_count']}")
        print(f"role_action by role: {after['role_action_counts']}")

        if mode == "deploy":
            sync_role1_actions(conn)
            smoke_role_id = ensure_smoke_role(conn)
            if smoke_role_id:
                assign_smoke_user(conn, smoke_role_id, "ry")
            print("\n=== smoke test (user ry + role dict_list_only_smoke) ===")
            print("1. Re-login as ry (password per your seed)")
            print("2. 字典管理：能进列表；点「新增」-> Toast 无权限操作")
            print("3. API: POST /dict/type without system:dict:add -> code 10009")


if __name__ == "__main__":
    main()
