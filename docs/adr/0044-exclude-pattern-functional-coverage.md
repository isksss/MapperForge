# 0044 Exclude Pattern Functional Coverage

## Status

Accepted

## Context

PLAN.local.md defines include and exclude patterns for target mapper XML files. Functional tests covered include overrides, but exclude behavior was not explicitly verified.

## Decision

Add a Gradle functional test that configures `exclude = ["**/legacy/**"]`, then verifies active mapper files are formatted while matching legacy mapper files remain unchanged.

## Consequences

The target-file filtering contract now covers both include and exclude patterns.
