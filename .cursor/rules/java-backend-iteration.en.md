# Java Backend Iteration Rule (Reference Translation)

- Make minimal and focused changes for each request.
- Preserve existing API contracts unless the user asks to change them.
- Keep `MoliResult<T>` and `PageRes<T>` response style consistent.
- Follow module boundaries: `moli-server` (business/API), `moli-common` (shared models/utils).
- Prefer config switches over hard-disable logic for temporary feature changes.
- For API changes, update `docs/api-iteration-map.md`.
- For major technical decisions or risk changes, update `docs/project-iteration-baseline.md`.
- After edits, check lints for changed files and fix obvious issues.
- If Chinese governance docs are changed, update corresponding English docs.


<!-- AUTO-SYNC OUTLINE START -->
## Outline Synced from Chinese Source

Source: `java-backend-iteration.mdc`

The following outline mirrors Chinese headings for translation tracking.

- [ ] # Java 后端迭代规则

<!-- AUTO-SYNC OUTLINE END -->
