# Action Permission (Button Permission) Design

Last updated: 2026-06-11  
Status: **Decided (§13 #1)** — other §13 items pending  
Scope: **moli-admin local RBAC** (excludes data-scope permissions and external-system SSO grants)

> This document separates **navigation** from **actions**.  
> See [multi-system-sso-design.en.md](multi-system-sso-design.en.md) for system access via `sys_user_system`.

---

## 1. Background

### 1.1 Current state

- Routes: `sys_menu` (M/C) + `sys_role_menu`
- Permission strings: `sys_menu.perms` → `PermissionService` → Shiro
- Many write APIs still use the same `list` permission
- Menu type `F` (button) is documented but barely used in backend; frontend already excludes F from routes/sidebar
- Role UI checks M/C/F in one tree; no dedicated `permissions` payload for the client

### 1.2 Goals

1. Assignable action permissions (e.g. list without add/delete)
2. **Buttons always visible**; unauthorized **click** shows “无权限操作” (no permission to perform this action)
3. Drop `menu_type=F`; menus are navigation only

### 1.3 Principles

- Decouple navigation (`sys_role_menu`) from actions (`sys_role_action`)
- `perm_code` is the single contract for Shiro, APIs, and client pre-checks
- Server-side enforcement is mandatory; client pre-check is UX only
- Phase 1 pilot: **User management** module

---

## 2. Product behavior

### 2.1 Example: user Zhang San

| Config | Behavior |
|--------|----------|
| Role grants User Management page (C) | Can open list; **Add/Edit/Delete buttons remain visible** |
| No `system:user:add` | Click Add → toast **「无权限操作」**; form does not open |
| Direct `POST /user` | **`code: 10009`**, msg **「无权限操作」** (HTTP 200, see §5.3) |

### 2.2 Effective permissions

```
effectivePermissions =
    perms from authorized C menus (sys_role_menu → sys_menu, menu_type = 'C', non-empty perms)
  ∪ perm_code from sys_role_action
  ∪ { *:*:* } for super-admin
```

Implementation may join `menu_type IN ('M','C')` for legacy data; only **C** rows contribute page perms (see §5.1).

- Page (C) grant implies that page’s `list` perm (on the C row)
- Actions (add/edit/remove…) must be explicitly granted in `sys_role_action`
- Actions without page access must still fail at API level

---

## 3. Deprecating menu type F

**Decision:** `sys_menu` only **M (directory)** and **C (page)**.

| Area | Impact |
|------|--------|
| Sidebar/routes | None (client already filters F) |
| Menu admin | Remove “button” type |
| Role UI | Tree = M/C only; separate action checkboxes |
| Legacy data | Migrate F `perms` → `sys_role_action` if any exist |

Action catalog moves to **`sys_action`** (see §4), not F rows.

---

## 4. Data model

### 4.1 New tables

**`sys_role_action`**

```sql
(role_id, perm_code) PRIMARY KEY
```

**`sys_action`** — registry for assignable actions (UI labels, grouping by `menu_id` on C page)

### 4.2 Naming

`module:resource:action` — e.g. `system:user:add`

### 4.3 Unchanged

- `sys_menu` — M/C navigation only
- `sys_role_menu` — M/C menu IDs only
- `sys_user_role` — unchanged

---

## 5. Backend

### 5.1 `PermissionService`

`getPermissionsByUserId(userId, userName)`:

1. Super-admin → `{ *:*:* }`  
2. Load enabled roles for user  
3. **Page perms** (list, etc.): `sys_role_menu` → `sys_menu`, **`menu_type = 'C'`** and non-empty `perms` (join may use `IN ('M','C')`; effective set matches C rows only, see §2.2)  
4. **Action perms**: `sys_role_action` → `perm_code`, join `sys_action` where `status = 1`  
5. Union and dedupe (multi-role: page and action perms are unioned)

Clear Shiro cache on role changes (existing `PermissionAuthUtils`).

### 5.2 User module API permissions (phase 1)

| Method | Perm |
|--------|------|
| GET list/detail | `system:user:list` |
| POST | `system:user:add` |
| PUT | `system:user:edit` |
| DELETE | `system:user:remove` |
| PUT resetPassword | `system:user:resetPwd` |

#### 5.2.1 Mutating user APIs: dual check (decided §13 #2)

Before `add` / `edit` / `remove` / `resetPwd` etc.:

1. User must have the **action** perm (e.g. `system:user:add`).
2. User must also have **`system:user:list`** (User Management page / list access).

Without `list`, mutating APIs return `10009` even if the action perm alone is present — blocks misconfiguration and direct API abuse without page access.

### 5.3 Unauthorized response (decided §13 #1)

HTTP **200**, business code **`10009`**. Wording depends on path:

| Path | `code` | `msg` |
|------|--------|-------|
| **Shiro `@RequiresPermissions` failure** | `10009` | **「无权限操作」** (`ShiroExceptionHandler` only) |
| **`PrivilegedUserUtils` / controller business checks** | `10009` | **Keep existing detailed messages** (e.g. cannot view this user) |

Do not use HTTP 403 as the business code. Client should handle `10009` for Shiro action denials; business `msg` strings stay as returned.

### 5.4 Role save

```json
{ "id": 1, "menuIds": [10, 11], "actionCodes": ["system:user:add"] }
```

Filter F from `menuIds`; validate `actionCodes` against `sys_action`.

### 5.5 Permissions delivery (decided §13 #3, #6)

Single backend `effectivePermissions` set; client caches `permissions[]` for `assertAction`.

| Role | Source | When |
|------|--------|------|
| **Primary** | `POST /system/enter`, `POST /system/switch` | Explicit enter/switch |
| **Primary (login-equivalent)** | `POST /login` → `LoginVo.permissions` | Portal off, or single INTERNAL auto-enter (no frontend enter call) |
| **Secondary** | `GET /auth/capabilities` | Missing login payload, F5, explicit refresh |

#### Login paths (`LoginVo` ↔ `SystemEnterVo`)

`LoginVo` already has `fullPermission` but **no `permissions` array** yet; extend **both** VOs together.

| Scenario | Backend today | Gap |
|----------|---------------|-----|
| Portal off | `menuVoList` only, no `enterSystem` | No `permissions` |
| Single INTERNAL auto-enter | Internal `enterSystem`, copies only `currentSystem` + `menuVoList` | `permissions` not forwarded to `LoginVo` |
| Multi-system | Empty menu → `/system-select` → enter API | OK after enter |

**P1 backend:** Add `permissions` to `SystemEnterVo` and `LoginVo`; `fillLoginContext` must copy full enter result (including `permissions`, `fullPermission`); portal-off branch must aggregate effective permissions alongside `resolveMenus` (does not call `enterSystem` / `setCurrentSystem`; OK for single INTERNAL today). EXTERNAL enter: `permissions = []`.

**P1 frontend:** `handlePostLogin` → `savePermissions` from `LoginVo` when present; else `ensurePermissionsLoaded()` → capabilities. Same for enter/switch. Do **not** parallel-fetch capabilities after login/enter.

See Chinese doc §5.5 login-path table and §6.6 lifecycle for full detail.

#### Endpoints

- `POST /system/enter`, `/system/switch` → embed `permissions`, `fullPermission`
- `GET /auth/capabilities` → `{ permissions, fullPermission }` (secondary only)
- `GET /role/{id}/auth` → `{ menuIds, actionCodes }`
- `GET /action/list?menuId=` → actions for role UI (optional)

---

## 6. Frontend (meiling-ui)

- Do **not** hide buttons with `v-if` / `v-permission`
- Cache `permissions` from **`LoginVo` (login paths)**, then **`enter`/`switch`**, then **`/auth/capabilities`** as fallback (§5.5)
- `assertAction(code)` on click → toast if missing perm
- Axios `10009` handler: Shiro generic msg → 「无权限操作」; keep business-specific msgs
- Role modal: page tree + action checkboxes per selected C page
- Menu form: M/C only

---

## 7. Phases

| Phase | Work |
|-------|------|
| P1 | Tables, seeds, PermissionService, **`LoginVo`/`SystemEnterVo.permissions`**, `fillLoginContext`, embed in enter/switch, `/auth/capabilities`, user API split, Shiro `10009`, frontend `usePermissions` |
| P2 | Role APIs + UI, remove F |
| P3 | Other modules’ actions |
| P4 | (Optional) action admin CRUD |

**Out of scope:** data permissions, external SSO action grants.

---

## 8. Migration

1. Create tables + seed user actions  
2. If F rows exist: migrate `perms` to `sys_role_action`, clean `sys_role_menu`, remove F menus  
3. Old clients sending F `menuIds`: server ignores them  

---

## 9. Decided (§13)

- Shiro `10009` → 「无权限操作」; business checks keep detailed msg
- Dual check: action perm + `system:user:list` for mutating APIs
- Permissions source: **`LoginVo` / enter / switch primary**; capabilities secondary
- Login-path permissions: extend `LoginVo` + `SystemEnterVo`; `fillLoginContext` must forward full enter payload
- P1 user actions: add/edit/remove/resetPwd; P2 assignRole/assignSystem
- Buttons always visible; click toast on deny

---

## Appendix A: User module action seeds

`system:user:add`, `edit`, `remove`, `resetPwd` — linked to User Management C menu for UI grouping only.

`system:user:list` stays on the C menu `perms` field, not in `sys_role_action`.

---

**After approval**, implement P1 → P2 in `moli-common`, `moli-server`, and `meiling-ui`.
