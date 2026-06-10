# 0035 Attribute Layout Golden Coverage

## Status

Accepted

## Context

PLAN.local.md defines `attributeLayout` and `tagWrapStyle` as formatter controls. The implementation supported these options, but golden fixtures did not explicitly cover `ONE_PER_LINE` or `ALWAYS`.

## Decision

Add dedicated golden file tests for:

- `attributeLayout = ONE_PER_LINE`
- `tagWrapStyle = ALWAYS`

Both fixtures also participate in the global golden idempotence test.

## Consequences

Changes to attribute wrapping behavior now require intentional golden updates.
