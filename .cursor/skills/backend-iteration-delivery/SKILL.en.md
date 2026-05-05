---
name: backend-iteration-delivery
description: Deliver small backend iterations for this Java admin project with controlled API changes, config-first toggles, and synchronized project docs. Use when implementing feature requests, bug fixes, or API adjustments in moli-server/moli-common.
disable-model-invocation: true
---

# Backend Iteration Delivery

## Scope

Use this skill for changes in:

- `moli-server`
- `moli-common`
- `docs/project-iteration-baseline.md`
- `docs/api-iteration-map.md`

## Workflow

Copy this checklist and execute in order:

```text
Iteration Progress
- [ ] Confirm target module and impacted API
- [ ] Implement minimal code change
- [ ] Validate lints for edited files
- [ ] Update docs if behavior/risk changed
- [ ] Return change summary + next step
```

## Implementation Rules

- Keep existing coding style and response wrappers.
- Avoid broad refactors unless explicitly requested.
- Prefer config switch for temporary enable/disable requirements.
- Keep controller/service/mapper responsibilities separated.

## Docs Update Rules

- Update `docs/api-iteration-map.md` when API contract or endpoint behavior changes.
- Update `docs/project-iteration-baseline.md` when technical debt, risk, or priorities change.
- If Chinese governance docs are changed, update corresponding English docs.

## Output Format

Provide:

1. What changed (paths + purpose)
2. What was validated
3. Any remaining risk/follow-up


<!-- AUTO-SYNC OUTLINE START -->
## Outline Synced from Chinese Source

Source: `SKILL.md`

The following outline mirrors Chinese headings for translation tracking.

- [ ] # 后端迭代交付
- [ ] ## 适用范围
- [ ] ## 执行流程
- [ ] ## 实施规则
- [ ] ## 文档更新规则
- [ ] ## 输出格式

<!-- AUTO-SYNC OUTLINE END -->
