# Phase 3 Prompt: Scheduling, Backup, And Downstream APIs

Use this prompt when the goal is to expose stable downstream interfaces and timed jobs.

## Prompt
You are working in `E:\\hdyqa`. Follow `AGENTS.md`, `.codex/rules/main-data-rules.md`, and `.codex/skills/main-data-platform/SKILL.md`.

Goal:
- Add timed execution and downstream service interfaces for the master data platform.

Implement:
- `xxl-job` handlers for full sync and weekly backup.
- Weekly backup generation with filename format `yyyyMMdd_backup.sql`.
- Retention cleanup to keep at most 10 weeks.
- Three read-only downstream APIs for:
  - organization department
  - person basic info
  - person job info
- Bearer token authentication for downstream APIs.
- Backup records and job execution visibility if missing.

Constraints:
- Sync and backup may run at the same time.
- Keep APIs separate by data type.
- Do not leak invalidated remote rows by default.

Validation:
- Verify job handler registration points.
- Verify API request/response shape consistency.
- Verify backup filename and cleanup logic.
