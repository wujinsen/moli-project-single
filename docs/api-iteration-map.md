# 后台接口迭代地图

最后更新: 2026-06-10  
适用范围: `moli-server` 当前代码中的 Controller 接口

## 1. 基本结论

- 控制器数量: 15
- HTTP 接口数量: 约 70（`GET/POST/PUT/DELETE` 注解统计）
- 统一返回: `MoliResult<T>`
- 分页返回: `PageRes<T>`
- 鉴权方式: Shiro Session（token 实际为 sessionId）
- 细粒度权限: `@RequiresPermissions`，权限码与 `sys_menu.perms` 一致（如 `system:user:list`）
- 超级管理员: `superadmin` / `admin` 拥有 `*:*:*`，绕过权限校验
- 无权限响应: HTTP 200 + `code=10009` + `msg=无访问权限`
- 全局鉴权策略:
  - 放行: `/login`、`/sso/validate`、Swagger 相关
  - 其余路径: `authc` 拦截（未登录返回 token 失效 JSON）

## 2. 认证与会话

### `LoginController`（前缀为空）

- `POST /login`
  - 入参: `SysUser`（JSON body）
  - 出参: `MoliResult<LoginVo>`（token + user + systemList + 可选 menuVoList）
  - 说明: 启用 `sso.enabled` 且存在 `sys_system` 时返回系统门户；仅 1 个 INTERNAL 系统时自动进入并返回菜单；多系统时需调 `/system/enter`
- `POST /logout`
  - 入参: 无
  - 出参: `MoliResult`
- `POST /captchaImage`
  - 入参: 无
  - 出参: `MoliResult<CaptchaImageVo>`
  - 现状: 支持开关控制（`captcha.enabled`），关闭时返回“验证码功能暂时关闭”

### `SystemController`（前缀 `/system`）

- `GET /system/my`：当前用户可访问系统
- `POST /system/enter`、`POST /system/switch`：进入/切换系统（同一 Session）；EXTERNAL 返回 `redirectUrl`
- `GET/POST/PUT/DELETE /system`：系统注册维护（仅 `superadmin`）

### `SsoController`（前缀 `/sso`）

- `POST /sso/validate`：子系统校验 Ticket（匿名；可配请求头 `X-Sso-Secret`）

### `ChatGPTController`（前缀 `chatgpt`）

- `GET /chatgpt/v1/createCompletion`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`
- `GET /chatgpt/v1/createCompletionTurbo`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`

## 3. 系统管理域接口

### 用户管理 `UserController`（前缀 `/user`，13个）

- `GET /user/list`：分页用户列表（`UserVo` 查询参数）；`superadmin`（最大权限）与 `admin`（特殊管理员）对外隐藏，仅特殊账号登录时可见；按部门筛选时特殊账号无部门归属仍会出现在列表中
- 用户查询/删除/改状态/重置密码等：非特殊账号访问 `superadmin`/`admin` 返回无权限（`10009`）
- `POST /user`：新增用户（`UserVo` body）
- `PUT /user`：更新用户（`SysUserVo` body，含岗位关系更新）
- `GET /user/{id}`：查询用户
- `GET /user/getUserDetail/{id}`：查询用户详情（含 postIds）
- `GET /user/profile`：当前登录用户信息
- `DELETE /user/{userIds}`：批量删除（逻辑删除 + 角色关系删除）
- `PUT /user/changeStatus`：用户启停
- `GET /user/getRoleByUserId/{userId}`：用户角色信息
- `PUT /user/insertUserRole`：重设用户角色；成功后 `msg` 提示刷新页面；写入操作日志
- `PUT /user/addUserRole`：给角色新增用户；成功后 `msg` 提示刷新页面；写入操作日志
- `GET /user/getUserByRole`：查询角色下用户
- `PUT /user/removeUsers`：移除角色下用户；成功后 `msg` 提示刷新页面；写入操作日志
- `GET /user/unauthorizedUsers`：角色未授权用户列表
- `PUT /user/resetPassword`：重置密码
- `GET /user/getSystemByUserId/{userId}`：用户已授权系统
- `PUT /user/insertUserSystem`：保存用户可访问系统（`UserSystemVo`）

### 角色管理 `RoleController`（前缀 `/role`，7个）

- `GET /role/list`：分页列表
- `POST /role`：新增角色（含角色菜单关系）
- `PUT /role`：更新角色（含角色菜单关系）；成功后 `msg` 提示通知相关用户刷新页面
- `GET /role/{id}`：查询角色
- `DELETE /role/{ids}`：删除角色（含关系）
- `PUT /role/changeStatus`：角色状态变更
- `GET /role/getRoleAll`：获取有效角色列表

### 菜单管理 `MenuController`（前缀 `/menu`，8个）

- `GET /menu/getRouters`：当前用户菜单树
- `GET /menu/list`：菜单列表（`menuName`、`status`）
- `POST /menu`：新增菜单
- `PUT /menu`：更新菜单
- `GET /menu/{id}`：菜单详情
- `DELETE /menu/{id}`：删除菜单
- `GET /menu/selectMenuTreeByRoleId/{roleId}`：角色菜单树
- `GET /menu/getMenuTreeAll`：全量菜单树

### 部门管理 `DeptController`（前缀 `dept`，6个）

- `GET /dept/list`：部门列表
- `GET /dept/getDeptTreeList`：部门树
- `POST /dept`：新增部门
- `PUT /dept`：更新部门
- `GET /dept/{id}`：部门详情
- `DELETE /dept/{id}`：删除部门（级联删除其下所有子部门；部门不存在时返回 `data: false`）

### 岗位管理 `PostController`（前缀 `post`，6个）

- `GET /post/list`：分页岗位
- `POST /post`：新增岗位
- `PUT /post`：更新岗位
- `GET /post/{id}`：岗位详情
- `DELETE /post/{ids}`：批量删除
- `GET /post/allPost`：全部岗位

### 字典管理 `DictController`（前缀 `dict`，10个）

- 类型:
  - `GET /dict/type/list`
  - `GET /dict/type/listAll`
  - `POST /dict/type`
  - `PUT /dict/type`
  - `GET /dict/type/{id}`
  - `DELETE /dict/type/{dictIds}`
- 数据:
  - `GET /dict/data/list`
  - `GET /dict/data/{id}`
  - `PUT /dict/data`
  - `DELETE /dict/data/{dictIds}`

### 日志管理 `LogController`（前缀 `/log`，2个）

- `GET /log/loginLogList`：登录日志分页（`loginAddress`/`browser`/`os` 由登录时从请求头解析写入；内网 IP 显示「本地」或「内网IP」）
- `GET /log/operationLogList`：操作日志分页

## 4. 运维域接口

### 平台管理 `OperationPlatformController`（前缀 `/operation/platform`，5个）

- `GET /operation/platform/list`
- `POST /operation/platform`
- `PUT /operation/platform`
- `GET /operation/platform/{id}`
- `DELETE /operation/platform/{ids}`

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
- 已调整（2026-06-08）: 操作/登录日志菜单需执行 `sql/patch_sys_menu_log_perms.sql` 补全 perms
- 控制器层已覆盖主要管理接口权限注解；`/menu/getRouters`、`/dict/data/type/{dictType}`、`/user/profile` 等仍仅要求登录
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

### [日期-迭代号]

- 新增接口:
- 变更接口:
- 废弃接口:
- 鉴权变更:
- 请求参数变更:
- 返回结构变更:
- 前端联调影响:
- 回归验证:

