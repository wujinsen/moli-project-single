# 后台接口迭代地图

最后更新: 2026-05-06  
适用范围: `moli-server` 当前代码中的 Controller 接口

## 1. 基本结论

- 控制器数量: 13
- HTTP 接口数量: 61（`GET/POST/PUT/DELETE` 注解统计）
- 统一返回: `MoliResult<T>`
- 分页返回: `PageRes<T>`
- 鉴权方式: Shiro Session（token 实际为 sessionId）
- 全局鉴权策略:
  - 放行: `/login`、Swagger 相关
  - 其余路径: `authc` 拦截（未登录返回 token 失效 JSON）

## 2. 认证与会话

### `LoginController`（前缀为空）

- `POST /login`
  - 入参: `SysUser`（JSON body）
  - 出参: `MoliResult<LoginVo>`（token + user + menuVoList）
  - 说明: 登录成功后 token = `ShiroUtils.getSession().getId()`
- `POST /logout`
  - 入参: 无
  - 出参: `MoliResult`
- `POST /captchaImage`
  - 入参: 无
  - 出参: `MoliResult<CaptchaImageVo>`
  - 现状: 支持开关控制（`captcha.enabled`），关闭时返回“验证码功能暂时关闭”

### `ChatGPTController`（前缀 `chatgpt`）

- `GET /chatgpt/v1/createCompletion`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`
- `GET /chatgpt/v1/createCompletionTurbo`
  - 入参: `Messages`（query 参数形态）
  - 出参: `MoliResult<String>`

## 3. 系统管理域接口

### 用户管理 `UserController`（前缀 `/user`，13个）

- `GET /user/list`：分页用户列表（`UserVo` 查询参数）
- `POST /user`：新增用户（`UserVo` body）
- `PUT /user`：更新用户（`SysUserVo` body，含岗位关系更新）
- `GET /user/{id}`：查询用户
- `GET /user/getUserDetail/{id}`：查询用户详情（含 postIds）
- `GET /user/profile`：当前登录用户信息
- `DELETE /user/{userIds}`：批量删除（逻辑删除 + 角色关系删除）
- `PUT /user/changeStatus`：用户启停
- `GET /user/getRoleByUserId/{userId}`：用户角色信息
- `PUT /user/insertUserRole`：重设用户角色
- `PUT /user/addUserRole`：给角色新增用户
- `GET /user/getUserByRole`：查询角色下用户
- `PUT /user/removeUsers`：移除角色下用户
- `GET /user/unauthorizedUsers`：角色未授权用户列表
- `PUT /user/resetPassword`：重置密码

### 角色管理 `RoleController`（前缀 `/role`，7个）

- `GET /role/list`：分页列表
- `POST /role`：新增角色（含角色菜单关系）
- `PUT /role`：更新角色（重建角色菜单关系）
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
- `DELETE /dept/{id}`：删除部门

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

- `GET /log/loginLogList`：登录日志分页
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
- 控制器层几乎没有细粒度权限注解，当前主要依赖全局登录拦截
- 多数接口直接接收 Entity 作为入参，需评估字段越权更新风险

## 6. 建议的下一步（接口维度）

- P1: 补齐验证码校验链路（登录请求校验 code + uuid，并消费 Redis）
- P1: 为高风险写操作接口补充权限点模型（先约定权限码，再落注解）
- P1: 给用户/角色/菜单/字典建立最小 API 回归测试
- P2: 推进 DTO 化与参数校验注解，降低直接暴露实体风险
- P2: 增加接口变更日志（新增/废弃/兼容策略）

## 7. 迭代记录追加模板（接口版）

### [日期-迭代号]

- 新增接口:
- 变更接口:
- 废弃接口:
- 鉴权变更:
- 请求参数变更:
- 返回结构变更:
- 前端联调影响:
- 回归验证:

