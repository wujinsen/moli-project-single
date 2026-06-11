#!/usr/bin/env python3
"""Export moli DB schema + baseline seed from application-dev.yml connection."""
from __future__ import annotations

import argparse
from datetime import date
from pathlib import Path

import pymysql
from pymysql.cursors import DictCursor

ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = ROOT / "docs" / "sql"

DB = dict(
    host="localhost",
    port=3306,
    user="root",
    password="12345678",
    database="moli",
    charset="utf8mb4",
)

# Audit tables are excluded from baseline data (fresh env starts empty).
SKIP_DATA_TABLES = frozenset({"sys_login_log", "sys_operation_log"})


def connect():
    return pymysql.connect(**DB, cursorclass=DictCursor)


def list_tables(conn) -> list[str]:
    cur = conn.cursor()
    cur.execute("SHOW TABLES")
    key = f"Tables_in_{DB['database']}"
    return sorted(row[key] for row in cur.fetchall())


def export_schema(conn, tables: list[str]) -> str:
    lines = [
        f"-- Moli database schema (exported {date.today().isoformat()})",
        f"-- Database: {DB['database']}",
        "-- Run before 01_baseline_data.sql on empty database.",
        "",
        "SET NAMES utf8mb4;",
        "SET FOREIGN_KEY_CHECKS = 0;",
        "",
    ]
    cur = conn.cursor()
    for table in tables:
        cur.execute(f"SHOW CREATE TABLE `{table}`")
        row = cur.fetchone()
        ddl = row["Create Table"]
        lines.append(f"DROP TABLE IF EXISTS `{table}`;")
        lines.append(f"{ddl};")
        lines.append("")
    lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    lines.append("")
    return "\n".join(lines)


def sql_literal(value) -> str:
    if value is None:
        return "NULL"
    if isinstance(value, (int, float)):
        return str(value)
    if isinstance(value, (bytes, bytearray)):
        return "0x" + value.hex()
    escaped = (
        str(value)
        .replace("\\", "\\\\")
        .replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\x00", "\\0")
    )
    return f"'{escaped}'"


def export_data(conn, tables: list[str]) -> str:
    lines = [
        f"-- Moli baseline seed data (exported {date.today().isoformat()})",
        f"-- Database: {DB['database']}",
        "-- Excludes audit tables: sys_login_log, sys_operation_log",
        "-- Prerequisite: 00_schema.sql",
        "",
        "SET NAMES utf8mb4;",
        "SET FOREIGN_KEY_CHECKS = 0;",
        "",
    ]
    cur = conn.cursor()
    for table in tables:
        if table in SKIP_DATA_TABLES:
            continue
        cur.execute(f"SELECT * FROM `{table}` ORDER BY 1")
        rows = cur.fetchall()
        if not rows:
            continue
        columns = list(rows[0].keys())
        col_list = ", ".join(f"`{c}`" for c in columns)
        lines.append(f"-- {table}: {len(rows)} rows")
        for row in rows:
            values = ", ".join(sql_literal(row[c]) for c in columns)
            lines.append(f"INSERT INTO `{table}` ({col_list}) VALUES ({values});")
        lines.append("")
    lines.append("SET FOREIGN_KEY_CHECKS = 1;")
    lines.append("")
    return "\n".join(lines)


def write_readme(tables: list[str], conn) -> str:
    cur = conn.cursor()
    lines = [
        "# Moli 数据库基线 SQL",
        "",
        f"自本地 `moli` 库导出（`application-dev.yml`），日期：{date.today().isoformat()}。",
        "",
        "## 文件说明",
        "",
        "| 文件 | 说明 |",
        "|------|------|",
        "| `00_schema.sql` | 全库表结构（22 张表） |",
        "| `01_baseline_data.sql` | 基线种子数据（不含登录/操作日志） |",
        "",
        "## 新环境初始化",
        "",
        "```bash",
        "mysql -u root -p -e \"CREATE DATABASE IF NOT EXISTS moli DEFAULT CHARSET utf8mb4;\"",
        "mysql -u root -p moli < docs/sql/00_schema.sql",
        "mysql -u root -p moli < docs/sql/01_baseline_data.sql",
        "```",
        "",
        "无 `mysql` 客户端时可用：`python scripts/export_db_baseline.py` 重新导出。",
        "",
        "## 表行数（导出时快照）",
        "",
        "| 表 | 行数 | 纳入数据 |",
        "|----|------|----------|",
    ]
    for table in tables:
        cur.execute(f"SELECT COUNT(*) AS c FROM `{table}`")
        count = cur.fetchone()["c"]
        included = "否（审计表）" if table in SKIP_DATA_TABLES else "是"
        lines.append(f"| `{table}` | {count} | {included} |")
    lines.append("")
    lines.append(
        "历史增量脚本（`patch_*.sql`、`migrate_sys_action.sql`）已合并进本基线，"
        "新环境无需再执行旧 patch。"
    )
    lines.append("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser(description="Export moli baseline SQL")
    parser.add_argument(
        "--out-dir",
        type=Path,
        default=OUT_DIR,
        help="Output directory (default: docs/sql)",
    )
    args = parser.parse_args()
    out_dir: Path = args.out_dir
    out_dir.mkdir(parents=True, exist_ok=True)

    conn = connect()
    try:
        tables = list_tables(conn)
        schema_path = out_dir / "00_schema.sql"
        data_path = out_dir / "01_baseline_data.sql"
        readme_path = out_dir / "README.md"

        schema_path.write_text(export_schema(conn, tables), encoding="utf-8")
        data_path.write_text(export_data(conn, tables), encoding="utf-8")
        readme_path.write_text(write_readme(tables, conn), encoding="utf-8")

        print(f"Exported {len(tables)} tables")
        print(f"  {schema_path}")
        print(f"  {data_path}")
        print(f"  {readme_path}")
        return 0
    finally:
        conn.close()


if __name__ == "__main__":
    raise SystemExit(main())
