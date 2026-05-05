[English](./README.md) | [中文](./README-zh.md) | [日本語](./README-ja.md)

# Moli Admin (moli-project-single)

A **Java admin backend** built with Spring Boot, delivered as a Maven multi-module project. It provides REST APIs with a unified `MoliResult<T>` / `PageRes<T>` style, Shiro-based auth, and Swagger2 documentation.

## Tech stack

- **Runtime**: Java 8, Spring Boot 2.3.x  
- **Data**: MySQL, MyBatis-Plus, Druid  
- **Cache / session**: Redis, Jedis, Shiro Redis session  
- **Other**: Apache Shiro, EasyExcel, MinIO client, Swagger2 (Springfox)

## Modules

| Module | Role |
|--------|------|
| `moli-parent` | Parent BOM / dependency versions |
| `moli-common` | Shared entities, VOs, constants, utilities |
| `moli-server` | Web layer, business services, mappers |

## Prerequisites

- JDK 8  
- Maven 3.6+  
- MySQL 8.x (or compatible)  
- Redis  

## Quick start

1. **Install the parent POM locally** (this repo uses `com.moli:moli-parent` from the `moli-parent` module):

   ```bash
   cd moli-parent && mvn -DskipTests install && cd ..
   ```

2. **Build**:

   ```bash
   mvn -pl moli-common,moli-server -am -DskipTests package
   ```

3. **Configure** `moli-server/src/main/resources/application-dev.yml` (datasource URL, username/password, Redis, etc.). The default Spring profile is `dev` in `application.yml`.

4. **Run** (tests are currently skipped if `UserServiceTest` fails to compile on your JDK):

   ```bash
   cd moli-server && mvn -Dmaven.test.skip=true spring-boot:run
   ```

5. **Open**: [http://localhost:1125/swagger-ui.html](http://localhost:1125/swagger-ui.html) (when `swagger.show` is `true` in the active profile).

## Documentation

- [API iteration map](docs/api-iteration-map.md)  
- [Project iteration baseline](docs/project-iteration-baseline.md)  
- [Dependency security roadmap](docs/dependency-security-roadmap.md)  
- AI collaboration: [AGENTS.md](AGENTS.md) (Chinese) / [AGENTS.en.md](AGENTS.en.md) (English mirror)

## License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE).
