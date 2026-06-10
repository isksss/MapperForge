# ADR 0006: Hybrid Mapper AST

## Status

Accepted

## Context

MapperForge needs MyBatis-aware formatting and validation, while still preserving unknown XML tags without semantic assumptions.
The design specifies dedicated MyBatis nodes and `GenericElement` for unknown elements.

## Decision

- Parse known MyBatis mapper tags into dedicated immutable `ElementNode` records.
- Preserve unknown XML elements as `GenericElementNode`.
- Keep all element nodes behind the shared `ElementNode` interface with `tagName`, `attributes`, and `children`.
- Keep the current formatter output stable; this change prepares parser, validator, and future rule pipeline work.

## Consequences

Rules can target MyBatis concepts directly without losing unknown extension elements.
The parser remains lossless enough for validation while allowing later SQL/OGNL AST attachment.
