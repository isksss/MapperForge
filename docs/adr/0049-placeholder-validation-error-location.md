# 0049 Placeholder Validation Error Location

## Status

Accepted

## Context

PLAN.local.md defines `ValidationError` with a `Range location`. Validator errors previously carried `null` locations, which made validation failures harder to map back to source text.

## Decision

Attach a source `Range` to placeholder sequence validation failures. The location points to the first differing placeholder in the original source using 1-based line and column values.

## Consequences

Validation reports can now identify the source position for placeholder semantic changes. Other validation error types can reuse the same range helper as their source mapping is tightened.
