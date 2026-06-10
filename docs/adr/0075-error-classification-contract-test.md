# 0075 Error Classification Contract Test

## Status

Accepted

## Context

`PLAN.local.md` defines `ErrorCode` as a stable top-level error classification. MapperForge already uses error codes in validation, configuration, parser, formatter, reports, and Gradle output, but the enum values and ordering were not directly guarded by tests or Japanese Javadoc.

## Decision

Add Japanese Javadoc to `ErrorCode` and `ErrorType`, and extend `ValidationErrorTest` with enum contract tests.

The tests verify that `ErrorCode` matches the PLAN-defined classification order and that `ErrorType` covers the current validation difference kinds.

## Consequences

Report, Gradle, and future IDE integrations can rely on stable error classification names. Adding, removing, or reordering these values now requires an explicit test and ADR update.
