# 0084 Placeholder Expression Contract

## Status

Accepted

## Context

`PLAN.local.md` defines `PlaceholderExpression` with `PlaceholderType`, expression text, and `Map<String,String>` options.
The parser already covered `#{...}`, `${...}`, and quoted option values, but the public placeholder AST did not document or directly guard option immutability.

## Decision

Add Japanese Javadocs to `PlaceholderExpression`, `PlaceholderType`, and `PlaceholderParser`.
Make `PlaceholderExpression` defensively copy its options map and add a focused test for that contract.

## Consequences

Placeholder options are stable immutable AST data.
Future placeholder model changes must preserve this contract or update the test and ADR.
