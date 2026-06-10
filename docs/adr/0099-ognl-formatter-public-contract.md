# 0099 OGNL Formatter Public Contract

## Status

Accepted

## Context

`OgnlFormatter` is a public formatting entry point used by dynamic SQL attribute formatting. It uses the OGNL AST printer when parsing succeeds and falls back to a legacy token formatter for unsupported OGNL. This recoverable behavior is important because MyBatis users may write OGNL constructs beyond the current parser coverage.

## Decision

Add Japanese Javadoc to `OgnlFormatter` and extend `OgnlFormatterTest` with contract tests for:

- blank OGNL returning an empty string
- legacy fallback preserving string literal content
- unsupported ternary expressions continuing to format via fallback

## Consequences

Dynamic attribute formatting remains usable for unsupported OGNL without parser exceptions or literal rewrites. Future formatter behavior changes must update tests and this ADR.
