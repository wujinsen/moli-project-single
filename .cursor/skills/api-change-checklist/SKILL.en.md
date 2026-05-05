---
name: api-change-checklist
description: Apply a consistent checklist for backend API changes in this repository. Use when adding, modifying, or deprecating endpoints, request fields, response fields, or auth behavior.
disable-model-invocation: true
---

# API Change Checklist

## Scope

Use for changes in controller/service/vo/entity that affect API behavior.

## Checklist

```text
API Change Progress
- [ ] Confirm impacted endpoint(s): method + path
- [ ] Confirm request shape change (query/path/body)
- [ ] Confirm response shape/code/message change
- [ ] Confirm auth/permission impact
- [ ] Confirm backward compatibility decision
- [ ] Update docs/api-iteration-map.md
- [ ] Update docs/project-iteration-baseline.md if risk/priority changed
```

## Compatibility Policy

- Default: keep backward compatibility.
- If breaking change is required:
  - state reason explicitly
  - provide migration note in output
  - mark as high-risk follow-up

## Validation Guidance

- Validate edited files with lint.
- If runtime validation is unavailable, provide manual verification steps.

## Output Template

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


<!-- AUTO-SYNC OUTLINE START -->
## Outline Synced from Chinese Source

Source: `SKILL.md`

The following outline mirrors Chinese headings for translation tracking.

- [ ] # 接口变更检查清单
- [ ] ## 适用范围
- [ ] ## 检查清单
- [ ] ## 兼容性策略
- [ ] ## 验证建议
- [ ] ## 输出模板

<!-- AUTO-SYNC OUTLINE END -->
