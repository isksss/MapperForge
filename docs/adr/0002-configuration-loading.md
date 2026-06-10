# ADR 0002: Configuration Loading Priority

## Status

Accepted

## Context

MapperForge supports Gradle configuration and a supplemental `mapperforge.yml`.
The design specifies this priority:

1. `build.gradle`
2. `mapperforge.yml`
3. defaults

## Decision

- Keep the Gradle extension as explicit user input only.
- Load `mapperforge.yml` with SnakeYAML Engine.
- Merge configuration inside Gradle task configuration using `extension.orElse(yamlOrDefault)`.
- Treat unknown YAML keys as ignored for now so older/newer config files can coexist during early development.

## Consequences

YAML values can override defaults, while Gradle DSL values still override YAML.
Validation of unknown keys can be added later when the configuration schema stabilizes.
