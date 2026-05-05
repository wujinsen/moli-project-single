# 当前依赖漏洞治理清单与升级优先级

最后更新: 2026-05-06  
适用范围: `moli-project-single`（`moli-server` / `moli-common` / `moli-parent`）

## 1. 治理目标

- 降低已知高危依赖漏洞暴露面（RCE、反序列化、上传组件漏洞等）
- 将依赖版本管理统一到父 `pom`，减少子模块漂移
- 按“低风险先落地、高收益优先”推进，避免一次性大改导致回归

## 2. 当前依赖状态（关键项）

## 2.1 已完成（本轮已落地）

- `com.alibaba:fastjson` -> `1.2.83`
- `commons-fileupload:commons-fileupload` -> `1.5`
- `org.apache.httpcomponents:httpclient` -> `4.5.14`

说明:

- 以上版本已通过 `dependency:tree` 验证在 `moli-server` 生效
- 子模块显式版本已收口到 `moli-parent` 的 `dependencyManagement`

## 2.2 待重点治理（高优先级）

- `spring-boot` 基线: `2.3.12.RELEASE`（生命周期较老）
- `springfox-swagger2 / springfox-swagger-ui`: `2.9.2`（生态老旧，维护状态差）
- `jedis`: `2.9.3`（版本陈旧）
- `servlet-api`: `2.3`（非常老）
- `easyexcel`: `2.2.10`（较老）

## 3. 风险分级与优先级

## 3.1 P0（立即执行）

- **完成项确认与固化**
  - 固化本轮已升级依赖（fastjson/fileupload/httpclient）
  - 在 CI 中加入依赖树检查（避免后续回退到旧版本）
- **依赖扫描机制接入**
  - 引入 OWASP Dependency-Check 或 Snyk/Trivy（任选其一）
  - 形成可重复执行的扫描命令与基线报告

验收标准:

- 编译通过
- 依赖扫描可执行，输出报告可落盘
- 关键依赖不被间接依赖降级覆盖

## 3.2 P1（高价值，中等改动）

- **Swagger 技术栈治理**
  - 目标: `springfox 2.9.2` -> `springdoc-openapi`（推荐）
  - 风险: 接口文档路径、注解与 UI 展示行为变化
- **Redis 客户端升级评估**
  - 目标: `jedis 2.9.3` 升级到维护中的稳定版本，或评估使用 Spring Boot 默认客户端栈
- **Javax 旧 API 依赖治理**
  - `servlet-api`、`persistence-api` 等历史依赖做兼容性梳理

验收标准:

- 文档功能可用（Swagger/OpenAPI 页面正常）
- 关键接口回归通过（登录、用户、角色、菜单、字典）
- 启动日志无新增严重告警

## 3.3 P2（持续优化）

- **基础框架升级路线**
  - 规划 `Spring Boot 2.3.x -> 2.7.x`（阶段性）
  - 再评估到 `3.x`（涉及 `javax -> jakarta`，改动较大）
- **依赖治理自动化**
  - 月度/双周扫描节奏
  - 漏洞阈值门禁（如 CVSS >= 7 阻断发布）
- **冗余依赖清理**
  - 清理未使用或重复声明依赖，降低冲突概率

## 4. 推荐执行批次（建议按顺序）

1. 批次 A（已做）: fastjson + fileupload + httpclient
2. 批次 B: 接入依赖漏洞扫描并生成第一版基线报告
3. 批次 C: Swagger 从 springfox 迁移到 springdoc
4. 批次 D: Redis/Jedis 与 javax 旧依赖升级
5. 批次 E: Spring Boot 主版本升级路线

## 5. 每批次标准检查清单

- [ ] `mvn -pl moli-server -DskipTests compile` 通过
- [ ] 核心路径冒烟（登录、用户列表、菜单树、字典分页）
- [ ] 依赖树确认目标版本生效
- [ ] 记录变更与风险（更新本文件 + 基线文档）
- [ ] 必要时给出回滚版本与回滚命令

## 6. 已知约束

- 本项目依赖了本地工程构件（如 `moli-parent`、`moli-chatgpt-server`），在新环境执行构建前需先本地安装上游包
- 依赖扫描工具尚未接入（当前文档为治理路线与执行清单，非最终漏洞审计报告）

