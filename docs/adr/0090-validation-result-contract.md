# 0090 Validation Result Contract

## Status

Accepted

## Context

`PLAN.local.md` defines validation as a core feature and exposes validation errors with error codes, error types, messages, and source ranges. `ValidationResult` is a public result object used by report formatting and Gradle task behavior, so callers must not be able to mutate its error list after construction.

## Decision

Add Japanese Javadocs to `ValidationError` and `ValidationResult`.

Make `ValidationResult` defensively copy `errors` with `List.copyOf`, and add a focused contract test proving that constructor input mutation and accessor mutation do not affect the result.

## Consequences

Validation result objects are stable snapshots. Future validation report or task code can rely on the error list remaining unchanged after result creation.
