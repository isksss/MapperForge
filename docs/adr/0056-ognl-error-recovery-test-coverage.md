# 0056 OGNL Error Recovery Test Coverage

## Status

Accepted

## Context

PLAN.local.md requires recoverable parsing and error recovery tests for unknown expressions. SQL unknown recovery had explicit tests, while OGNL recovery was implemented but not directly covered by parser tests.

## Decision

Add OGNL parser tests that assert unsupported trailing tokens and malformed expressions return `OgnlUnknownExpression` with the original raw expression preserved.

## Consequences

The test suite now guards the OGNL recoverable parser contract and prevents future changes from throwing or partially accepting unsupported expressions.
