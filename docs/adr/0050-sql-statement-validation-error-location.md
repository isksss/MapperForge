# 0050 Sql Statement Validation Error Location

## Status

Accepted

## Context

PLAN.local.md defines `ValidationError` with a `Range location`. Placeholder validation already reports the first differing placeholder location, but SQL statement semantic validation still reported no source location.

## Decision

Attach a source `Range` to SQL statement validation failures. The location points to the first differing SQL statement body in the original mapper XML, using the whole statement body rather than a narrower expression range.

## Consequences

Statement semantic changes are now traceable to the mapper XML region that caused the validation error. More precise expression-level ranges can be added later without changing the public `ValidationError` shape.
