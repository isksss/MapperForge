# 0094 Token Type Public Contract

## Status

Accepted

## Context

`TokenType` is part of the public source model used by tokenizer tests, parser tests, and future integrations. Its enum names are visible to plugin consumers, reports, and possible IDE integrations, but the enum order was only implied by implementation.

## Decision

Add Japanese Javadoc to `TokenType` and guard the enum names and order in `SqlTokenizerTest`.

The order remains:

```text
IDENTIFIER, KEYWORD, STRING, NUMBER, SYMBOL, PLACEHOLDER, COMMENT, EOF, UNKNOWN
```

## Consequences

Changing token classifications now requires an explicit test and ADR update. Existing parser and formatter behavior remains unchanged.
