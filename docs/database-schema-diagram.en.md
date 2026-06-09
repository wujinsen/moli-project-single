# Database Schema Diagram

Last updated: 2026-06-08  
Source: `sql/schema_moli.sql` and `moli-common` entities  
Database: `moli` (utf8mb4)

## 1. Notes

- **18** business tables: **12** system + **6** operations.
- Relationships are **logical only** — no database foreign keys in DDL.
- Primary keys are assigned by the application (`CustomIdGenerator`), not DB auto-increment.
- Keep this file in sync with `sql/schema_moli.sql` when the schema changes.

## 2. Overview

```mermaid
flowchart TB
    subgraph RBAC["System · RBAC & Org"]
        dept[sys_dept]
        user[sys_user]
        role[sys_role]
        menu[sys_menu]
        post[sys_post]
        ur[sys_user_role]
        rm[sys_role_menu]
        up[sys_user_post]
        dept --> user
        user --> ur --> role
        role --> rm --> menu
        user --> up --> post
        dept -.self-ref.-> dept
        menu -.self-ref.-> menu
    end

    subgraph DICT["System · Dictionary"]
        dt[sys_dict_type]
        dd[sys_dict_data]
        dt --> dd
    end

    subgraph LOG["System · Logs"]
        ll[sys_login_log]
        ol[sys_operation_log]
    end

    subgraph OPS["Operations"]
        plat[operation_platform_info]
        srv[operation_server_info]
        proj[operation_project_deploy_info]
        comp[operation_component_deploy_info]
        sp[operation_server_project]
        sc[operation_server_component]
        srv --> proj
        srv --> sp --> proj
        srv --> sc --> comp
    end

    user -.user_name.-> ll
    user -.user_name.-> ol
```

## 3. System · RBAC

```mermaid
erDiagram
    sys_dept {
        bigint id PK
        bigint parent_id
        varchar dept_name
    }

    sys_user {
        bigint id PK
        bigint dept_id FK
        varchar user_name UK
    }

    sys_role {
        bigint id PK
        varchar role_name
    }

    sys_menu {
        bigint id PK
        bigint parent_id
        varchar perms
    }

    sys_post {
        bigint id PK
        varchar post_code
    }

    sys_user_role {
        bigint id PK
        bigint user_id FK
        bigint role_id FK
    }

    sys_role_menu {
        bigint id PK
        bigint role_id FK
        bigint menu_id FK
    }

    sys_user_post {
        bigint id PK
        bigint user_id FK
        bigint post_id FK
    }

    sys_dept ||--o{ sys_dept : "parent_id tree"
    sys_dept ||--o{ sys_user : "dept_id"
    sys_user ||--o{ sys_user_role : "user_id"
    sys_role ||--o{ sys_user_role : "role_id"
    sys_role ||--o{ sys_role_menu : "role_id"
    sys_menu ||--o{ sys_role_menu : "menu_id"
    sys_menu ||--o{ sys_menu : "parent_id tree"
    sys_user ||--o{ sys_user_post : "user_id"
    sys_post ||--o{ sys_user_post : "post_id"
```

**Auth path**: user → `sys_user_role` → role → `sys_role_menu` → menu (`perms` for API/button permissions).

## 4. System · Dictionary

```mermaid
erDiagram
    sys_dict_type {
        bigint id PK
        varchar dict_type UK
    }

    sys_dict_data {
        bigint id PK
        varchar dict_type FK
        varchar dict_key
        varchar dict_value
    }

    sys_dict_type ||--o{ sys_dict_data : "dict_type"
```

Linked by `dict_type` string code, not by numeric `id`.

## 5. System · Logs

```mermaid
erDiagram
    sys_user {
        varchar user_name UK
    }

    sys_login_log {
        bigint id PK
        varchar user_name
        datetime login_time
    }

    sys_operation_log {
        bigint id PK
        varchar user_name
        datetime create_time
    }

    sys_user ||--o{ sys_login_log : "user_name logical"
    sys_user ||--o{ sys_operation_log : "user_name logical"
```

## 6. Operations Module

```mermaid
erDiagram
    operation_server_info {
        bigint id PK
        varchar ip
    }

    operation_project_deploy_info {
        bigint id PK
        bigint server_id FK
        varchar project_name
    }

    operation_component_deploy_info {
        bigint id PK
        varchar component_name
    }

    operation_server_project {
        bigint server_id FK
        bigint project_id FK
    }

    operation_server_component {
        bigint server_id FK
        bigint component_id FK
    }

    operation_platform_info {
        bigint id PK
        varchar platform_name
    }

    operation_server_info ||--o{ operation_project_deploy_info : "server_id"
    operation_server_info ||--o{ operation_server_project : "server_id"
    operation_project_deploy_info ||--o{ operation_server_project : "project_id"
    operation_server_info ||--o{ operation_server_component : "server_id"
    operation_component_deploy_info ||--o{ operation_server_component : "component_id"
```

`operation_platform_info` is standalone. Component deploy info also references servers via `server_ip` (weak string link).

## 7. Table Index

| Table | Description | Module |
|-------|-------------|--------|
| `sys_dept` | Department | System |
| `sys_user` | User | System |
| `sys_role` | Role | System |
| `sys_menu` | Menu | System |
| `sys_post` | Post | System |
| `sys_user_role` | User–Role | System |
| `sys_role_menu` | Role–Menu | System |
| `sys_user_post` | User–Post | System |
| `sys_dict_type` | Dict type | System |
| `sys_dict_data` | Dict data | System |
| `sys_login_log` | Login log | System |
| `sys_operation_log` | Operation log | System |
| `operation_platform_info` | Platform | Ops |
| `operation_server_info` | Server | Ops |
| `operation_project_deploy_info` | Project deploy | Ops |
| `operation_component_deploy_info` | Component deploy | Ops |
| `operation_server_project` | Server–Project | Ops |
| `operation_server_component` | Server–Component | Ops |

## 8. Related Files

- DDL: [`sql/schema_moli.sql`](../sql/schema_moli.sql)
- Entities: `moli-common/src/main/java/com/moli/common/domain/entity/`
- Chinese version: [database-schema-diagram.md](database-schema-diagram.md)
