# 0055 Whitespace Validation Error Location

## Status

Accepted

## Context

PLAN.local.md says whitespace is compared only when `preserveWhitespace=true`, and `ValidationError` carries a source `Range`. Whitespace validation detected tag-boundary whitespace changes but did not identify the affected region.

## Decision

Attach a source `Range` to whitespace validation failures. The location points to the first differing tag-boundary region. When whitespace was inserted and no before-side range exists, the after-side inserted region is used.

## Consequences

Whitespace preservation errors now point to the XML boundary that changed. This completes source range reporting for the validator error types currently covered by semantic validation tests.
