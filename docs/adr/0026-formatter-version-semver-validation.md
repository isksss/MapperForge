# 0026 Formatter Version SemVer Validation

## Status

Accepted

## Context

PLAN.local.md states that `formatterVersion` uses SemVer. The value participates in formatter configuration and Gradle cache keys, but it was previously accepted as an arbitrary string from both `mapperforge.yml` and the Gradle DSL.

## Decision

Validate `formatterVersion` in `FormatterConfig`.

- Accept `MAJOR.MINOR.PATCH`
- Accept optional prerelease and build metadata
- Reject arbitrary strings such as `latest`
- Apply validation uniformly to defaults, YAML config, and Gradle DSL config

## Consequences

Invalid formatter versions fail early and consistently. Cache keys and future report output can rely on a stable version format.
