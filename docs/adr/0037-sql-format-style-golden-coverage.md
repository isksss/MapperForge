# 0037 SQL Format Style Golden Coverage

## Status

Accepted

## Context

PLAN.local.md defines `SINGLE_LINE`, `COMPACT`, and `MULTI_LINE` SQL formatting styles. Unit tests covered style behavior at the SQL formatter level, but Mapper XML golden fixtures only exercised the default `MULTI_LINE` style.

## Decision

Add golden fixtures for:

- `sqlFormatStyle = SINGLE_LINE`
- `sqlFormatStyle = COMPACT`

Both fixtures participate in idempotence tests.

## Consequences

Mapper XML integration of SQL style settings is now covered by golden file tests.
