# 0097 SQL Expression Parser Public Contract

## Status

Accepted

## Context

`SqlExpressionParser` is the public Pratt parser entry point for SQL expression AST construction. It strips outer whitespace, ignores SQL comments, and recovers unsupported or malformed input as `UnknownExpression`, but those behaviors were only implicit in tests for specific expressions.

## Decision

Add Japanese Javadoc to `SqlExpressionParser` and extend `SqlExpressionParserTest` with contract tests for:

- outer whitespace stripping
- SQL comments excluded from expression parsing
- blank SQL falling back to `UnknownExpression` with stripped raw SQL

## Consequences

Callers can parse partial mapper SQL without handling parser exceptions for unsupported expressions. Future parser behavior changes must update the tests and this ADR.
