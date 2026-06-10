# 0031 SQL Comment Tokenization

## Status

Accepted

## Context

SQL comments are part of mapper source text and must be preserved by formatting, but comments should not affect semantic validation of SQL statements.

Line comments are especially sensitive because collapsing all whitespace can extend a `--` comment over following SQL tokens.

## Decision

MapperForge tokenizes SQL line comments and block comments as `COMMENT`.

Statement and expression parsers ignore `COMMENT` tokens for structural parsing and semantic signatures. The legacy SQL formatter preserves comments and protects comment text from keyword uppercasing and clause wrapping.

## Consequences

- `--` comments keep a line break boundary during formatting.
- `/* ... */` comments remain in place.
- SQL keywords inside comments are not rewritten.
- Comments are not represented in SQL AST signatures.
