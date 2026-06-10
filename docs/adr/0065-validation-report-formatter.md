# 0065 Validation Report Formatter

## Status

Accepted

## Context

`PLAN.local.md` lists `Report` as an extension target, and validation failures already carry stable error codes, error types, messages, and source ranges. Gradle tasks previously formatted validation failures in a private helper, which made the reporting contract hard to reuse from future CLI, IDE, or report outputs.

## Decision

Introduce `ValidationReportFormatter` as the first report boundary. It converts `ValidationResult` into deterministic human-readable text, including file path, error code, error type, message, and source location when available. Gradle tasks use this formatter for validation failures.

## Consequences

Validation output now has a reusable API and direct JUnit coverage. Future machine-readable reports can be added alongside this formatter without changing validation internals or Gradle task processing.
