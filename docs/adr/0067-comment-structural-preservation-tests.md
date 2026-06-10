# 0067 Comment Structural Preservation Tests

## Status

Accepted

## Context

`PLAN.local.md` forbids deleting, moving, merging, or splitting comments. MapperForge already rejected comment text changes, but the test evidence did not name these structural cases directly.

## Decision

Extend `ValidatorTest` with explicit cases for XML comment deletion, movement, merge, and split. These tests assert `ErrorType.COMMENT` and source locations for the first affected original comment.

## Consequences

The comment preservation contract is now directly tied to the PLAN wording. Formatter changes that accidentally alter comment structure fail validation tests even when ordinary comment content-change tests still pass.
