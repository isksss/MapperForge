# 0096 SQL Statement Parser Public Contract

## Status

Accepted

## Context

`SqlStatementParser` is a public parser entry point for statement AST construction. It already strips outer whitespace, ignores SQL comments while classifying the statement kind, and returns `UnknownStatement` for unsupported or blank SQL, but those entry point behaviors were not documented as a public contract.

## Decision

Add Japanese Javadoc to `SqlStatementParser` and extend `SqlStatementParserTest` with contract tests for:

- outer whitespace stripping
- SQL comments excluded from statement classification
- blank SQL falling back to `UnknownStatement` with stripped raw SQL

## Consequences

Parser callers can rely on recoverable behavior for incomplete and unsupported SQL. Future changes to statement classification must update tests and this ADR.
