# 0030 SQL Quoted Identifier Tokenization

## Status

Accepted

## Context

MapperForge targets PostgreSQL and MySQL. PostgreSQL commonly uses double-quoted identifiers and MySQL commonly uses backtick-quoted identifiers. The SQL tokenizer previously treated double quotes as strings and backticks as symbols, so quoted table/column names could not be parsed into normal `ColumnExpression` nodes.

## Decision

Tokenize SQL quoted identifiers as `IDENTIFIER`.

- PostgreSQL-style `"identifier"` is an identifier
- MySQL-style `` `identifier` `` is an identifier
- Dotted quoted identifiers such as `"user"."id"` and `` `user`.`id` `` are consumed as one logical identifier token
- Single-quoted values remain string literals

## Consequences

SQL parser, validator, and AST printer can handle common quoted identifier forms for both supported DB dialects without treating the expression as unknown.
