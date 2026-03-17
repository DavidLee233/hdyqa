# Current Baseline

## Already Implemented
- Field mapping table exists with add, edit, delete, and list functions.
- Local CRUD pages and backend exist for:
  - organization department
  - person basic info
  - person job info
- Remote token retrieval service already exists.
- Department remote browse has an initial implementation.

## Still Missing or Incomplete
- Person basic info remote browse page and backend support.
- Person job info remote browse page and backend support.
- Unified force sync logic for all three data types.
- Scheduled remote-to-local sync based on `xxl-job`.
- Weekly backup with 10-week retention and required filename format.
- Downstream open APIs for all three data types.
- Robust sync logs, conflict handling, and job visibility.

## Design Defaults For This Repo
- Sync strategy: full sync now, extensible for incremental later.
- Merge rule: remote dominant for remote rows, local supplement for manual rows.
- Downstream interface mode: three separate APIs.
- Downstream auth: existing Bearer token.
