# Phase 2 Prompt: Remote Browse And Sync

Use this prompt when the goal is to finish remote master data access and remote-to-local sync.

## Prompt
You are working in `E:\\hdyqa`. Follow `AGENTS.md`, `.codex/rules/main-data-rules.md`, and `.codex/skills/main-data-platform/SKILL.md`.

Goal:
- Complete remote browse and force sync for organization department, person basic info, and person job info.

Implement:
- Shared remote query and mapping support for all three data types.
- Remote browse backend APIs and Vue pages for the two employee data types.
- Force sync endpoints for each data type.
- Full-sync-to-local logic with remote-dominant and local-supplement rules.
- Sync batch logging and clear failure handling.

Constraints:
- Remote view is read-only.
- Do not hard delete remote-missing rows by default.
- Keep field mapping in backend services.
- Keep the three data types behaviorally aligned.

Validation:
- Verify each type has local browse, remote browse, and force sync entry points.
- Verify token refresh fallback still works.
