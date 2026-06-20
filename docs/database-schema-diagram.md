# 数据库表关系图

最后更新: 2026-06-12  
数据来源: `docs/sql/00_schema.sql` 与 `moli-common` 实体类  
数据库名: `moli`（utf8mb4）

## 1. 说明

- 共 **22** 张业务表：系统模块 **16** 张（含动作权限 **2** 张、多系统 SSO **2** 张）、运维模块 **6** 张。
- 表间为 **逻辑关联**，DDL 中 **未声明数据库外键**（`FOREIGN_KEY_CHECKS = 0`），由应用层维护一致性。
- 主键 `id` 多数由应用侧 `CustomIdGenerator` 赋值；**例外**：`sys_action.id` 为数据库 **AUTO_INCREMENT**。
- 权限模型：页面权限仍在 `sys_menu.perms`（C 菜单）；动作权限在 `sys_action` + `sys_role_action`（见 [action-permission-design.md](action-permission-design.md)）。
- 结构变更时请同步更新本文件，并执行 `python scripts/export_db_baseline.py` 重导出 `docs/sql/`。

## 2. 总览

```mermaid
flowchart TB
    subgraph RBAC["系统 · 权限与组织"]
        dept[sys_dept]
        user[sys_user]
        role[sys_role]
        menu[sys_menu]
        post[sys_post]
        ur[sys_user_role]
        rm[sys_role_menu]
        sa[sys_action]
        ra[sys_role_action]
        up[sys_user_post]
        dept --> user
        user --> ur --> role
        role --> rm --> menu
        role --> ra --> sa
        sa -.menu_id.-> menu
        user --> up --> post
        dept -.自关联.-> dept
        menu -.自关联.-> menu
    end

    subgraph DICT["系统 · 字典"]
        dt[sys_dict_type]
        dd[sys_dict_data]
        dt --> dd
    end

    subgraph LOG["系统 · 日志"]
        ll[sys_login_log]
        ol[sys_operation_log]
    end

    subgraph OPS["运维模块"]
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

    subgraph SSO["多系统 SSO"]
        sys[sys_system]
        us[sys_user_system]
        user --> us --> sys
    end

    user -.用户名.-> ll
    user -.用户名.-> ol
```

## 3. 多系统 SSO（新增 2 表）

多系统门户 **只新增下面 2 张表**；Ticket 存 **Redis**，不落库。本系统内角色菜单仍用既有 RBAC 表（`sys_user_role` 等），**未新增** `system_id` 字段。

```mermaid
erDiagram
    sys_user {
        bigint id PK "主键"
        varchar user_name UK "用户名"
    }

    sys_system {
        bigint id PK "主键"
        varchar system_code UK "系统编码 如 moli-admin"
        varchar system_name "显示名称"
        varchar base_url "访问地址"
        varchar sso_mode "INTERNAL 本项目 / EXTERNAL 外链"
        varchar entry_path "SSO 入口 默认 /sso/login"
        int status "1启用 0停用"
        int sort "排序"
    }

    sys_user_system {
        bigint id PK "主键"
        bigint user_id FK "用户ID"
        bigint system_id FK "系统ID"
        int is_default "1默认系统"
    }

    sys_user ||--o{ sys_user_system : "user_id"
    sys_system ||--o{ sys_user_system : "system_id"
```

| 表 | 作用 |
|----|------|
| `sys_system` | 登记可进入的**业务系统**（本项目 `moli-admin`、CRM 等）；含跳转 URL、SSO 模式 |
| `sys_user_system` | 用户**能进哪些系统**（系统准入）；与 `sys_user_role`（本系统内能干什么）分开 |

**与现有表的关系**

| 已有表 | 与 SSO 的关系 |
|--------|----------------|
| `sys_user` | `sys_user_system.user_id` → 谁被分配了哪些系统 |
| `sys_user_role` / `sys_role_menu` / `sys_menu` | **不变**；管 moli-admin **内部**页面导航 |
| `sys_action` / `sys_role_action` | **新增**；管写操作动作（add/edit/remove 等），与 C 菜单 `list` 并集下发 |
| `operation_platform_info` | **无关**；运维外部平台账号，不是 SSO 业务系统 |

DDL：已合并进 `docs/sql/00_schema.sql`（含 `sys_system` / `sys_user_system`）。

## 4. 系统模块 · 权限与组织（RBAC）

```mermaid
erDiagram
    sys_dept {
        bigint id PK "主键"
        bigint parent_id "父部门ID"
        varchar dept_name "部门名称"
        int status "1正常 0停用"
    }

    sys_user {
        bigint id PK "主键"
        bigint dept_id FK "部门ID"
        varchar user_name UK "用户名"
        varchar telephone UK "电话"
        int is_delete "逻辑删除"
    }

    sys_role {
        bigint id PK "主键"
        varchar role_name "角色名称"
        int status "1正常 0停用"
    }

    sys_menu {
        bigint id PK "主键"
        bigint parent_id "父菜单ID"
        varchar menu_type "M目录 C页面（F已废弃）"
        varchar perms "页面 list 权限标识"
    }

    sys_action {
        bigint id PK "自增"
        varchar perm_code UK "如 system:user:add"
        varchar resource "资源 user/role/..."
        varchar action "add/edit/remove/..."
        bigint menu_id "关联 C 页面 UI 分组"
        tinyint status "1启用 0停用"
    }

    sys_role_action {
        bigint role_id PK "角色ID"
        varchar perm_code PK "动作权限码"
    }

    sys_post {
        bigint id PK "主键"
        varchar post_code "岗位编码"
        varchar post_name "岗位名称"
    }

    sys_user_role {
        bigint id PK "主键"
        bigint user_id FK "用户ID"
        bigint role_id FK "角色ID"
    }

    sys_role_menu {
        bigint id PK "主键"
        bigint role_id FK "角色ID"
        bigint menu_id FK "菜单ID"
    }

    sys_user_post {
        bigint id PK "主键"
        bigint user_id FK "用户ID"
        bigint post_id FK "岗位ID"
    }

    sys_dept ||--o{ sys_dept : "parent_id 树形"
    sys_dept ||--o{ sys_user : "dept_id"
    sys_user ||--o{ sys_user_role : "user_id"
    sys_role ||--o{ sys_user_role : "role_id"
    sys_role ||--o{ sys_role_menu : "role_id"
    sys_menu ||--o{ sys_role_menu : "menu_id"
    sys_role ||--o{ sys_role_action : "role_id"
    sys_action ||--o{ sys_role_action : "perm_code"
    sys_menu ||--o{ sys_action : "menu_id"
    sys_menu ||--o{ sys_menu : "parent_id 树形"
    sys_user ||--o{ sys_user_post : "user_id"
    sys_post ||--o{ sys_user_post : "post_id"
```

**关系摘要**

| 关系 | 类型 | 关联字段 | 业务含义 |
|------|------|----------|----------|
| 部门 ↔ 部门 | 1:N 树 | `sys_dept.parent_id` → `sys_dept.id` | 组织架构层级 |
| 部门 → 用户 | 1:N | `sys_user.dept_id` → `sys_dept.id` | 用户归属部门 |
| 用户 ↔ 角色 | M:N | `sys_user_role` | 一个用户可多角色 |
| 角色 ↔ 菜单 | M:N | `sys_role_menu` | 角色授权菜单/按钮 |
| 菜单 ↔ 菜单 | 1:N 树 | `sys_menu.parent_id` → `sys_menu.id` | 目录/菜单/按钮树 |
| 用户 ↔ 岗位 | M:N | `sys_user_post` | 用户兼任岗位 |

**鉴权链路**: 用户 → `sys_user_role` → 角色 → `sys_role_menu` → 菜单（`perms` 为接口/按钮权限标识）。

## 5. 系统模块 · 字典

```mermaid
erDiagram
    sys_dict_type {
        bigint id PK "主键"
        varchar dict_type UK "字典类型编码"
        varchar dict_name "字典名称"
    }

    sys_dict_data {
        bigint id PK "主键"
        varchar dict_type FK "字典类型编码"
        varchar dict_key "字典键"
        varchar dict_value "字典值"
    }

    sys_dict_type ||--o{ sys_dict_data : "dict_type"
```

`sys_dict_data.dict_type` 与 `sys_dict_type.dict_type` 通过 **字符串编码** 关联，非 `id` 外键。

## 6. 系统模块 · 日志

```mermaid
erDiagram
    sys_user {
        varchar user_name UK "用户名"
    }

    sys_login_log {
        bigint id PK "主键"
        varchar user_name "用户名"
        varchar ip_address "IP"
        datetime login_time "登录时间"
    }

    sys_operation_log {
        bigint id PK "主键"
        varchar user_name "操作人"
        varchar request_url "请求URL"
        datetime create_time "操作时间"
    }

    sys_user ||--o{ sys_login_log : "user_name 逻辑关联"
    sys_user ||--o{ sys_operation_log : "user_name 逻辑关联"
```

- `sys_login_log`：登录成功/失败时由 `LoginController` 写入。
- `sys_operation_log`：由 AOP 切面记录接口操作，与用户表无物理外键。

## 7. 运维模块

```mermaid
erDiagram
    operation_platform_info {
        bigint id PK "主键"
        varchar platform_name "平台名称"
        int environment "1dev 2test 3pre 4pro"
    }

    operation_server_info {
        bigint id PK "主键"
        varchar server_name "服务器名"
        varchar ip "公网IP"
        varchar inner_ip "内网IP"
    }

    operation_project_deploy_info {
        bigint id PK "主键"
        bigint server_id FK "服务器ID"
        varchar project_name "项目名称"
        varchar deploy_path "部署路径"
    }

    operation_component_deploy_info {
        bigint id PK "主键"
        varchar component_name "组件名"
        varchar server_ip "服务器IP"
    }

    operation_server_project {
        bigint id PK "主键"
        bigint server_id FK "服务器ID"
        bigint project_id FK "项目ID"
    }

    operation_server_component {
        bigint id PK "主键"
        bigint server_id FK "服务器ID"
        bigint component_id FK "组件ID"
    }

    operation_server_info ||--o{ operation_project_deploy_info : "server_id"
    operation_server_info ||--o{ operation_server_project : "server_id"
    operation_project_deploy_info ||--o{ operation_server_project : "project_id"
    operation_server_info ||--o{ operation_server_component : "server_id"
    operation_component_deploy_info ||--o{ operation_server_component : "component_id"
```

**关系摘要**

| 关系 | 类型 | 关联字段 | 说明 |
|------|------|----------|------|
| 服务器 → 项目部署 | 1:N | `operation_project_deploy_info.server_id` | 项目落在哪台服务器 |
| 服务器 ↔ 项目 | M:N | `operation_server_project` | 多对多关联表 |
| 服务器 ↔ 组件 | M:N | `operation_server_component` | 组件部署关联 |
| 平台信息 | 独立 | — | `operation_platform_info` 无外键关联 |

`operation_component_deploy_info` 通过 `server_ip` 与服务器做 **弱关联**（字符串 IP），未使用 `server_id`。

## 8. 表清单

| 表名 | 中文名 | 模块 |
|------|--------|------|
| `sys_system` | 业务系统注册 | 多系统 SSO |
| `sys_user_system` | 用户-系统准入 | 多系统 SSO |
| `sys_dept` | 部门 | 系统 |
| `sys_user` | 用户 | 系统 |
| `sys_role` | 角色 | 系统 |
| `sys_menu` | 菜单（M/C；F 已废弃） | 系统 |
| `sys_action` | 动作目录 | 系统 · 权限 |
| `sys_role_action` | 角色-动作 | 系统 · 权限 |
| `sys_post` | 岗位 | 系统 |
| `sys_user_role` | 用户-角色 | 系统 |
| `sys_role_menu` | 角色-菜单 | 系统 |
| `sys_user_post` | 用户-岗位 | 系统 |
| `sys_dict_type` | 字典类型 | 系统 |
| `sys_dict_data` | 字典数据 | 系统 |
| `sys_login_log` | 登录日志 | 系统 |
| `sys_operation_log` | 操作日志 | 系统 |
| `operation_platform_info` | 运营平台 | 运维 |
| `operation_server_info` | 服务器 | 运维 |
| `operation_project_deploy_info` | 项目部署 | 运维 |
| `operation_component_deploy_info` | 组件部署 | 运维 |
| `operation_server_project` | 服务器-项目 | 运维 |
| `operation_server_component` | 服务器-组件 | 运维 |

## 9. 相关文件

- DDL + 种子: [`docs/sql/00_schema.sql`](sql/00_schema.sql)、[`docs/sql/01_baseline_data.sql`](sql/01_baseline_data.sql)
- 实体类: `moli-common/src/main/java/com/moli/common/domain/entity/`
- 英文版: [database-schema-diagram.en.md](database-schema-diagram.en.md)
