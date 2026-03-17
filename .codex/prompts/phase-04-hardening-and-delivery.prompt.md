# Phase 4 Prompt: Hardening And Delivery

Use this prompt when the main features exist and the goal is to make the platform safe to operate.

## Prompt
You are working in `E:\\hdyqa`. Follow `AGENTS.md`, `.codex/rules/main-data-rules.md`, and `.codex/skills/main-data-platform/SKILL.md`.

Goal:
- Harden the master data platform for ongoing operation and handoff.

Implement or improve:
- Error handling and retry boundaries.
- Sync and backup observability.
- Conflict logging clarity.
- Export consistency across local and remote views.
- Permission checks and endpoint naming consistency.
- Missing validation tests or lightweight verification commands.
- Code cleanup where the current implementation is duplicated or divergent across the three data types.

Constraints:
- Do not change the business defaults unless required by a bug.
- Prefer small safe refactors with visible benefit.

Done means:
- The system is easier to operate, debug, and continue building.
- Remaining risks are documented clearly in the final handoff.
