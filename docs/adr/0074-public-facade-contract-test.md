# 0074 Public Facade Contract Test

## Status

Accepted

## Context

`PLAN.local.md` defines Format, Check, Diff, and Validation as core capabilities. The Gradle tasks and lower-level components already had tests, but the public `MapperForge` facade did not have a focused contract test or Japanese Javadoc.

## Decision

Add Japanese Javadoc to the public `MapperForge` API and add `MapperForgeTest`.

The test verifies that the facade can format mapper XML, report formatted status, validate formatting-only changes, and return a unified diff through the same entry point.

## Consequences

The public Java API is now documented and directly covered. Future changes to the facade must preserve the core PLAN capabilities or update the test and ADR explicitly.
