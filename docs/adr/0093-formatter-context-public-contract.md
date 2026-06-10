# 0093 Formatter Context Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines a fixed formatter rule pipeline. `FormatterContext` is the shared object passed to formatter rules, and the existing rule API exposes it as a mutable context. That mutability was implied by setters but not documented or tested directly.

## Decision

Add Japanese Javadocs to `FormatterContext`.

Extend `FormatterRulePipelineTest` with a focused contract test that verifies `setConfig` and `setSource` replace the context values returned by `config()` and `source()`.

## Consequences

Rule implementations can rely on `FormatterContext` as the mutable context boundary. If the context becomes immutable later, the rule API contract and this ADR must be updated together.
