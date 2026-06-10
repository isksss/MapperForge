# 0040 Non-Strict Validation Failure Behavior

## Status

Accepted

## Context

PLAN.local.md defines `strict=false` as warning-only behavior that keeps the original file when validation fails. Existing functional tests covered successful non-strict formatting, but not validation failure behavior.

## Decision

Add a Gradle functional test that uses `preserveWhitespace=true` to trigger validation failure after formatting. In non-strict mode, `mapperForgeFormat` succeeds, logs the validation warning, and leaves the mapper file unchanged.

## Consequences

The Gradle task contract for non-strict validation failures is now covered by TestKit.
