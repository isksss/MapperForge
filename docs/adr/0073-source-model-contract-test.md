# 0073 Source Model Contract Test

## Status

Accepted

## Context

`PLAN.local.md` defines `SourceFile`, `Position`, `Range`, and `Token` as the source management records used by parser, tokenizer, validator, diff, and report code. The records already existed, but their contract was only implicit through downstream tests.

## Decision

Add Japanese Javadoc to the source model records and add `SourceModelTest`.

The test verifies that `SourceFile`, `Position`, `Range`, and `Token` remain Java records and documents the coordinate convention: offsets are zero-based, while line and column values are one-based.

## Consequences

Future source model changes must preserve the documented record-based API and coordinate convention, or update the contract test and ADR explicitly.
