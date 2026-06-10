# 0039 CDATA Config Golden Coverage

## Status

Accepted

## Context

PLAN.local.md defines CDATA preservation and SQL formatting inside CDATA as configurable behavior. Unit and Gradle functional tests covered these settings, but golden fixtures only covered the default CDATA-preserving behavior.

## Decision

Add golden fixtures for:

- `formatSqlInsideCdata = true` with CDATA wrapper preserved
- `preserveCdata = false` with XML-sensitive characters escaped as text

Both fixtures participate in idempotence tests.

## Consequences

CDATA configuration behavior now has before/after golden coverage, including `<` and `&` escaping when CDATA is not preserved.

Normal XML text output escapes XML-sensitive `<` and `&` during formatting so that CDATA removal remains valid and idempotent.
