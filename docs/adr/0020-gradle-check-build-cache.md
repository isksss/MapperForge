# 0020 Gradle Check Build Cache

## Status

Accepted

## Context

PLAN.local.md requires Gradle Build Cache support. MapperForge has three Gradle tasks, but `mapperForgeFormat` mutates source files in place and `mapperForgeDryRun` prints diffs to standard output. Caching those tasks would hide user-visible side effects.

## Decision

Only `mapperForgeCheck` is marked cacheable.

- Split Gradle tasks into mode-specific task classes
- Mark `MapperForgeCheckTask` with `@CacheableTask`
- Keep `MapperForgeFormatTask` and `MapperForgeDryRunTask` non-cacheable with explicit reasons
- Add a marker output file under `build/mapperforge/` so Gradle can track successful check execution
- Keep formatter configuration and source files as task inputs, so FormatterVersion, FormatterConfig, plugin implementation, and file contents participate in Gradle's cache key

## Consequences

Successful check runs can be up-to-date and build-cache friendly. Format and dry-run remain deterministic but intentionally non-cacheable because their primary behavior is side-effectful.
