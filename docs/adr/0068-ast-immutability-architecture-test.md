# 0068 AST Immutability Architecture Test

## Status

Accepted

## Context

`PLAN.local.md` requires immutable AST nodes and explicitly forbids `setXxx(...)` mutators. MapperForge already models mapper, SQL, and OGNL AST nodes as records, sealed interfaces, and enums, but this design rule was not guarded directly.

## Decision

Add `AstImmutabilityTest` as a source-level architecture test for `src/main/java/io/github/isksss/mapperforge/ast`.

The test requires each AST source file to use a top-level `record`, `sealed interface`, or `enum`, and rejects setter method declarations.

## Consequences

Future AST additions must preserve the immutable design. If a mutable class becomes necessary, the decision must be recorded explicitly instead of entering the AST package accidentally.
