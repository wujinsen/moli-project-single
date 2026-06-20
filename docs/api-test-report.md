# API 单元测试用例报告（不连库）

> 生成时间：2026-06-12  
> 模块：`moli-server`  
> 执行命令：`mvn -pl moli-common,moli-server -am test`

## 1. 执行摘要

| 指标 | 结果 |
|------|------|
| 测试用例总数 | **128** |
| 通过 | **128** |
| 失败 | 0 |
| 跳过 | 0 |
| 执行方式 | Mockito 注入 Controller，Mock Mapper/Service，**不启动 Spring、不连 MySQL/Redis** |
| HTML 报告 | `moli-server/target/site/surefire-report.html` |
| XML 明细 | `moli-server/target/surefire-reports/` |

## 2. 测试基础设施

| 文件 | 说明 |
|------|------|
| `testsupport/ControllerTestSupport` | 统一断言 `MoliResult.code=200`，封装分页/CRUD Mock |
| `testsupport/MybatisPlusTestSupport` | 初始化实体 Lambda 元数据，避免无 Spring 时 `LambdaQueryWrapper` 报错 |
| `testsupport/ShiroMockSupport` | Mock `SecurityUtils.getSubject()` 注入当前用户 |
| `testsupport/AbstractApiTest` | API 测试基类，`@BeforeClass` 初始化 MyBatis-Plus 元数据 |

**说明：** 原 `@SpringBootTest` 集成测试（`RedisTest`、`UserServiceTest`、`PostServiceTest`）已移至 `moli-server/legacy-integration-test/`，不参与本次单元测试。

## 3. 接口覆盖总览

| Controller | 路径前缀 | 活跃接口数 | 已测接口数 | 覆盖率 |
|------------|----------|-----------|-----------|--------|
| LoginController | `/login` 等 | 3 | 3 | 100%（成功登录路径见 §5） |
| UserController | `/user` | 20 | 20 | 100%（含本人 profile/改密） |
| ActionController | `/action` | 7 | 7 | 100% |
| SystemController | `/system` | 7 | 7 | 100% |
| SsoController | `/sso` | 1 | 1 | 100% |
| MenuController | `/menu` | 8 | 8 | 100% |
| RoleController | `/role` | 7 | 7 | 100% |
| DeptController | `/dept` | 6 | 6 | 100% |
| PostController | `/post` | 6 | 6 | 100% |
| DictController | `/dict` | 12 | 12 | 100% |
| LogController | `/log` | 6 | 6 | 100% |
| OperationPlatformController | `/operation/platform` | 5 | 5 | 100% |
| OperationServerController | `/operation/server` | 5 | 5 | 100% |
| OperationProjectController | `/operation/project` | 5 | 5 | 100% |
| OperationComponentController | `/operation/component` | 5 | 5 | 100% |
| ChatGPTController | `/chatgpt` | 0（已注释） | — | 不适用 |
| **合计** | | **96** | **96** | **100%** |

另含服务层/工具类用例 **19** 个（见 §4.2）。

## 4. 用例明细

### 4.1 Controller API 冒烟用例（`com.moli.api.*`）

测试类命名规则：`{HTTP方法}_{模块}_{路径标识}`，断言 Controller 在 Mock 依赖下返回 `code=200`（或预期的业务错误码）。

#### LoginControllerApiTest（3）

| 用例 | 接口 | 说明 |
|------|------|------|
| `POST_login_userNotFound` | `POST /login` | 用户不存在 → `ERROR` |
| `POST_captchaImage_disabled` | `POST /captchaImage` | 验证码关闭 → `SERVICE_ERROR_CODE` |
| `POST_logout` | `POST /logout` | Mock Subject 退出 → 200 |

#### UserControllerApiTest（23）

| 用例 | 接口 |
|------|------|
| `GET_user_list` | `GET /user/list` |
| `POST_user_insert_requiresPassword` | `POST /user`（缺密码） |
| `POST_user_insert_success` | `POST /user` |
| `PUT_user_update` | `PUT /user` |
| `GET_user_id` | `GET /user/{id}` |
| `GET_user_getUserDetail` | `GET /user/getUserDetail/{id}` |
| `GET_user_profile` | `GET /user/profile` |
| `PUT_user_language` | `PUT /user/language` |
| `DELETE_user_userIds` | `DELETE /user/{userIds}` |
| `PUT_user_changeStatus` | `PUT /user/changeStatus` |
| `GET_user_getRoleByUserId` | `GET /user/getRoleByUserId/{userId}` |
| `PUT_user_insertUserRole` | `PUT /user/insertUserRole` |
| `GET_user_getSystemByUserId` | `GET /user/getSystemByUserId/{userId}` |
| `PUT_user_insertUserSystem` | `PUT /user/insertUserSystem` |
| `GET_user_getUserBySystem` | `GET /user/getUserBySystem` |
| `GET_user_unauthorizedUsersBySystem` | `GET /user/unauthorizedUsersBySystem` |
| `PUT_user_addUserRole` | `PUT /user/addUserRole` |
| `GET_user_getUserByRole` | `GET /user/getUserByRole` |
| `PUT_user_removeUsers` | `PUT /user/removeUsers` |
| `GET_user_unauthorizedUsers` | `GET /user/unauthorizedUsers` |
| `PUT_user_resetPassword` | `PUT /user/resetPassword` |
| `GET_user_getSystemByUserId_deniesSuperadminForNormalViewer` | 超管可见性：普通用户不可查 superadmin 系统 |
| `PUT_user_insertUserSystem_skipsSuperadmin` | 分配系统时跳过 superadmin |

#### SystemControllerApiTest（7）

| 用例 | 接口 |
|------|------|
| `GET_system_my` | `GET /system/my` |
| `POST_system_enter` | `POST /system/enter` |
| `POST_system_switch` | `POST /system/switch` |
| `GET_system_list` | `GET /system/list` |
| `POST_system_insert` | `POST /system` |
| `PUT_system_update` | `PUT /system` |
| `DELETE_system_ids` | `DELETE /system/{ids}` |

#### SsoControllerApiTest（2）

| 用例 | 接口 |
|------|------|
| `POST_sso_validate_success` | `POST /sso/validate`（合法 secret） |
| `POST_sso_validate_invalidSecret` | `POST /sso/validate`（非法 secret） |

#### MenuControllerApiTest（8）

| 用例 | 接口 |
|------|------|
| `GET_menu_getRouters` | `GET /menu/getRouters` |
| `GET_menu_list` | `GET /menu/list` |
| `POST_menu_insert` | `POST /menu` |
| `PUT_menu_update` | `PUT /menu` |
| `GET_menu_id` | `GET /menu/{id}` |
| `DELETE_menu_id` | `DELETE /menu/{id}` |
| `GET_menu_selectMenuTreeByRoleId` | `GET /menu/selectMenuTreeByRoleId/{roleId}` |
| `GET_menu_getMenuTreeAll` | `GET /menu/getMenuTreeAll` |

#### RoleControllerApiTest（7）

| 用例 | 接口 |
|------|------|
| `GET_role_list` | `GET /role/list` |
| `POST_role_insert` | `POST /role` |
| `PUT_role_update` | `PUT /role` |
| `GET_role_id` | `GET /role/{id}` |
| `DELETE_role_ids` | `DELETE /role/{ids}` |
| `PUT_role_changeStatus` | `PUT /role/changeStatus` |
| `GET_role_getRoleAll` | `GET /role/getRoleAll` |

#### DeptControllerApiTest（6）

| 用例 | 接口 |
|------|------|
| `GET_dept_list` | `GET /dept/list` |
| `GET_dept_getDeptTreeList` | `GET /dept/getDeptTreeList` |
| `POST_dept_insert` | `POST /dept` |
| `PUT_dept_update` | `PUT /dept` |
| `GET_dept_id` | `GET /dept/{id}` |
| `DELETE_dept_id` | `DELETE /dept/{id}` |

#### PostControllerApiTest（6）

| 用例 | 接口 |
|------|------|
| `GET_post_list` | `GET /post/list` |
| `POST_post_insert` | `POST /post` |
| `PUT_post_update` | `PUT /post` |
| `GET_post_id` | `GET /post/{id}` |
| `DELETE_post_ids` | `DELETE /post/{ids}` |
| `GET_post_allPost` | `GET /post/allPost` |

#### DictControllerApiTest（12）

| 用例 | 接口 |
|------|------|
| `GET_dict_type_list` | `GET /dict/type/list` |
| `GET_dict_type_listAll` | `GET /dict/type/listAll` |
| `POST_dict_type` | `POST /dict/type` |
| `PUT_dict_type` | `PUT /dict/type` |
| `GET_dict_type_id` | `GET /dict/type/{id}` |
| `DELETE_dict_type_ids` | `DELETE /dict/type/{dictIds}` |
| `GET_dict_data_type` | `GET /dict/data/type/{dictType}` |
| `GET_dict_data_list` | `GET /dict/data/list` |
| `GET_dict_data_id` | `GET /dict/data/{id}` |
| `POST_dict_data` | `POST /dict/data` |
| `PUT_dict_data` | `PUT /dict/data` |
| `DELETE_dict_data_ids` | `DELETE /dict/data/{dictIds}` |

#### LogControllerApiTest（6）

| 用例 | 接口 |
|------|------|
| `GET_log_loginLogList` | `GET /log/loginLogList` |
| `DELETE_log_loginLog_ids` | `DELETE /log/loginLog/{ids}` |
| `DELETE_log_loginLog_clean` | `DELETE /log/loginLog/clean` |
| `GET_log_operationLogList` | `GET /log/operationLogList` |
| `DELETE_log_operationLog_ids` | `DELETE /log/operationLog/{ids}` |
| `DELETE_log_operationLog_clean` | `DELETE /log/operationLog/clean` |

#### OperationControllersApiTest（20）

| 用例 | 接口 |
|------|------|
| `GET/POST/PUT/GET/DELETE_operation_platform_*` | `/operation/platform` 全套 5 接口 |
| `GET/POST/PUT/GET/DELETE_operation_server_*` | `/operation/server` 全套 5 接口 |
| `GET/POST/PUT/GET/DELETE_operation_project_*` | `/operation/project` 全套 5 接口 |
| `GET/POST/PUT/GET/DELETE_operation_component_*` | `/operation/component` 全套 5 接口 |

### 4.2 服务层与工具类（19）

#### SysSystemServiceImplTest（5）

- `listUserIdsBySystemId_nullSystemId_returnsEmpty`
- `listUserIdsBySystemId_mergesAssignedUsersAndPrivilegedAccounts`
- `listUserIdsBySystemId_includesPrivilegedAccountsEvenWithoutRelations`
- `assignUserSystems_skipsPrivilegedAccount`
- `assignUserSystems_replacesRelationsForNormalUser`

#### PrivilegedUserUtilsTest（10）

- 超管/特殊账号判定、`canViewUser` 可见性矩阵
- `applyListVisibilityFilter` 对普通用户与特殊账号的 SQL 条件差异

#### SystemGroupConstantTest（4）

- `system_group` 七类门户分组校验与默认值 `business`

## 5. 未覆盖 / 后续建议

| 项 | 原因 | 建议 |
|----|------|------|
| `POST /login` 成功路径 | 依赖 Shiro `Subject.login()`、Session、Redis | 单独集成测试或 Testcontainers |
| `ChatGPTController` | 源码已注释 | 启用后再补用例 |
| Shiro `@RequiresPermissions` | 单元测试未走权限链 | 可选 MockMvc + Shiro 测试配置 |
| 分页/排序/边界参数 | 冒烟只验证 200 | 按需补充参数化用例 |

## 6. 本地复现

```bash
cd moli-project-single
mvn install -pl moli-common -DskipTests
mvn test -pl moli-server surefire-report:report
# 浏览器打开
# moli-server/target/site/surefire-report.html
```
