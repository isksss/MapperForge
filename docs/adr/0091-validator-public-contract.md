# 0091 Validator Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines validation as a core feature for preventing SQL, OGNL, XML, placeholder, comment, CDATA, and whitespace semantic changes. `Validator` is the public boundary used by the facade and Gradle tasks, but its public contract and returned result snapshot behavior were not documented directly.

## Decision

Add Japanese Javadocs to `Validator` and `Validator.validate`.

Extend `ValidatorTest` to verify that validation failures returned from the public `validate` entry point expose an immutable error list.

## Consequences

Callers can treat `Validator.validate` results as stable snapshots. Future validation categories must preserve the public `ValidationResult` contract.
