# 0029 Format SQL Inside CDATA Validation

## Status

Accepted

## Context

`formatSqlInsideCdata=true` asks MapperForge to format SQL while preserving the CDATA wrapper. Validator previously compared CDATA raw text whenever `preserveCdata=true`, so legitimate formatting-only changes inside CDATA failed validation.

## Decision

When `formatSqlInsideCdata=true`, Validator ignores raw CDATA text differences in XML AST comparison and relies on SQL statement signature validation for semantic safety.

- CDATA wrapper preservation is still controlled by `preserveCdata`
- Formatting-only CDATA SQL changes are allowed
- SQL semantic changes inside CDATA are still rejected by statement signature comparison
- Default `formatSqlInsideCdata=false` keeps strict raw CDATA preservation

## Consequences

The `formatSqlInsideCdata` option is usable in strict Gradle tasks without weakening SQL semantic validation.
