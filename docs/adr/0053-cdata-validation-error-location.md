# 0053 CDATA Validation Error Location

## Status

Accepted

## Context

PLAN.local.md requires CDATA preservation by default and defines `ValidationError` with a source `Range`. CDATA validation detected raw CDATA changes but did not identify the affected source range.

## Decision

Attach a source `Range` to CDATA validation failures. The location points to the first differing CDATA section in the original mapper XML.

## Consequences

CDATA preservation errors now point to the changed CDATA region. This complements the existing placeholder, SQL statement, OGNL, and comment validation locations.
