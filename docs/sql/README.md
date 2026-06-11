# Moli 数据库基线 SQL

自本地 `moli` 库导出（`application-dev.yml`），日期：2026-06-11。

## 文件说明

| 文件 | 说明 |
|------|------|
| `00_schema.sql` | 全库表结构（22 张表） |
| `01_baseline_data.sql` | 基线种子数据（不含登录/操作日志） |

## 新环境初始化

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS moli DEFAULT CHARSET utf8mb4;"
mysql -u root -p moli < docs/sql/00_schema.sql
mysql -u root -p moli < docs/sql/01_baseline_data.sql
```

无 `mysql` 客户端时可用：`python scripts/export_db_baseline.py` 重新导出。

## 表行数（导出时快照）

| 表 | 行数 | 纳入数据 |
|----|------|----------|
| `operation_component_deploy_info` | 8 | 是 |
| `operation_platform_info` | 6 | 是 |
| `operation_project_deploy_info` | 6 | 是 |
| `operation_server_component` | 10 | 是 |
| `operation_server_info` | 6 | 是 |
| `operation_server_project` | 6 | 是 |
| `sys_action` | 38 | 是 |
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
| `sys_system` | 38 | 是 |
| `sys_user` | 33 | 是 |
| `sys_user_post` | 1 | 是 |
| `sys_user_role` | 31 | 是 |
| `sys_user_system` | 72 | 是 |

历史增量脚本（`patch_*.sql`、`migrate_sys_action.sql`）已合并进本基线，新环境无需再执行旧 patch。
