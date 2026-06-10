# 0054 Generic Element Validation Error Location

## Status

Accepted

## Context

PLAN.local.md defines `GenericElement` as a lossless fallback node and includes generic elements in validator comparison targets. Generic element validation detected AST changes but did not identify the affected source range.

## Decision

Attach a source `Range` to generic element validation failures. The location points to the first differing XML start tag in the original mapper XML.

## Consequences

Generic element attribute and structure changes now point to the XML tag most likely responsible for the validation error. The range is intentionally tag-level rather than attribute-level.
