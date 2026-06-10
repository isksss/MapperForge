# 0086 SQL and OGNL List Immutability Contract

## Status

Accepted

## Context

`PLAN.local.md` requires immutable AST nodes. SQL and OGNL expression records that expose list components already use defensive copies, but the behavior was not covered by an explicit contract test.

## Decision

Keep list-valued SQL and OGNL expression records defensive-copying constructor inputs with `List.copyOf`.

Extend `AstImmutabilityTest` to verify that SQL and OGNL expression list accessors are independent from the original constructor arguments and reject mutation attempts.

## Consequences

Future SQL and OGNL AST additions with list components must preserve the same immutable-list contract and be added to the architecture test.
