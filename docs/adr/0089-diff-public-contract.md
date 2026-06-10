# 0089 Diff Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines the diff engine as AST diff plus unified diff. `AstDiff` and `UnifiedDiff` are public entry points used by the facade and Gradle dry-run behavior, but their public contract and trailing-newline behavior were not documented directly.

## Decision

Add Japanese Javadocs to `AstDiff` and `UnifiedDiff`.

Extend `UnifiedDiffTest` to lock the behavior for changed content without a trailing newline. The diff still reports full-file ranges and line edits, and returns an empty string when content is identical.

## Consequences

The public diff API is documented as stable behavior. Future changes to unified diff line splitting or AST descriptor output must update the focused tests and this contract.
