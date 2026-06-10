# ADR 0003: Unified Diff First

## Status

Accepted

## Context

The design calls for both AST diff and unified diff.
The formatter currently has a conservative XML formatting pipeline, while the full mapper and SQL AST parser is still evolving.

## Decision

- Provide user-visible unified diff first.
- Use a deterministic line-based longest common subsequence implementation.
- Connect `mapperForgeDryRun` to the same diff API used by the Java library.
- Keep AST diff as a later enhancement once the parser AST is complete enough to compare semantic nodes.

## Consequences

Dry runs now show actionable file changes instead of only listing file names.
The diff is textual rather than semantic for now, so future AST diff work should be added without changing the dry-run command contract.
