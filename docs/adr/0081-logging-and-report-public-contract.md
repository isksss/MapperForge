# 0081 Logging And Report Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines SLF4J logging categories in the order `parser`, `formatter`, `validator`, and `gradle`, and also lists Report as an extension target.
The implementation already centralized loggers and exposed `ValidationReportFormatter`, but the PLAN category order and public API documentation were not directly recorded.

## Decision

Extend `MapperForgeLoggersTest` to assert the PLAN logging categories in PLAN order.
Add Japanese Javadocs to `MapperForgeLoggers` and `ValidationReportFormatter`.

## Consequences

Logging category changes now require an explicit test and ADR update.
The report formatter remains a documented public entry point for future CLI, IDE, and report integrations.
