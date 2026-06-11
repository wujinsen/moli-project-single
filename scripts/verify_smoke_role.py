#!/usr/bin/env python3
import pymysql

conn = pymysql.connect(
    host="localhost", port=3306, user="root", password="12345678",
    database="moli", charset="utf8mb4",
)
cur = conn.cursor()

cur.execute("SELECT id, role_name FROM sys_role WHERE role_name = 'dict_list_only_smoke'")
role = cur.fetchone()
print("role:", role)
if not role:
    raise SystemExit(1)
role_id = role[0]

cur.execute("SELECT menu_id FROM sys_role_menu WHERE role_id = %s", (role_id,))
print("menu_ids:", [r[0] for r in cur.fetchall()])

cur.execute("SELECT perm_code FROM sys_role_action WHERE role_id = %s", (role_id,))
print("action_codes:", [r[0] for r in cur.fetchall()])

cur.execute(
    """
    SELECT DISTINCT m.perms FROM sys_role_menu rm
    JOIN sys_menu m ON m.id = rm.menu_id
    WHERE rm.role_id = %s AND m.menu_type = 'C' AND m.perms IS NOT NULL AND m.perms != ''
    """,
    (role_id,),
)
page_perms = [r[0] for r in cur.fetchall()]
print("effective_page_perms:", page_perms)

cur.execute(
    """
    SELECT u.user_name FROM sys_user u
    JOIN sys_user_role ur ON ur.user_id = u.id
    WHERE ur.role_id = %s
    """,
    (role_id,),
)
print("users:", [r[0] for r in cur.fetchall()])

assert "system:dict:list" in page_perms, "missing dict list page perm"
cur.execute("SELECT COUNT(*) FROM sys_role_action WHERE role_id = %s", (role_id,))
assert cur.fetchone()[0] == 0, "smoke role should have zero actions"
print("PASS: smoke role has dict:list page only, no actions")

conn.close()
