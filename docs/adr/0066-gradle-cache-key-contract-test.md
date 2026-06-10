# 0066 Gradle Cache Key Contract Test

## Status

Accepted

## Context

`PLAN.local.md` requires Gradle Build Cache support and identifies `FormatterVersion`, `FormatterConfig`, plugin implementation, and file hash as cache key inputs. MapperForge already marks `mapperForgeCheck` as cacheable and keeps formatting and dry-run tasks non-cacheable, but the tests only checked task-level cache annotations.

## Decision

Extend `MapperForgeTaskCacheAnnotationTest` to verify the Gradle task input/output annotation contract:

- Formatter mode and all formatter configuration properties are `@Input`.
- Source files are `@InputFiles`.
- The task state file is `@OutputFile`.
- `mapperForgeCheck` remains `@CacheableTask`.
- `mapperForgeFormat` and `mapperForgeDryRun` remain explicitly non-cacheable.

Gradle's task implementation classpath supplies the plugin implementation portion of the cache key.

## Consequences

Cache-relevant task properties are now guarded by tests. Future config additions must be added to the task input contract or the cache key evidence will become incomplete.
