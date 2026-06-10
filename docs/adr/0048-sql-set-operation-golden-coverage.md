# 0048 Sql Set Operation Golden Coverage

## Status

Accepted

## Context

PLAN.local.md lists `UNION`, `INTERSECT`, and `EXCEPT` as supported statement parser targets. Unit tests cover set operation parsing, but the mapper XML formatter did not have a golden fixture for SQL set operations.

## Decision

Add a default golden fixture containing `UNION`, `INTERSECT`, and `EXCEPT` in a single mapper `select`. The fixture records the current formatter line breaks around set operation keywords.

## Consequences

Formatter regressions that alter set operation placement inside mapper XML are now caught by the golden suite.
