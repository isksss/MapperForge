# 0041 Config Error Code

## Status

Accepted

## Context

PLAN.local.md defines `CONFIG_ERROR` in `ErrorCode`. Validation errors already carry broad error codes, but configuration failures from YAML or Gradle DSL were surfaced as raw exceptions without a stable code.

## Decision

`ConfigLoader.ConfigException` carries `ErrorCode.CONFIG_ERROR`. `ConfigLoader` wraps validation failures raised while constructing `FormatterConfig`, and Gradle tasks report config failures with the `CONFIG_ERROR` prefix.

## Consequences

Users and tests can distinguish configuration failures from validation, parser, and formatter errors.
