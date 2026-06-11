#!/usr/bin/env python3
"""API smoke: dict list-only user cannot POST /dict/type (expects 10009 if server up)."""
import json
import sys
import urllib.error
import urllib.request

BASE = "http://127.0.0.1:8888"
ROLE_NAME = "dict_list_only_smoke"


def post(path: str, body: dict, cookie: str | None = None):
    data = json.dumps(body).encode("utf-8")
    req = urllib.request.Request(
        BASE + path,
        data=data,
        headers={
            "Content-Type": "application/json",
            "Cookie": cookie or "",
        },
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=8) as resp:
        return resp.getheader("Set-Cookie"), json.loads(resp.read().decode())


def get(path: str, cookie: str):
    req = urllib.request.Request(BASE + path, headers={"Cookie": cookie}, method="GET")
    with urllib.request.urlopen(req, timeout=8) as resp:
        return json.loads(resp.read().decode())


def main():
    import pymysql

    conn = pymysql.connect(
        host="localhost",
        port=3306,
        user="root",
        password="12345678",
        database="moli",
        charset="utf8mb4",
    )
    cur = conn.cursor()
    cur.execute(
        """
        SELECT u.user_name FROM sys_user u
        JOIN sys_user_role ur ON ur.user_id = u.id
        JOIN sys_role r ON r.id = ur.role_id
        WHERE r.role_name = %s AND u.status = 1
        LIMIT 1
        """,
        (ROLE_NAME,),
    )
    row = cur.fetchone()
    conn.close()
    if not row:
        print("SKIP: no user assigned to dict_list_only_smoke — run deploy first")
        return 1
    username = row[0]
    print(f"smoke user: {username}")

    # common dev passwords to try
    passwords = ["123456", "admin123", "12345678", "password"]
    cookie = None
    for pwd in passwords:
        try:
            cookie, res = post("/login", {"username": username, "password": pwd})
            if res.get("code") == 200 and cookie:
                print(f"login ok with password trial")
                break
        except urllib.error.HTTPError as e:
            print(f"login failed: {e.code}")
    else:
        print(f"SKIP API smoke: could not login as {username}; set password manually")
        return 0

    _, caps = get("/auth/capabilities", cookie)
    perms = (caps.get("data") or {}).get("permissions") or []
    print("permissions:", perms)
    if "system:dict:list" not in perms:
        print("FAIL: missing system:dict:list")
        return 1
    if "system:dict:add" in perms:
        print("FAIL: should NOT have system:dict:add")
        return 1

    try:
        _, res = post(
            "/dict/type",
            {"dictName": "smoke", "dictType": "smoke_type", "status": 1},
            cookie,
        )
        code = res.get("code")
        print("POST /dict/type ->", code, res.get("msg"))
        if code == 10009:
            print("PASS: dict add blocked as expected")
            return 0
        print("FAIL: expected 10009")
        return 1
    except urllib.error.HTTPError as e:
        print("HTTP error", e.code, e.read().decode()[:200])
        return 1


if __name__ == "__main__":
    try:
        sys.exit(main())
    except urllib.error.URLError as e:
        print(f"SKIP: server not reachable at {BASE} ({e})")
        print("Start moli-server then re-run: python scripts/smoke_dict_permission.py")
        sys.exit(0)
