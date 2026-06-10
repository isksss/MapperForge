# 0098 SQL Formatter Public Contract

## Status

Accepted

## Context

`SqlFormatter` is a public formatting entry point used by `MapperXmlFormatter`, tests, and future integrations. It supports AST printing, legacy regex formatting, single-line formatting, and blank SQL handling. AST printing is intentionally recoverable because the SQL parser does not cover every possible dialect feature.

## Decision

Add Japanese Javadoc to `SqlFormatter` and extend `SqlFormatterTest` with contract tests for:

- blank SQL returning an empty string
- AST printer fallback to legacy formatting for unsupported SQL
- `formatLegacy` bypassing the AST printer even when config requests `SqlPrinter.AST`

## Consequences

Callers can safely request AST formatting without losing output for unsupported SQL. The legacy formatter remains a stable escape hatch.
