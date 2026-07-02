# Moli 数据库基线 SQL

> **新环境请用 [`scripts/moli.sql`](../../scripts/moli.sql)**（全库快照：结构 + 数据）。  
> 早期拆分脚本 `00_schema.sql` / `01_baseline_data.sql` 已删除（被 `scripts/moli.sql` 取代）。

## 新环境初始化

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS moli DEFAULT CHARSET utf8mb4;"
mysql -u root -p moli < scripts/moli.sql
```

## 表行数（导出时快照）

| 表 | 行数 | 纳入数据 |
|----|------|----------|
| `operation_component_deploy_info` | 8 | 是 |
| `operation_platform_info` | 6 | 是 |
| `operation_project_deploy_info` | 6 | 是 |
| `operation_server_component` | 10 | 是 |
| `operation_server_info` | 6 | 是 |
| `operation_server_project` | 6 | 是 |
| `sys_action` | 40 | 是 |
| `sys_dept` | 34 | 是 |
| `sys_dict_data` | 35 | 是 |
| `sys_dict_type` | 12 | 是 |
| `sys_login_log` | 39 | 否（审计表） |
| `sys_menu` | 31 | 是 |
| `sys_operation_log` | 166 | 否（审计表） |
| `sys_post` | 39 | 是 |
| `sys_role` | 10 | 是 |
| `sys_role_action` | 56 | 是 |
| `sys_role_menu` | 45 | 是 |
| `sys_system` | 35 | 是 |
| `sys_user` | 33 | 是 |
| `sys_user_post` | 1 | 是 |
| `sys_user_role` | 31 | 是 |
| `sys_user_system` | 70 | 是 |

历史增量脚本（`patch_*.sql`、`migrate_sys_action.sql`）已合并进 `scripts/moli.sql`，新环境无需再执行旧 patch。
