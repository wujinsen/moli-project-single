# 项目迭代基线（Moli 后台）

最后更新: 2026-06-11
维护方式: 每次迭代后更新本文件，作为需求/技术/风险的单一基准事实来源

## 1. 项目概览

- 仓库: `moli-project-single`
- 类型: Java Maven 多模块后端项目（当前仓库未包含前端管理端代码）
- 模块:
  - `moli-server`: Spring Boot 主服务，包含 controller/service/mapper/config
  - `moli-common`: 公共实体、VO、常量、工具、异常、返回结构
- 启动类: `moli-server/src/main/java/com/moli/MoliApplication.java`
- 默认端口: `1125`（`moli-server/src/main/resources/application.yml`）

## 2. 技术栈与关键依赖

来自 `README.md` 与 `pom.xml`:

- 框架: Spring Boot `2.3.12.RELEASE`（maven plugin）
- ORM: MyBatis-Plus
- 鉴权: Apache Shiro + shiro-redis
- 数据库: MySQL + Druid
- 缓存/会话: Redis（Jedis）
- 文档: Swagger2（springfox）
- 文件/对象存储: MinIO
- 其他: EasyExcel、AOP、Lombok、Fastjson

构建与运行特征:

- 包管理/构建: Maven
- JDK 编译目标: 8（`moli-server/pom.xml`）
- 测试插件: surefire 配置为跳过测试（`<skip>true</skip>`）

## 3. 业务与代码结构（后端）

### 3.1 主要包分层（`moli-server`）

- `com.moli.system.controller`: 登录、用户、角色、菜单、部门、岗位、字典、日志
- `com.moli.operation.controller`: 平台/服务器/项目/组件等运维类接口
- `com.moli.system.service` + `impl`: 业务服务层
- `com.moli.system.mapper`、`com.moli.operation.mapper`: 持久层 Mapper
- `com.moli.config`: MyBatis-Plus、Redis、Druid、Swagger、Shiro、工具配置
- `com.moli.aspectj`: 操作日志切面
- `com.moli.ai`: `ChatGPTController`

### 3.2 主要控制器清单

- 系统域:
  - `system/controller/LoginController.java`
  - `system/controller/UserController.java`
  - `system/controller/RoleController.java`
  - `system/controller/MenuController.java`
  - `system/controller/DeptController.java`
  - `system/controller/PostController.java`
  - `system/controller/DictController.java`
  - `system/controller/LogController.java`
- 运维域:
  - `operation/controller/OperationPlatformController.java`
  - `operation/controller/OperationServerController.java`
  - `operation/controller/OperationProjectController.java`
  - `operation/controller/OperationComponentController.java`
- AI:
  - `ai/ChatGPTController.java`

### 3.3 鉴权与会话机制

- Shiro 入口: `moli-server/src/main/java/com/moli/config/shiro/ShiroConfig.java`
- 过滤链:
  - 放行: `/login`、Swagger 相关路径等
  - 其余: `/** -> authc`
- Session 存储: Redis（`RedisSessionDAO`）
- Cache: Redis（`RedisCacheManager`，主键字段 `userName`）
- 登录接口: `LoginController#login`，返回 token（sessionId）+ 用户信息 + 菜单树

### 3.4 公共模型与响应

- 统一返回: `moli-common/src/main/java/com/moli/common/core/MoliResult.java`
- 核心实体: 位于 `moli-common/src/main/java/com/moli/common/domain/entity`
- 视图对象: 位于 `moli-common/src/main/java/com/moli/common/domain/vo`

## 4. 环境与配置基线

配置文件:

- `moli-server/src/main/resources/application.yml`
- `moli-server/src/main/resources/application-dev.yml`
- `moli-server/src/main/resources/application-test.yml`
- `moli-server/src/main/resources/application-pre.yml`
- `moli-server/src/main/resources/application-pro.yml`

当前显性配置要点:

- profile 默认: `dev`
- MySQL/Redis/MinIO 连接信息写在 yml 中
- swagger 开关在不同环境有差异（如 `pro` 为 `false`）

## 5. 测试、质量与工程化现状

- 单测目录存在: `moli-server/src/test/java`
- 已有测试以集成/实验性为主，覆盖有限（User/Post/Redis）
- Maven 打包默认跳过测试（存在回归风险）
- 仓库中未见 CI 配置（未发现 `.github/workflows`）
- 未发现前端工程、Node 构建或前端 lint/test 配置
- AWS 单机部署步骤见 `docs/aws-deployment-guide.md`（MySQL/Nginx/Redis 自建；Redis 勿用 Serverless）
- 数据库表关系图见 `docs/database-schema-diagram.md`（Mermaid ER 图，对应 `docs/sql/00_schema.sql`）
- 多系统 SSO：在 **moli-admin**（本仓库，模块目录 `moli-server`）做登录、系统门户、用户-系统分配；其他系统各自 RBAC。见 `docs/multi-system-sso-design.md`
- Linux 部署脚本：`scripts/linux/moli-server.sh`（启停）、`moli-server.env.example`、`moli-server.service`（systemd）

## 6. 现阶段风险与技术债（基于仓库事实）

- 配置中出现明文数据库/Redis/MinIO 凭证，存在泄漏与环境切换风险
- `moli-server/pom.xml` 依赖 `com.moli:moli-chatgpt-server:1.0-SNAPSHOT`，但当前聚合模块中未包含该模块，可能导致构建依赖不可解析
- MyBatis-Plus 配置了 `mapper-locations: classpath*:mapper/*.xml`，但仓库未发现 `mapper/*.xml` 文件（需确认是否完全注解化 mapper 或存在遗漏）
- 登录验证码已改为开关模式（`captcha.enabled`），但登录接口尚未接入验证码校验链路
- 测试默认跳过，且测试内容偏样例化，不足以保护核心业务回归
- `Swagger2Config` 继承 `WebMvcConfigurerAdapter`（较老写法）

## 7. 建议的迭代优先级（P0 -> P2）

- P0: 敏感配置治理（改为环境变量/密钥管理，清理明文凭证）
- P0: 修复构建完整性（确认/补齐 `moli-chatgpt-server` 依赖来源或移除）
- P1: 补齐登录验证码校验链路（启用时校验并消费验证码）
- P1: 建立最小回归测试集（登录、权限、用户、菜单、字典等核心链路）
- P1: 打开 CI（至少 `mvn test` + 编译检查）
- P1: 梳理并统一 mapper 策略（XML or 注解）与配置一致性
- P2: 升级老旧依赖与安全风险依赖（如 fastjson 旧版本等）
- P2: 规范接口文档与鉴权说明（Swagger + README 扩展）

## 8. 后续迭代记录模板（每次开发后追加）

### [迭代编号/日期]

- 目标:
- 变更模块:
- 关键文件:
- 数据结构/接口变更:
- 配置变更:
- 风险与回滚点:
- 验证方式:
- 结果:
- 遗留事项:

## 9. 多系统 SSO 与超管权限（2026-06-11）

- 目标: moli-admin 统一登录 + 系统门户；超管可进任意系统并在 INTERNAL 系统内拥有全菜单与 `*:*:*`
- 变更模块: `moli-common`、`moli-server`
- 关键能力:
  - `sys_system` / `sys_user_system` 表（已含于 `docs/sql/00_schema.sql`）
  - `SystemController`、`SsoController`、`LoginController` 扩展
  - 超管 `hasFullPermission`；SSO `fullPermission` 字段
  - 登录响应 `LoginVo.fullPermission`
- 配置: `sso.enabled`、`SSO_SHARED_SECRET`（见 `application.yml` 与 `scripts/linux/moli-server.env.example`）
- 验证: `mvn -pl moli-common,moli-server -am -DskipTests package` 编译通过；需执行 SQL 后联调登录与 `/system/enter`
- 遗留: 「系统注册」菜单已含于基线种子；前端 `meiling-ui` 在独立仓库联调
- 前端开发文档: `meiling-ui/docs/sso-frontend-dev-guide.md`、`meiling-ui/AGENTS.md`

## 10. 动作权限（P1–P4 已落地，2026-06-11）

- 模型: `sys_action` + `sys_role_action`；页面 list 仍在 C 菜单 `perms`；废弃菜单 F
- **P3**：字典/系统注册/日志/运维四模块动作种子 + Controller 写接口 `add/edit/remove` + `list` 双重鉴权；前端各管理页 `guardAction`
- **P4**：`GET /action/page` 等动作目录 CRUD（权限复用 `system:menu:list` / `edit`）；`ActionManageView` + `patch_sys_menu_action_manage.sql`
- 后端: `PermissionService` 并集页面+动作；`GET /auth/capabilities`、`GET /action/list`、`GET /role/{id}/auth`
- 下发: `LoginVo` / `SystemEnterVo` 含 `permissions`；`fillLoginContext` 门户关/单系统自动进已拷贝
- 前端（meiling-ui）: `constants/permissions.ts`、`guardAction`、角色页动作勾选、按钮常显点击拦截
- SQL 基线: `docs/sql/00_schema.sql` + `01_baseline_data.sql`（自稳定库导出；新环境按 `docs/sql/README.md` 初始化）；重导出: `python scripts/export_db_baseline.py`
- 设计: [action-permission-design.md](action-permission-design.md)

## 11. 登录会话策略（2026-06-11）

- 配置 `shiro.single-session`（`application.yml`）：`false`（默认）= 同一用户多端登录互不影响；`true` = 新登录踢掉旧会话
- 实现：`ShiroRealm` 仅在单端模式登录前 `deleteCache`；多端模式用 Redis Set 记录各 Session，退出只移除当前 Session；停用用户仍 `deleteCache` 清理全部 Session

## 12. 最近一次修复记录（2026-05-06）

- 修复 `DictController#deleteData`:
  - 路径变量显式绑定为 `@PathVariable("dictIds")`
  - 删除对象从 `dictTypeMapper` 更正为 `dictDataMapper`
- 修复 `LoginController#captchaImage`:
  - 改为配置开关模式（`captcha.enabled`，默认 `false`）
  - 开启时生成验证码图片并写入 Redis；关闭时返回关闭提示
- 当前阻塞: 已解除（本机已配置 Maven，2026-06-11 编译通过）

## 13. AI 迭代基础设施（2026-05-06）

- 已新增项目级 AI 协作基线文件:
  - `.cursor/skills/bugfix-triage/SKILL.md`
  - `.cursor/skills/api-change-checklist/SKILL.md`
  - `AGENTS.md`
  - `.cursor/rules/java-backend-iteration.mdc`
  - `.cursor/rules/docs-sync-policy.mdc`
  - `.cursor/skills/backend-iteration-delivery/SKILL.md`
- 用途:
  - 统一后续迭代执行规范
  - 约束 API/文档同步策略
  - 提供可复用的后端迭代交付技能模板
- 已新增用户级全局 Hook（作用于本机所有 Cursor 项目）:
  - `~/.cursor/hooks.json`
  - `~/.cursor/hooks/bilingual-sync-reminder.py`
  - `~/.cursor/hooks/init-bilingual-docs.py`
  - 行为: 修改中文治理文档（如 `AGENTS.md`、`.cursor/rules/*.mdc`、`.cursor/skills/*/SKILL.md`）时，提醒同步对应英文文档。
  - 初始化命令: `python3 ~/.cursor/hooks/init-bilingual-docs.py <项目根目录>`
  - 骨架同步命令: `python3 ~/.cursor/hooks/init-bilingual-docs.py <项目根目录> --sync-skeleton`
  - 严格校验命令: `python3 ~/.cursor/hooks/init-bilingual-docs.py <项目根目录> --dry-run --sync-skeleton --strict`
  - 说明: 可将中文文档标题结构同步到英文镜像中的待翻译清单块（不覆盖原英文正文）。

## 14. 依赖安全治理记录（2026-05-06）

- 已完成第一批低风险依赖升级（父 `pom` 统一版本管理）:
  - `fastjson`: `1.2.46/1.2.70` -> `1.2.83`
  - `commons-fileupload`: `1.3.3` -> `1.5`
  - `httpclient`: `4.5.9` -> `4.5.14`
- 变更文件:
  - `moli-parent/pom.xml`
  - `moli-server/pom.xml`
- 说明:
  - `moli-server` 中对应依赖去掉显式版本，统一走父 `dependencyManagement`。
  - 已完成 Maven 环境配置，且已执行编译与依赖树验证（`moli-server` 编译通过，目标依赖版本生效）。
  - 已输出治理路线文档: `docs/dependency-security-roadmap.md`

