---
name: bugfix-triage
description: Triage and fix backend bugs in controlled steps for this Java admin project. Use when investigating errors, regressions, unexpected API behavior, or production-like failures in moli-server/moli-common.
disable-model-invocation: true
---

# Bugfix Triage

## When to Use

Use this skill for:

- API returns wrong data or error status
- login/auth/session related failures
- mapper/service/controller behavior regressions
- env/config-driven behavior mismatches

## Triage Workflow

Copy checklist:

```text
Bugfix Progress
- [ ] Define expected vs actual behavior
- [ ] Locate failure layer (controller/service/mapper/config)
- [ ] Implement smallest safe fix
- [ ] Validate changed files (lint/tests if available)
- [ ] Update docs risk/notes if behavior changed
```

## Investigation Rules

- Start from the reported endpoint or module, avoid broad code sweeps.
- Prefer fact-based findings from current code/config.
- Preserve existing API contracts unless fix requires explicit contract change.

## Fix Rules

- Minimize blast radius and avoid unrelated refactors.
- For temporary behavior changes, prefer config switch over hard-coded behavior.
- Keep error handling explicit and user-facing messages clear.

## Output

Return:

1. Root cause
2. Fix summary (file paths)
3. Validation status
4. Remaining risk/follow-up


<!-- AUTO-SYNC OUTLINE START -->
## Outline Synced from Chinese Source

Source: `SKILL.md`

The following outline mirrors Chinese headings for translation tracking.

- [ ] # 缺陷排查修复
- [ ] ## 何时使用
- [ ] ## 排查流程
- [ ] ## 排查规则
- [ ] ## 修复规则
- [ ] ## 输出内容

<!-- AUTO-SYNC OUTLINE END -->
