# 0095 SQL Tokenizer Public Contract

## Status

Accepted

## Context

`SqlTokenizer` is a public parser entry point used by parser tests and future integrations. It already normalizes keywords to uppercase, preserves string and identifier text, appends an EOF token, and returns an immutable token list, but these behaviors were not fully documented as a public contract.

## Decision

Add Japanese Javadoc to `SqlTokenizer` and extend `SqlTokenizerTest` with a contract test for:

- immutable returned token list
- trailing `EOF` token
- EOF range at the current source position after tokenization

## Consequences

Callers can rely on the token stream shape and immutability. Any future tokenizer behavior change must update the tests and this ADR explicitly.
