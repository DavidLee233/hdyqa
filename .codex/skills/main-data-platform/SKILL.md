---
name: main-data-platform
description: Build and extend the master data platform in this repository. Use when tasks involve remote master data access, local master data management, field mapping, token retrieval, remote-to-local sync, xxl-job scheduling, weekly backup, or downstream open APIs for organization department, person basic info, and person job info.
---

# Main Data Platform

Read `.codex/rules/main-data-rules.md` first.

## Use This Skill
- Use this skill for any task under `sysware-mainData` or `sysware-ui/src/views/mainData`.
- Use this skill when the task touches the phase-2 master data plan: remote browse, force sync, scheduled sync, backup, or downstream interfaces.

## Follow This Workflow
1. Read `references/project-map.md`.
2. Read `references/current-baseline.md` if the task depends on what is already implemented.
3. Decide the task slice:
   - backend only
   - frontend only
   - full stack
   - scheduling or backup
4. Touch all required layers for that slice.
5. Validate with the smallest useful command, usually a targeted build or search-based verification.

## Work By Task Type

### Remote browse or force sync
- Start from `RemoteTokenServiceImpl`, `RemoteDataServiceImpl`, and the relevant controller.
- Keep remote field mapping in backend services.
- Keep Vue remote pages read-only.

### Local/remote toggle pages
- Reuse the department page interaction model.
- Keep the same top-level actions across all three data types where feasible.
- Do not let remote mode expose add, edit, or delete.

### Scheduled sync or backup
- Reuse `xxl-job`.
- Separate sync job handlers from backup job handlers.
- Keep backup retention and naming rules aligned with `.codex/rules/main-data-rules.md`.

### Downstream API
- Expose three separate APIs, not one generic type router, unless the user asks for both.
- Default to Bearer token auth and read-only access.

## Read These References Only When Needed
- `references/project-map.md`: repo paths and likely edit points
- `references/current-baseline.md`: what is already done and what is still missing
- `references/delivery-checklist.md`: completion checklist before handoff

## Delivery Standard
- Keep behavior consistent across all three master data types.
- Prefer additive changes over destructive refactors.
- If a rule and existing code conflict, align code to the rule and note the migration point.
