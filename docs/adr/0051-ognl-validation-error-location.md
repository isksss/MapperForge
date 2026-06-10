# 0051 Ognl Validation Error Location

## Status

Accepted

## Context

PLAN.local.md defines `ValidationError` with a `Range location` and lists OGNL expressions as a semantic validation target. Expression validation detected semantic changes, but the reported location was still empty.

## Decision

Attach a source `Range` to OGNL expression validation failures. The location points to the first differing OGNL attribute value in the original mapper XML for `if`/`when` `test`, `bind` `value`, and `foreach` `collection`.

## Consequences

OGNL semantic changes are now traceable to the attribute value that caused the validation error. The range intentionally covers the full expression value rather than a narrower AST node.
