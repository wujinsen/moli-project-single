# AGENTS Guide (moli-project-single)

## Project Context

- Project type: Java backend admin system (Maven multi-module)
- Modules:
  - `moli-server`: business service and API
  - `moli-common`: shared entity, VO, constants, and utilities
- Core stack: Spring Boot, MyBatis-Plus, Shiro, Redis, MySQL, Swagger2

## Primary Development Goal

Keep each iteration small, testable, and traceable:

1. Implement one clear requirement at a time.
2. Keep API behavior backward compatible unless explicitly requested.
3. Update project docs after each meaningful change.

## Working Agreement

- Prefer minimal, targeted changes over large refactors.
- Do not modify unrelated files.
- Keep response wrapper style: `MoliResult<T>`.
- Keep page response style: `PageRes<T>`.
- Follow current layering: controller -> service -> mapper.
- Avoid introducing new frameworks unless required.

## Security and Config Guardrails

- Never hardcode new secrets in source code.
- Prefer config-driven switches (e.g. `captcha.enabled`).
- Add clear comments and doc updates for risky endpoint behavior.

## Iteration Completion Checklist

- [ ] API behavior is implemented as requested.
- [ ] Lints pass for edited files.
- [ ] Update `docs/project-iteration-baseline.md` when needed.
- [ ] Update `docs/api-iteration-map.md` when API behavior changes.
- [ ] Risks and follow-up items are documented.
- [ ] If Chinese governance docs were changed, update corresponding English docs.

## Priority Areas for Next Iterations

1. Login and auth flow stability
2. Core user/role/menu management APIs
3. Dictionary and operation module correctness
4. Basic regression tests for critical APIs


<!-- AUTO-SYNC OUTLINE START -->
## Outline Synced from Chinese Source

Source: `AGENTS.md`

The following outline mirrors Chinese headings for translation tracking.

- [ ] # AGENTS 协作指南（moli-project-single）
- [ ] ## 项目背景
- [ ] ## 主要开发目标
- [ ] ## 协作约定
- [ ] ## 安全与配置边界
- [ ] ## 迭代完成清单
- [ ] ## 后续迭代优先方向

<!-- AUTO-SYNC OUTLINE END -->
