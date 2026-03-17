# Phase 1 Prompt: Baseline And Gap Closure

Use this prompt when the goal is to align the current codebase with the phase-2 master data plan before deep feature work.

## Prompt
You are working in `E:\\hdyqa`. Follow `AGENTS.md`, `.codex/rules/main-data-rules.md`, and `.codex/skills/main-data-platform/SKILL.md`.

Goal:
- Audit the current master data implementation across backend, frontend, and SQL.
- Normalize inconsistent assumptions before new feature work.

Required outputs:
- List completed capabilities for each of the three data types.
- Identify asymmetric implementation gaps.
- Identify schema, enum, or field-mapping inconsistencies, especially type values.
- Propose the smallest safe code changes needed to make the baseline implementation-ready.

Execution constraints:
- Prefer incremental fixes over broad refactors.
- Keep xxl-job as the default scheduler.
- Preserve existing local CRUD behavior.

Done means:
- A concrete implementation list exists for the next phase.
- All blocking inconsistencies are either fixed or explicitly documented in code comments or task notes.
