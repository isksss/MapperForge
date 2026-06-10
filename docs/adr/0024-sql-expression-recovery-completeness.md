# 0024 SQL Expression Recovery Completeness

## Status

Accepted

## Context

MapperForge uses recoverable SQL parsing and falls back to `UnknownStatement` / `UnknownExpression` for unsupported syntax. The expression parser could previously parse a valid prefix and leave unsupported tokens unconsumed, which made expressions like `name is not null` appear as a successful `ColumnExpression`.

## Decision

`SqlExpressionParser.parse()` treats unconsumed tokens as parse failure and returns `UnknownExpression` for the full raw expression.

- Malformed binary expressions also recover to full-expression `UnknownExpression`
- Statement parser keeps unsupported clause expressions as `UnknownExpression`
- Existing supported expression parsing remains unchanged

## Consequences

Validation and AST diff no longer treat partially parsed expressions as semantically understood. Unsupported expressions remain lossless via normalized raw fallback until explicit grammar support is added.
