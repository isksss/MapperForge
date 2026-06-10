# ADR 0009: Fixed Rule Pipeline

## Status

Accepted

## Context

The design specifies a v1 fixed rule engine with a future SPI option.
Current formatting behavior is still stream-printer based, but the parser now exposes a hybrid mapper AST.

## Decision

- Add `FormatterRule` with the planned `MapperNode apply(MapperNode, FormatterContext)` contract.
- Add a fixed v1 `FormatterRulePipeline` in this order:
  1. `NormalizeRule`
  2. `AttributeOrderRule`
  3. `OgnlFormatRule`
  4. `SqlFormatRule`
  5. `WrapRule`
- Keep rules immutable: rules return new nodes instead of mutating input nodes.
- Keep `NormalizeRule` and `WrapRule` as traversal no-ops until the document printer is introduced.

## Consequences

Rule behavior can now be tested on AST nodes directly.
Future printer work can consume the same pipeline without changing the public rule contract.
