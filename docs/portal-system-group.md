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
| `platform` | 平台与治理 | 平台管理后台、IAM、配置审计入口 | `moli-admin` |
| `business` | 业务应用 | CRM、电商、会员、客服、用户增长、OA 办公协同 | `crm-demo` |
| `data` | 数据平台 | BI、指标、数仓、大数据开发、数据治理等（不含 AI 应用） | `bi-report` |
| `tech` | 技术类平台 | API 网关/开放平台、低代码、CI/CD、AI Copilot | `api-portal` |
| `ops` | 运维与保障 | 监控、日志、发布、资源 | `moli-ops` |

**默认分组**：未填时后端归一为 `business`。

**展示顺序**（前端固定）：

`platform` → `business` → `data` → `tech` → `ops`

某分组下无系统时不显示该分组标题。

**演示数据：** `scripts/moli.sql` 已含门户演示系统；详见 [portal-system-group-catalog.md](portal-system-group-catalog.md)。

## 3. 数据库

执行（全库快照，含结构 + 数据）：

```bash
mysql -u root -p moli < scripts/moli.sql
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
  'api-portal', 'API 网关 / 开放平台', 'https://api.example.com', 'code', 10, 1, 'EXTERNAL', '/sso/login',
  '技术类平台示例', 'tech'
);
```

## 7. 英文文档

[portal-system-group.en.md](portal-system-group.en.md)
