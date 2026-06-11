import pymysql

c = pymysql.connect(host="localhost", port=3306, user="root", password="12345678", database="moli", charset="utf8mb4")
cur = c.cursor()
cur.execute(
    "SELECT u.id, u.user_name, u.dept_id, d.dept_name "
    "FROM sys_user u LEFT JOIN sys_dept d ON d.id = u.dept_id "
    "WHERE u.is_delete = 0 AND (u.user_name IN ('superadmin','admin') OR u.user_name LIKE %s)",
    ("%super%",),
)
rows = cur.fetchall()
print("privileged-like users (%d):" % len(rows))
for r in rows:
    print(" ", r)
c.close()
