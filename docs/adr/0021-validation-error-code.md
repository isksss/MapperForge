# 0021 Validation Error Code

## Status

Accepted

## Context

PLAN.local.md defines a broad `ErrorCode` enum in addition to validation-specific `ErrorType`. Existing `ValidationError` only exposed `ErrorType`, so callers could not distinguish high-level error categories consistently.

## Decision

`ValidationError` carries both `ErrorCode` and `ErrorType`.

- Existing constructor `ValidationError(ErrorType, String, Range)` remains and defaults to `VALIDATION_ERROR`
- Explicit constructor accepts `ErrorCode` for future parser/tokenizer/OGNL/config integration
- Validator semantic failures continue to use `VALIDATION_ERROR` as the broad code and `ErrorType` as the detailed validation classification

## Consequences

Gradle and future report/lint integrations can surface stable top-level error codes without losing the existing detailed validation type.
