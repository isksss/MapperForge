# 0085 Mapper Element List Immutability

## Status

Accepted

## Context

`PLAN.local.md` requires immutable AST nodes. Mapper element records already expose their structure through record components, but list components can still leak mutability when the original constructor arguments are retained.

## Decision

All `ElementNode` implementations copy `attributes` and `children` through `ElementNode.copyAttributes` and `ElementNode.copyChildren` in their compact constructors.

`AstImmutabilityTest` verifies every mapper element node with list components by mutating the original constructor arguments and by attempting to mutate accessor results.

## Consequences

Mapper AST callers cannot mutate element attributes or children after node construction. Future mapper element nodes must keep the same defensive-copy contract and be added to the contract test.
