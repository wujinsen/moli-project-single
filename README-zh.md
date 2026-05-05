[English](./README.md) | [中文](./README-zh.md) | [日本語](./README-ja.md)

# 茉莉后台管理系统（moli-project-single）

基于 **Spring Boot** 的 **Java 后台管理系统**，采用 **Maven 多模块** 组织代码。接口统一使用 `MoliResult<T>`、`PageRes<T>`，鉴权为 **Shiro**，并提供 **Swagger2** 文档。

## 技术栈

- **运行环境**：Java 8、Spring Boot 2.3.x  
- **数据**：MySQL、MyBatis-Plus、Druid  
- **缓存 / 会话**：Redis、Jedis、Shiro Redis Session  
- **其他**：Apache Shiro、EasyExcel、MinIO 客户端、Swagger2（Springfox）

## 模块说明

| 模块 | 说明 |
|------|------|
| `moli-parent` | 父工程，统一依赖版本（BOM） |
| `moli-common` | 公共实体、VO、常量与工具 |
| `moli-server` | Web 与业务接口层、Mapper |

## 环境要求

- JDK 8  
- Maven 3.6+  
- MySQL 8.x（或兼容版本）  
- Redis  

## 快速开始

1. **先本地安装父 POM**（工程依赖 `com.moli:moli-parent`，需从本仓库 `moli-parent` 安装到本地仓库）：

   ```bash
   cd moli-parent && mvn -DskipTests install && cd ..
   ```

2. **编译**：

   ```bash
   mvn -pl moli-common,moli-server -am -DskipTests package
   ```

3. **配置** `moli-server/src/main/resources/application-dev.yml`（数据库地址、账号密码、Redis 等）。`application.yml` 中默认激活的 profile 为 `dev`。

4. **启动**（若本地 `UserServiceTest` 编译失败，可先跳过测试）：

   ```bash
   cd moli-server && mvn -Dmaven.test.skip=true spring-boot:run
   ```

5. **访问**：当前配置下 HTTP 端口为 **1125**，Swagger：[http://localhost:1125/swagger-ui.html](http://localhost:1125/swagger-ui.html)（需在对应 profile 中开启 `swagger.show`）。

## 文档

- [接口迭代地图](docs/api-iteration-map.md)  
- [项目迭代基线](docs/project-iteration-baseline.md)  
- [依赖安全治理路线图](docs/dependency-security-roadmap.md)  
- AI 协作：[AGENTS.md](AGENTS.md) / [AGENTS.en.md](AGENTS.en.md)

## 许可证

本项目采用 **MIT 许可证**，详见 [LICENSE](LICENSE)。
