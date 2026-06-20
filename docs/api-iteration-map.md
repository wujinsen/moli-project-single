# 后台接口迭代地图

最后更新: 2026-06-12  
适用范围: `moli-server` 当前代码中的 Controller 接口

## 1. 基本结论

- 控制器数量: 16（含 `ActionController`、`AuthController`、`SystemController`、`SsoController`）
- HTTP 接口数量: 约 100+（`GET/POST/PUT/DELETE` 注解统计）
- 统一返回: `MoliResult<T>`
- 分页返回: `PageRes<T>`
- 鉴权方式: Shiro Session（token 实际为 sessionId）
- 细粒度权限: 页面 `sys_menu.perms`（如 `system:user:list`）+ 动作 `sys_role_action`（如 `system:user:add`）；`PermissionService` 并集后供 Shiro 与 `GET /auth/capabilities`
- 超级管理员: `superadmin` / `admin` 拥有 `*:*:*`，可进入任意已注册系统（含停用），进入 INTERNAL 系统时返回全部菜单；多系统时与普通用户一样需先选系统（仅 1 个 INTERNAL 时登录自动进入）
- 无权限响应: HTTP 200 + `code=10009`；Shiro 鉴权失败 `msg=无权限操作`；业务层校验保留细分文案
- 全局鉴权策略:
  - 放行: `/login`、`/sso/validate`、Swagger 相关
  - 其余路径: `authc` 拦截（未登录返回 token 失效 JSON）

## 2. 认证与会话

### `LoginController`（前缀为空）

- `POST /login`
  - 入参: `SysUser`（JSON body）
  - 出参: `MoliResult<LoginVo>`（token + user + `fullPermission` + `systemPortalEnabled` + 可选 `systemList` / `currentSystem` / `menuVoList`）
  - 出参含 `permissions: string[]`（门户关闭或单 INTERNAL 自动进入时与 `menuVoList` 一并下发）
  - 说明: 启用 `sso.enabled` 且存在 `sys_system` 时返回系统门户；**门户关闭**时直出 `menuVoList` + `permissions`；仅 1 个 INTERNAL 系统时 `fillLoginContext` 完整拷贝 `currentSystem`、`menuVoList`、`permissions`、`fullPermission`；多系统时 `menuVoList` 为空，需调 `/system/enter` 或走选系统页
- `POST /logout`
  - 入参: 无
  - 出参: `MoliResult`
- `POST /captchaImage`
  - 入参: 无
  - 出参: `MoliResult<CaptchaImageVo>`
  - 现状: 支持开关控制（`captcha.enabled`），关闭时返回“验证码功能暂时关闭”

### `SystemController`（前缀 `/system`）

- `GET /system/my`：当前用户可访问系统；`SystemVo` 含 `systemGroup`（门户分组，见 `docs/portal-system-group.md`）
- `POST /system/enter`、`POST /system/switch`：进入/切换系统（同一 Session）；INTERNAL 返回 `menuVoList`；EXTERNAL 返回 `redirectUrl`
  - `SystemEnterVo` 含 `permissions`、`fullPermission`（与 `LoginVo` 对齐）
- `GET /auth/capabilities`：当前系统上下文 `{ permissions, fullPermission }`；F5 / 缓存缺失时补拉
- `GET /action/list?menuId=`：页面可分配动作（角色授权 UI）
- **P4 动作目录**：`GET /action/page`、`GET /action/{id}`、`POST/PUT/DELETE /action`、`PUT /action/changeStatus`；权限与菜单管理一致（读 `system:menu:list`，写 `system:menu:edit` + `list`）
- `GET/POST/PUT/DELETE /system`：系统注册维护（权限 `system:system:list`；增删改另需 `superadmin`/`admin`）；`SysSystem.systemGroup` 支持 `governance`/`business`/`ai`/`tech`/`ops`/`data`/`office`；`GET /system/list` 可按 `systemGroup` 筛选
- 系统注册菜单、分组字段已含于 `docs/sql` 基线（`00_schema.sql` + `01_baseline_data.sql`）

### `SsoController`（前缀 `/sso`）

- `POST /sso/validate`：子系统校验 Ticket（匿名；可配请求头 `X-Sso-Secret`）；响应含 `fullPermission`（超管为 `true`，外部系统可据此授予本地最大权限）

### `ChatGPTController`（前缀 `chatgpt`）

- `GET /chatgpt/v1/createCompletion`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`
- `GET /chatgpt/v1/createCompletionTurbo`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`

## 3. 系统管理域接口

### 用户管理 `UserController`（前缀 `/user`）

- `GET /user/list`：分页用户列表（`UserVo` 查询参数）；`superadmin`（最大权限）与 `admin`（特殊管理员）对外隐藏，仅特殊账号登录时可见；**未选部门**时特殊账号可见，**按部门筛选**时仅展示 `dept_id` 落在该部门树内的用户（无部门归属的特殊账号不会出现于各部门子列表）
- 用户查询/删除/改状态/重置密码等：非特殊账号访问 `superadmin`/`admin` 返回无权限（`10009`）
- `POST /user`：新增用户；权限 `system:user:add` + `system:user:list`
- `PUT /user`：更新用户；**本人**仅可改昵称/联系方式等个人信息（仅需登录）；改他人需 `system:user:edit` + `system:user:list`
- `GET /user/{id}`：查询用户
- `GET /user/getUserDetail/{id}`：查询用户详情（含 postIds）
- `GET /user/profile`：当前登录用户信息（仅需登录）
- `PUT /user/language`：更新界面语言（仅需登录）
- `DELETE /user/{userIds}`：批量删除；权限 `system:user:remove` + `system:user:list`
- `PUT /user/changeStatus`：用户启停；权限 `system:user:edit` + `system:user:list`
- `GET /user/getRoleByUserId/{userId}`：用户角色信息
- `PUT /user/insertUserRole`：重设用户角色；权限 `system:user:assignRole` + `system:user:list`
- `PUT /user/addUserRole`：给角色新增用户；成功后 `msg` 提示刷新页面；写入操作日志
- `GET /user/getUserByRole`：查询角色下用户
- `PUT /user/removeUsers`：移除角色下用户；成功后 `msg` 提示刷新页面；写入操作日志
- `GET /user/unauthorizedUsers`：角色未授权用户列表
- `PUT /user/resetPassword`：重置密码；**本人**可改自己密码（仅需登录，可选传 `oldPassword` 校验）；改他人需 `system:user:resetPwd` + `system:user:list`
- `GET /user/getSystemByUserId/{userId}`：用户已授权系统
- `PUT /user/insertUserSystem`：保存用户可访问系统；权限 `system:user:assignSystem` + `system:user:list`
- `GET /user/getSystemByUserId/{userId}`：超管目标用户 `systemIds` 为全部系统，`systemList` 含停用系统
- `GET /user/getUserBySystem`：按系统查已授权用户（query：`systemId`、分页、`userName`/`telephone`）；含 `sys_user_system` 关联及超管
- `GET /user/unauthorizedUsersBySystem`：按系统查未授权用户（参数同上；排除已授权与超管）
- 侧栏「系统用户分配」菜单、`多系统/审计日志` 分组已含于 `docs/sql` 基线

### 角色管理 `RoleController`（前缀 `/role`）

- `GET /role/list`：分页列表；权限 `system:role:list`
- `POST /role`：新增角色（`menuIds` + `actionCodes`）；权限 `system:role:add` + `system:role:list`
- `PUT /role`：更新角色；**保存 `menuIds`/`actionCodes`** 需 `system:role:assignPerm` + `list`；**仅改角色字段**需 `system:role:edit` + `list`；强制「有动作必先有页面」
- `GET /role/{id}/auth`：授权回显 `{ menuIds, actionCodes }`；权限 `system:role:list`
- `GET /role/{id}`：查询角色；权限 `system:role:list`
- `DELETE /role/{ids}`：删除角色（含关系）；权限 `system:role:remove` + `system:role:list`
- `PUT /role/changeStatus`：角色状态变更；权限 `system:role:edit` + `system:role:list`
- `GET /role/getRoleAll`：获取有效角色列表；权限 `system:role:list`
- 用户模块 `PUT /user/addUserRole`、`PUT /user/removeUsers`：权限 `system:role:assignUser` + `system:role:list`

### 菜单管理 `MenuController`（前缀 `/menu`，8个）

- `GET /menu/getRouters`：当前用户菜单树
- `GET /menu/list`：菜单列表（`menuName`、`status`）；权限 `system:menu:list`
- `POST /menu`：新增菜单；权限 `system:menu:add` + `system:menu:list`
- `PUT /menu`：更新菜单；权限 `system:menu:edit` + `system:menu:list`
- `GET /menu/{id}`：菜单详情；权限 `system:menu:list`
- `DELETE /menu/{id}`：删除菜单；权限 `system:menu:remove` + `system:menu:list`
- `GET /menu/selectMenuTreeByRoleId/{roleId}`：角色菜单树
- `GET /menu/getMenuTreeAll`：全量菜单树

### 部门管理 `DeptController`（前缀 `dept`，6个）

- `GET /dept/list`：部门列表；权限 `system:dept:list`
- `GET /dept/getDeptTreeList`：部门树；权限 `system:dept:list`
- `POST /dept`：新增部门；权限 `system:dept:add` + `system:dept:list`
- `PUT /dept`：更新部门；权限 `system:dept:edit` + `system:dept:list`
- `GET /dept/{id}`：部门详情；权限 `system:dept:list`
- `DELETE /dept/{id}`：删除部门（级联删除其下所有子部门；部门不存在时返回 `data: false`）；权限 `system:dept:remove` + `system:dept:list`

### 岗位管理 `PostController`（前缀 `post`，6个）

- `GET /post/list`：分页岗位；权限 `system:post:list`
- `POST /post`：新增岗位；权限 `system:post:add` + `system:post:list`
- `PUT /post`：更新岗位；权限 `system:post:edit` + `system:post:list`
- `GET /post/{id}`：岗位详情；权限 `system:post:list`
- `DELETE /post/{ids}`：批量删除；权限 `system:post:remove` + `system:post:list`
- `GET /post/allPost`：全部岗位；权限 `system:post:list`

### 字典管理 `DictController`（前缀 `dict`，10个）

- 类型:
  - `GET /dict/type/list`、`listAll`、`GET /dict/type/{id}`：`system:dict:list`
  - `POST /dict/type`：`system:dict:add` + `list`
  - `PUT /dict/type`：`system:dict:edit` + `list`
  - `DELETE /dict/type/{dictIds}`：`system:dict:remove` + `list`
- 数据:
  - `GET /dict/data/list`、`GET /dict/data/{id}`：`system:dict:list`
  - `POST/PUT /dict/data`：`add`/`edit` + `list`
  - `DELETE /dict/data/{dictIds}`：`system:dict:remove` + `list`

### 日志管理 `LogController`（前缀 `/log`，2个）

- `GET /log/loginLogList`：登录日志分页；`system:loginlog:list`
- `DELETE /log/loginLog/{ids}`、`DELETE /log/loginLog/clean`：`system:loginlog:remove` + `list`
- `GET /log/operationLogList`：操作日志分页；`system:operlog:list`
- `DELETE /log/operationLog/{ids}`、`DELETE /log/operationLog/clean`：`system:operlog:remove` + `list`

## 4. 运维域接口

### 平台管理 `OperationPlatformController`（前缀 `/operation/platform`，5个）

- `GET /operation/platform/list`：`operation:platform:list`
- `POST`：`operation:platform:add` + `list`；`PUT`：`edit` + `list`；`DELETE`：`remove` + `list`
- `GET /operation/platform/{id}`：`operation:platform:list`

### 服务器管理 `OperationServerController`（前缀 `/operation/server`，5个）

- `GET /operation/server/list`
- `POST /operation/server`
- `PUT /operation/server`
- `GET /operation/server/{id}`
- `DELETE /operation/server/{ids}`

### 项目管理 `OperationProjectController`（前缀 `/operation/project`，5个）

- `GET /operation/project/list`
- `POST /operation/project`
- `PUT /operation/project`
- `GET /operation/project/{id}`
- `DELETE /operation/project/{ids}`

### 组件管理 `OperationComponentController`（前缀 `/operation/component`，5个）

- `GET /operation/component/list`
- `POST /operation/component`
- `PUT /operation/component`
- `GET /operation/component/{id}`
- `DELETE /operation/component/{ids}`

## 5. 当前可见接口风险（用于迭代排期）

- 已修复（2026-05-06）: 字典数据删除接口路径变量绑定与删除 Mapper 误用问题
- 已调整（2026-05-06）: 验证码接口改为配置开关模式（`captcha.enabled`）
- 已调整（2026-06-08）: 系统/运维 Controller 补充 `@RequiresPermissions`，与菜单 perms 对齐
- 已调整（2026-06-08）: 角色授权接口返回刷新提示；授权后清除 Shiro 授权缓存
- 已调整（2026-06-08）: 操作/登录日志菜单 perms 已含于 `docs/sql` 基线
- 已调整（2026-06-12）: 本人 `PUT /user` / `PUT /user/resetPassword` 仅需登录；角色 `assignPerm` / `assignUser` 动作拆分
- 已调整（2026-06-12）: 生产同域 Nginx `/login` GET 须回 `index.html`（见 `aws-deployment-guide.md`）
- 控制器层已覆盖主要管理接口权限注解；`/menu/getRouters`、`/dict/data/type/{dictType}`、`/user/profile`、`PUT /user`（本人）等仍仅要求登录
- 多数接口直接接收 Entity 作为入参，需评估字段越权更新风险

## 6. 建议的下一步（接口维度）

- P1: 补齐验证码校验链路（登录请求校验 code + uuid，并消费 Redis）
- P1: 前端联调角色授权接口的 `msg` 提示（刷新页面后菜单生效）
- P1: 给用户/角色/菜单/字典建立最小 API 回归测试
- P2: 推进 DTO 化与参数校验注解，降低直接暴露实体风险
- P2: 增加接口变更日志（新增/废弃/兼容策略）

## 7. 迭代记录追加模板（接口版）

### 2026-06-08 权限与角色授权

- 新增: `PermissionConstants`、`PermissionService`、`ShiroExceptionHandler`
- 鉴权变更: 系统/运维管理接口启用 `@RequiresPermissions`
- 返回结构变更: `insertUserRole` / `addUserRole` / `removeUsers` 成功时 `msg` 含刷新提示
- 前端联调影响: 无权限时 `code=10009`；角色授权成功需展示 `msg` 并建议用户刷新
- 回归验证: 非授权角色访问管理接口应被拒绝；授权后刷新可见新菜单

### 2026-06-12 个人中心、角色动作、部署

- 变更接口: `PUT /user`（本人改资料）、`PUT /user/resetPassword`（本人改密）、`PUT /role`（assignPerm 分支）、`PUT /user/addUserRole`/`removeUsers`（assignUser）
- 鉴权变更: 新增 `system:role:assignPerm`、`system:role:assignUser` 种子与校验
- 数据库: `sys_action` 增至 **40** 条（含角色分配权限）；基线见 `docs/sql/01_baseline_data.sql`
- 部署: `moli-ui.wu-jinsen.com` 同域反代时 `/login` GET/POST 拆分
- 回归验证: test 用户个人中心可保存；直接访问 `/login` 不出现 JSON 500

### [日期-迭代号]

- 新增接口:
- 变更接口:
- 废弃接口:
- 鉴权变更:
- 请求参数变更:
- 返回结构变更:
- 前端联调影响:
- 回归验证:

