# AGENTS 协作指南（moli-project-single）

## 项目背景

- 项目类型：Java **茉莉管理系统** 后端（Maven 多模块）
- 模块：
  - `moli-server`：业务服务与接口层
  - `moli-common`：公共实体、VO、常量与工具
- 核心技术栈：Spring Boot、MyBatis-Plus、Shiro、Redis、MySQL、Swagger2

## 主要开发目标

保持每次迭代小步可测、过程可追踪：

1. 一次只实现一个清晰需求。
2. 未明确要求时保持 API 向后兼容。
3. 每次有效变更后同步更新项目文档。

## 协作约定

- 优先小范围精准改动，避免大重构。
- 不修改无关文件。
- 保持统一返回风格：`MoliResult<T>`。
- 保持分页返回风格：`PageRes<T>`。
- 遵循现有分层：controller -> service -> mapper。
- 非必要不引入新框架。
- 数据库基线: `docs/sql/00_schema.sql` + `01_baseline_data.sql`；变更后 `python scripts/export_db_baseline.py`
- 单测回归: `mvn -pl moli-common,moli-server -am test`（见 `docs/api-test-report.md`）
- 界面截图: `PIC/`（见 [README-zh.md](README-zh.md) 界面预览）

## 安全与配置边界

- 禁止在源码中新增明文密钥。
- 优先配置开关控制（例如 `captcha.enabled`）。
- 对高风险接口补充说明并同步文档。

## 迭代完成清单

任务结束前检查：

- [ ] API 行为符合需求。
- [ ] 改动文件 lint 通过。
- [ ] 必要时更新 `docs/project-iteration-baseline.md`。
- [ ] 接口行为变更时更新 `docs/api-iteration-map.md`。
- [ ] 风险与后续事项已记录。
- [ ] 如本次修改了中文规范文档，同步更新对应英文文档。

## 后续迭代优先方向

1. 登录与鉴权稳定性
2. 用户、角色、菜单核心接口
3. 字典与运维模块正确性
4. 核心接口基础回归测试

