# 0069 Central Logger Usage Test

## Status

Accepted

## Context

`PLAN.local.md` requires SLF4J logging categories for parser, formatter, validator, and Gradle integration. MapperForge already exposes stable category names through `MapperForgeLoggers`, but the tests only checked the category strings.

## Decision

Extend `MapperForgeLoggersTest` to verify that the parser, formatter, validator, and Gradle task entry points use the central logger registry.

## Consequences

Future refactors are less likely to bypass the stable logging categories. If a subsystem entry point changes, the logging contract must be updated deliberately.
