# 0080 Gradle Cache Relocatability Contract

## Status

Accepted

## Context

`PLAN.local.md` lists Gradle Build Cache support and identifies file hash as a cache key input.
MapperForge already annotated task inputs and outputs, but the source file path sensitivity was not directly covered by a test.
For Gradle cache reuse across checkout directories, mapper XML inputs must use relative path sensitivity.

## Decision

Extend `MapperForgeTaskCacheAnnotationTest` to assert that `getSourceFiles()` is annotated with `@PathSensitive(PathSensitivity.RELATIVE)`.
Add Japanese Javadocs to the public `MapperForgeTask` API touched by the cache contract.

## Consequences

Future task input changes must preserve relative path sensitivity unless the cache contract is intentionally revised.
The Gradle task API is also clearer in generated Japanese API documentation.
