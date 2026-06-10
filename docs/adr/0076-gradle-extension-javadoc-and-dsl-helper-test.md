# 0076 Gradle Extension Javadoc And DSL Helper Contract Test

## Status

Accepted

## Context

`MapperForgeExtension` is the public Gradle DSL surface exposed as `mapperForge { ... }`.
The extension needs Japanese Javadocs because the generated API documentation is user-facing.
The `attributeOrder(String, List<String>)` helper also needed an explicit JUnit contract test so the public extension behavior stays covered without depending on formatter validation details.

## Decision

Add Japanese Javadocs to the Gradle plugin entry point and extension accessors.
Add a Gradle `ProjectBuilder` JUnit test that applies the plugin, obtains `MapperForgeExtension`, and verifies `attributeOrder(...)` adds and overwrites the configured order.

## Consequences

The public Gradle DSL is easier to document for Japanese users, and the helper method has an executable compatibility contract.
Future extension changes should update both Javadoc and functional coverage when they affect the public DSL.
