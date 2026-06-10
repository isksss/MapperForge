# 0072 Fixed Rule Order Contract Test

## Status

Accepted

## Context

`PLAN.local.md` defines the v1 rule engine as a fixed pipeline:

1. `NormalizeRule`
2. `AttributeOrderRule`
3. `OgnlFormatRule`
4. `SqlFormatRule`
5. `WrapRule`

MapperForge already used this order in `FormatterRulePipeline.v1()`, and existing tests covered the combined behavior, but the exact order was not tested directly.

## Decision

Extend `FormatterRulePipelineTest` with a contract test that verifies the `v1()` rule list order. Add Japanese Javadoc to the public rule API and rule classes touched by this contract.

## Consequences

Changing the v1 rule order now requires an explicit test update and ADR-level decision. The Japanese Javadoc makes the rule API intent visible in generated documentation.
