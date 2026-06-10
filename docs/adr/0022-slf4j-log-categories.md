# 0022 SLF4J Log Categories

## Status

Accepted

## Context

PLAN.local.md requires SLF4J logging with stable categories for parser, formatter, validator, and Gradle integration. The project already depends on `slf4j-api`, but logger category names were not centralized.

## Decision

Add `MapperForgeLoggers` as the central SLF4J logger registry.

- `io.github.isksss.mapperforge.parser`
- `io.github.isksss.mapperforge.formatter`
- `io.github.isksss.mapperforge.validator`
- `io.github.isksss.mapperforge.gradle`

Parser, formatter, validator, and Gradle task code reference these categories directly. The library still only depends on `slf4j-api`; applications and Gradle decide the runtime logging backend.

## Consequences

Users can enable logs per MapperForge subsystem without relying on implementation class names. Tests lock the category names so future refactors do not silently change logging configuration.
