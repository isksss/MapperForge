# 0032 SQL Comments in Mapper AST

## Status

Accepted

## Context

PLAN.local.md defines `CommentType.SQL`, but Mapper XML parsing previously emitted only XML comments as `CommentNode`. SQL line comments and block comments inside mapper text were kept as plain SQL text, so validator could not classify SQL comment edits as comment changes.

## Decision

Mapper XML parsing splits SQL-bearing text nodes around SQL comments and emits those comments as `CommentNode(CommentType.SQL, rawComment)`.

Validator reconstructs statement signatures from ordered SQL text and SQL comment chunks within the same element. SQL comments remain part of the source sequence for comment comparison, while SQL statement parsing ignores comment tokens for semantic comparison.

## Consequences

- SQL comment edits are reported as `ErrorType.COMMENT`.
- SQL formatting with unchanged comments remains valid.
- Statement signatures continue to compare SQL semantics rather than comment text.
