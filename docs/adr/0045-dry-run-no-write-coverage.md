# 0045 DryRun No-Write Coverage

## Status

Accepted

## Context

PLAN.local.md defines `DryRun` as a distinct task. Existing tests verified dry-run diff output, but did not explicitly verify that the mapper file remains unchanged.

## Decision

Extend the Gradle dry-run functional test to assert that `mapperForgeDryRun` prints the diff without writing the formatted content back to the file.

## Consequences

Dry-run remains a read-only reporting mode in the functional test suite.
