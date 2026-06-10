# 0027 CDATA Preservation Toggle

## Status

Accepted

## Context

`preserveCdata` existed in `FormatterConfig`, but formatter and validator always treated CDATA wrappers as preserved. PLAN.local.md defines CDATA preservation as configurable, and CDATA often appears around XML-sensitive SQL operators.

## Decision

When `preserveCdata=false`, formatter converts CDATA content to normal XML text.

- SQL text is formatted like other mapper SQL text
- XML-sensitive characters are escaped in text output
- Validator treats CDATA wrapper removal as acceptable when `preserveCdata=false`
- Default remains `preserveCdata=true`

## Consequences

Users can opt out of CDATA wrapper preservation while keeping XML output valid and idempotent. Default behavior and existing golden files remain unchanged.
