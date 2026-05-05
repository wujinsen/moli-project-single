---
name: api-change-checklist
description: 用于本仓库后端接口变更的一致性检查，适用于新增、修改、废弃接口及请求响应和鉴权行为变更。
disable-model-invocation: true
---

# 接口变更检查清单

## 适用范围

用于所有影响 API 行为的 controller/service/vo/entity 变更。

## 检查清单

```text
接口变更进度
- [ ] 确认受影响接口：方法 + 路径
- [ ] 确认请求结构变化（query/path/body）
- [ ] 确认返回结构、状态码、提示变化
- [ ] 确认鉴权与权限影响
- [ ] 确认向后兼容策略
- [ ] 更新 docs/api-iteration-map.md
- [ ] 若风险或优先级变化，更新基线文档
```

## 兼容性策略

- 默认保持向后兼容。
- 若必须做破坏性变更：
  - 明确说明原因
  - 在输出中提供迁移说明
  - 标记为高风险后续项

## 验证建议

- 用 lint 验证改动文件。
- 若无法运行验证，给出手工验证步骤。

## 输出模板

```markdown
API change summary:
- Endpoint:
- Request change:
- Response change:
- Auth change:
- Compatibility:
- Docs updated:
- Verification:
```

