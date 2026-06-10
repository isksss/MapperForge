# ADR 0007: Recoverable SQL Parser Entry Point

## Status

Accepted

## Context

The design calls for a complete SQL AST with a tokenizer, recursive-descent statement parser, Pratt expression parser, and `UnknownStatement` / `UnknownExpression` recovery.
Building the full SQL parser is incremental work, but mapper parsing and validation need a stable SQL parser entry point now.

## Decision

- Add a tokenizer that preserves token text, token type, and source range.
- Add a statement parser that identifies supported top-level statement kinds and set operations.
- Preserve raw SQL in statement nodes until deeper statement and expression ASTs are implemented.
- Fall back to `UnknownStatement` for unsupported or unclear syntax.
- Attach parsed SQL to CDATA nodes and mark SQL-bearing text as `TextType.SQL`.

## Consequences

Future SQL parser work can refine statement internals without changing the parser entry point.
Validation and mapper AST work can distinguish known SQL statements from unknown SQL safely.
