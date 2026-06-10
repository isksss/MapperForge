# ADR 0001: Initial MapperForge Bootstrap

## Status

Accepted

## Context

MapperForge is specified as a Gradle plugin and Java library for formatting and checking MyBatis Mapper XML.
The repository initially contained only the design plan, so the first implementation needs a runnable vertical slice.

## Decision

- Use Java 21 toolchains and Gradle 9.
- Manage required local tools through `mise`.
- Implement the Gradle plugin id as `io.github.isksss.mapperforge`.
- Add golden file tests before expanding formatter behavior.
- Keep the first formatter intentionally conservative: preserve comments, CDATA, placeholders, unknown XML elements, and mapper structure.
- Record larger design choices in `docs/adr`.

## Consequences

This establishes a testable baseline without attempting the full SQL/OGNL AST implementation in the first commit.
Future parser and rule-engine work can replace the conservative formatter behind the same public API.
