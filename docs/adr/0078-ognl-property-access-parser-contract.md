# 0078 OGNL Property Access Parser Contract

## Status

Accepted

## Context

`PLAN.local.md` lists OGNL property access and comparison operators as parser targets.
Formatter and printer tests already exercised property-like names indirectly, but the OGNL parser contract did not directly assert that dotted property access remains a stable name while parsing comparison operators.

## Decision

Add a focused parser test for dotted property access with `>=` and `<=` inside a boolean expression.
Add Japanese Javadocs to the public `OgnlExpressionParser` entry point touched by this contract.

## Consequences

Future OGNL parser changes must preserve dotted property access names and comparison operator parsing unless the contract and ADR are explicitly updated.
The parser entry point is also clearer in generated Japanese API documentation.
