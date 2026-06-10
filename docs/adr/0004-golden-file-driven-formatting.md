# ADR 0004: Golden File Driven Formatting

## Status

Accepted

## Context

MapperForge formatting behavior must be deterministic and easy to review across simple and complex MyBatis mapper XML.
Formatter changes can otherwise drift silently as SQL, OGNL, dynamic tags, comments, CDATA, and placeholders are expanded.

## Decision

- Treat golden file tests as the primary formatting contract.
- Add cases from simple SQL to complex dynamic SQL before changing formatter behavior.
- Keep known limitations visible in golden outputs until a stronger SQL AST formatter replaces the current conservative formatter.
- Use parser-backed validation tests for lossless guarantees around comments, CDATA, placeholders, and generic elements.

## Consequences

New formatter behavior should normally start with `src/test/resources/golden/**/before.xml` and `after.xml`.
Complex SQL expectations may initially document limitations, but any later improvement must update golden files explicitly.
