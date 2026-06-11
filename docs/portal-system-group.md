# SSO 门户系统分组（选系统页）

最后更新: 2026-06-11  
适用范围: `sys_system.system_group` + 前端 `SystemSelectView` 分组展示

## 1. 背景

登录后选系统页（`/system-select`）原先是平铺卡片。系统增多后按 **大厂业务域** 分组展示，与侧栏 `sys_menu` 分组（身份与门户 / 系统管理等）相互独立：

| 层级 | 作用 |
|------|------|
| `sys_system.system_group` | 门户：选 **进哪个业务系统** |
| `sys_menu` 树 | 进入 moli-admin 后：侧栏 **菜单** |

## 2. 分组枚举

**各分类下常见系统清单（含当前已注册 3 个）：** [portal-system-group-catalog.md](portal-system-group-catalog.md)

| `system_group` | 中文 | 说明 | 示例 |
|----------------|------|------|------|
| `governance` | 管理与治理 | 管理后台、IAM、配置审计入口 | `moli-admin` |
| `business` | 业务应用 | CRM、ERP、订单、营销 | `crm-demo` |
| `ai` | AI 应用 | 大模型助手、Copilot、智能体、AIGC 工具 | ChatGPT、内部 AI 门户 |
| `tech` | 技术类平台 | 开放平台、API 网关、低代码、CI/CD | （待注册） |
| `ops` | 运维与保障 | 监控、日志、发布、资源 | `moli-ops` |
| `data` | 数据平台 | BI、指标、数仓、Flink、数据治理等大数据平台（不含 AI 应用） | 见 `seed_sys_system_portal_demo.sql` |
| `office` | 办公协同 | OA、邮件、日程 | （可选） |

**默认分组**：未填时后端归一为 `business`。

**展示顺序**（前端固定）：

`governance` → `business` → `ai` → `tech` → `ops` → `data` → `office`

某分组下无系统时不显示该分组标题。

**演示数据：** 执行 `sql/seed_sys_system_portal_demo.sql` 可注册 7 个系统覆盖全部分组，详见 [portal-system-group-catalog.md](portal-system-group-catalog.md)。

## 3. 数据库

执行：

```bash
mysql -u root -p moli < sql/patch_sys_system_group.sql
```

新增列：

```sql
ALTER TABLE sys_system ADD COLUMN system_group VARCHAR(32) DEFAULT 'business' COMMENT '门户分组';
```

## 4. API

### `GET /system/my`

返回 `List<SystemVo>`，每项增加：

| 字段 | 类型 | 说明 |
|------|------|------|
| `systemGroup` | string | 分组码，见上表 |

### `POST/PUT /system`（系统注册）

请求体 `SysSystem` 增加 `systemGroup`，合法值见枚举；非法值保存时归为 `business`。

### `GET /system/list`（管理列表）

支持 query 参数 `systemGroup` 筛选。

## 5. 前端改造（meiling-ui）

**前端同学请直接读：**

👉 `meiling-ui/docs/portal-system-group-ui.md`

（本文件保留 API / 枚举 / SQL；UI 改造要点、验收清单在专用文档里。）

## 6. 注册新系统示例

```sql
INSERT INTO sys_system (
  id, create_id, create_time, update_id, update_time,
  system_code, system_name, base_url, icon, sort, status, sso_mode, entry_path, remark, system_group
) VALUES (
  (SELECT COALESCE(MAX(id),0)+1 FROM sys_system s),
  1, NOW(), 1, NOW(),
  'api-portal', 'API 开放平台', 'https://api.example.com', 'code', 10, 1, 'EXTERNAL', '/sso/login',
  '技术类平台示例', 'tech'
);
```

## 7. 英文文档

[portal-system-group.en.md](portal-system-group.en.md)
