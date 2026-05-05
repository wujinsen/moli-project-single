---
name: backend-iteration-delivery
description: 用于本项目后端小步迭代交付，强调 API 可控变更、配置开关优先与文档同步，适用于需求开发、缺陷修复与接口调整。
disable-model-invocation: true
---

# 后端迭代交付

## 适用范围

适用于以下变更：

- `moli-server`
- `moli-common`
- `docs/project-iteration-baseline.md`
- `docs/api-iteration-map.md`

## 执行流程

复制以下清单并按顺序执行：

```text
迭代进度
- [ ] 明确目标模块与受影响接口
- [ ] 实施最小改动
- [ ] 校验改动文件 lint
- [ ] 行为或风险变化时更新文档
- [ ] 输出变更总结与下一步建议
```

## 实施规则

- 保持现有代码风格与返回封装。
- 非明确要求不做大范围重构。
- 临时启停需求优先使用配置开关。
- 保持 controller/service/mapper 职责分离。

## 文档更新规则

- API 契约或接口行为变化时更新 `docs/api-iteration-map.md`。
- 技术债、风险或优先级变化时更新 `docs/project-iteration-baseline.md`。
- 若修改了中文规范文档，需同步更新对应英文文档。

## 输出格式

输出内容：

1. 改动了什么（路径 + 目的）
2. 做了哪些验证
3. 剩余风险与后续事项

