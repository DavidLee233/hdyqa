# Codex Workspace Guide

## Purpose
- Use this repository to build and evolve the master data platform around `sysware-mainData` and `sysware-ui`.
- Treat the current target as "master data platform phase 2": remote master data browsing, remote-to-local sync, weekly backup, and downstream service APIs.

## Required Local Guidance
- For any task about master data, sync, mapping, remote token, backup, or downstream open APIs, read:
  - `.codex/rules/main-data-rules.md`
  - `.codex/skills/main-data-platform/SKILL.md`
- If the user asks for phased delivery or asks to continue from the phase plan, use one of:
  - `.codex/prompts/phase-01-baseline-and-gap.prompt.md`
  - `.codex/prompts/phase-02-remote-sync.prompt.md`
  - `.codex/prompts/phase-03-open-api-and-scheduling.prompt.md`
  - `.codex/prompts/phase-04-hardening-and-delivery.prompt.md`

## Project Defaults
- Keep the existing multi-module structure; do not introduce a new service unless clearly necessary.
- Prefer extending:
  - `sysware-mainData` for master data backend logic
  - `sysware-ui` for master data pages
  - `qa_system.sql` for schema/data seed adjustments
- Reuse `xxl-job` for scheduled work by default. Do not add `@Scheduled` unless the user explicitly wants a second scheduler.
- Keep the three master data types aligned everywhere:
  - `1`: organization department
  - `2`: person basic info
  - `3`: person job info

## Working Mode
- When changing a master data capability, cover the full slice when needed:
  - backend API
  - service logic
  - mapper/domain/sql
  - frontend page and API client
  - validation or execution check
- Do not silently invent remote fields, sync rules, or push API contracts. Follow the local rules and skill references first.

## Safety
- Never delete existing local business data as part of remote sync unless the task explicitly requires hard delete.
- Prefer soft invalidation for remote-missing records.
- Keep manual local records and remote-synced records distinguishable.
