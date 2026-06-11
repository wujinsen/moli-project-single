# SSO Portal System Groups (System Select Page)

Last updated: 2026-06-11

## Groups

| `system_group` | Label | Examples |
|----------------|-------|----------|
| `governance` | Governance | `moli-admin` |
| `business` | Business apps | `crm-demo` |
| `ai` | AI apps | LLM assistants, Copilot, agents |
| `tech` | Technical platforms | API portal, low-code |
| `ops` | Operations | `moli-ops` |
| `data` | Data platforms | BI, reports (not AI apps) |
| `office` | Workplace (optional) | OA |

## API

- `GET /system/my` → `SystemVo.systemGroup`
- `POST/PUT /system` → `SysSystem.systemGroup`
- SQL: `docs/sql/00_schema.sql` + `01_baseline_data.sql`

## Frontend (meiling-ui)

Read: `meiling-ui/docs/portal-system-group-ui.md` (Chinese) / `portal-system-group-ui.en.md`
