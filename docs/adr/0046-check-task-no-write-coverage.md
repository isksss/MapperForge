# 0046 Check Task No-Write Coverage

## Status

Accepted

## Context

PLAN.local.md defines `Check` separately from `Format`. `mapperForgeCheck` should report that formatting is required without modifying mapper XML files.

## Decision

Extend the Gradle check functional test to assert that the original mapper XML remains unchanged when `mapperForgeCheck` fails because formatting is required.

## Consequences

The test suite now covers the no-write contract for both check and dry-run modes.
