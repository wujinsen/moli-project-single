# SSO Portal System Groups (System Select Page)

Last updated: 2026-06-11

## Groups

| `system_group` | Label | Examples |
|----------------|-------|----------|
| `platform` | Platform & governance | `moli-admin` |
| `business` | Business apps | `crm-demo`, e-commerce, member, OA |
| `data` | Data platforms | BI, metrics, warehouse, governance (not AI apps) |
| `tech` | Technical platforms | API gateway/portal, low-code, CI/CD, AI Copilot |
| `ops` | Operations | `moli-ops` |

Display order: `platform` → `business` → `data` → `tech` → `ops` (empty groups hidden).

## API

- `GET /system/my` → `SystemVo.systemGroup`
- `POST/PUT /system` → `SysSystem.systemGroup`
- SQL: `scripts/moli.sql` (full schema + data snapshot)

## Frontend (meiling-ui)

Read: `meiling-ui/docs/portal-system-group-ui.md` (Chinese) / `portal-system-group-ui.en.md`
