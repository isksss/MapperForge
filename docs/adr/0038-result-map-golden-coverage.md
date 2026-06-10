# 0038 ResultMap Golden Coverage

## Status

Accepted

## Context

PLAN.local.md lists MyBatis-specific mapper nodes including `resultMap`, `association`, `collection`, `constructor`, `arg`, `idArg`, `discriminator`, and `case`. Parser tests covered dedicated node creation, but golden formatter coverage only included simple `resultMap` cases.

## Decision

Add a nested `resultMap` golden fixture covering constructor arguments, nested association, collection, and discriminator cases.

## Consequences

Formatter and idempotence behavior for the core result mapping structure is covered by golden file tests.
