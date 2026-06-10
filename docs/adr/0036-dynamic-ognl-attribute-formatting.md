# 0036 Dynamic OGNL Attribute Formatting

## Status

Accepted

## Context

PLAN.local.md defines OGNL formatting for dynamic SQL. Validator already treats `if@/when@test`, `bind@value`, and `foreach@collection` as OGNL expressions, but formatter only formatted `test` attributes.

## Decision

Mapper XML formatting applies the OGNL formatter to:

- `if@test`
- `when@test`
- `bind@value`
- `foreach@collection`

## Consequences

Formatting and validation now use the same OGNL-bearing attribute set.
