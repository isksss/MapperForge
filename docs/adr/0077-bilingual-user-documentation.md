# 0077 Bilingual User Documentation

## Status

Accepted

## Context

MapperForge user-facing documentation existed mainly in English under `docs/`.
The project now requires Japanese documentation to be prepared together with documentation changes, while keeping GitHub distribution, configuration, task, golden test, release, and traceability guidance easy to find.

## Decision

Add Japanese versions of the current user-facing documents under `docs/ja/`:

- `README.md`
- `configuration.md`
- `tasks.md`
- `golden-tests.md`
- `release.md`
- `traceability.md`

Link the Japanese documentation from the root README and keep future user-facing documentation updates paired in English and Japanese.

## Consequences

Japanese users can follow the same plugin setup, task behavior, golden test workflow, release procedure, and traceability information without relying on English-only docs.
Future documentation changes must update both language versions or explicitly record why a document is language-specific.
