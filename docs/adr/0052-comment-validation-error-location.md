# 0052 Comment Validation Error Location

## Status

Accepted

## Context

PLAN.local.md requires comment preservation and defines `ValidationError` with a source `Range`. Comment semantic validation detected changed comments but did not identify the affected source range.

## Decision

Attach a source `Range` to comment validation failures. The location points to the first differing XML or SQL comment in the original mapper XML.

## Consequences

Comment preservation errors now point to the changed comment region. XML comments and SQL comments share the same `COMMENT` validation error type and location strategy.
