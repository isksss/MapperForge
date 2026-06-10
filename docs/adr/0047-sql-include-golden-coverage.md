# 0047 Sql Include Golden Coverage

## Status

Accepted

## Context

PLAN.local.md defines dedicated mapper nodes for `sql` fragments and `include` references. Parser tests cover these tags, but formatter golden files did not yet fix their XML and SQL interaction.

## Decision

Add a default golden fixture that formats a column `sql` fragment and a `select` statement containing an inline `include` reference. The fixture records the current formatter behavior of expanding empty include tags to explicit start/end tags.

## Consequences

The golden suite now detects regressions in reusable SQL fragment formatting and include placement inside SQL text.
