# Main Data Rules

## Scope
- Apply these rules when touching field mapping, local master data CRUD, remote master data browsing, forced sync, token retrieval, scheduled sync, backup, or downstream data APIs.

## Business Invariants
- The platform manages exactly three master data types:
  - `1` organization department
  - `2` employee basic info
  - `3` employee job info
- Field mapping type values must stay consistent across UI, backend, and SQL.
- Default sync strategy is:
  - full sync now
  - leave extension points for incremental sync later
- Default merge strategy is:
  - remote-dominant for remote-sourced rows
  - local manual rows remain preserved
- Default downstream API style is:
  - three separate read-only APIs
  - existing Bearer token authentication

## Scheduling Rules
- Reuse `xxl-job` for timed execution.
- Keep sync and backup as separate job handlers.
- Default remote sync order:
  - organization department
  - person basic info
  - person job info
- Backup rules:
  - run weekly
  - keep at most 10 weeks
  - filename format `yyyyMMdd_backup.sql`

## Data Handling Rules
- Do not hard delete remote records during sync by default.
- Mark remote-missing rows as invalid or deleted by flag.
- Store enough metadata to identify:
  - data source
  - last sync batch
  - last sync time
  - sync validity state
- Log each sync batch with counts for fetched, inserted, updated, skipped, invalidated, and failed rows.

## Implementation Rules
- Prefer shared sync components over copy-paste per data type.
- Keep remote query construction and field mapping in backend services, not in Vue pages.
- Treat the current department remote browsing code as a baseline to refactor, not as the final pattern.
- When adding a new remote list page for person basic or person job, keep the same interaction model as the department page:
  - local/remote switch
  - remote view read-only
  - forced sync action

## Module Ownership
- Backend:
  - `sysware-mainData/src/main/java/com/sysware/mainData`
- Backend SQL mappers:
  - `sysware-mainData/src/main/resources/mapper/mainData`
- Frontend APIs:
  - `sysware-ui/src/api/mainData`
- Frontend pages:
  - `sysware-ui/src/views/mainData`
- Seed/reference SQL:
  - `qa_system.sql`

## Before Finishing Any Main Data Task
- Check whether all three data types need symmetric changes.
- Check whether the field mapping type is correct.
- Check whether frontend and backend both expose the same capability.
- Check whether logs, export behavior, and permissions still make sense.
