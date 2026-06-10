# ADR 0008: Placeholder Parser

## Status

Accepted

## Context

MapperForge must preserve MyBatis placeholders as part of the lossless and validation guarantees.
The design models placeholders as `PlaceholderExpression(type, expression, options)`.

## Decision

- Parse `#{...}` as `HASH` placeholders and `${...}` as `DOLLAR` placeholders.
- Split placeholder options on commas while preserving commas inside quoted option values.
- Compare parsed placeholders in validation instead of comparing only raw placeholder strings.
- Preserve placeholder order as part of validation.

## Consequences

Validation can now detect semantic placeholder option changes, such as `jdbcType` changes.
Future SQL expression parsing can reuse the same placeholder parser.
