# 0088 Lowercase Maven Artifact ID

## Status

Accepted

## Context

The first GitHub Packages publish attempt for `v0.1.0` passed build and integration tests, then failed when uploading the main Maven publication. The failed request targeted an artifact path containing the project-name-derived artifact ID `MapperForge`.

GitHub Packages is stricter than local Maven publication for package coordinates. Keeping the public plugin ID as `io.github.isksss.mapperforge` while publishing the implementation artifact with a lowercase artifact ID avoids case-sensitive package path issues.

## Decision

Set the `pluginMaven` publication artifact ID explicitly to `mapperforge`.

The Gradle plugin marker publication remains generated from the plugin ID, and the implementation artifact uses:

```text
io.github.isksss:mapperforge:<version>
```

## Consequences

The failed `v0.1.0` publish is not reused. The next publish attempt should use a new release tag so GitHub Packages does not conflict with any partially uploaded package assets.
