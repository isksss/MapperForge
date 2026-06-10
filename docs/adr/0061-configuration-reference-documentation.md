# 0061 Configuration Reference Documentation

## Status

Accepted

## Context

MapperForge exposes configuration through the Gradle extension and `mapperforge.yml`. README showed only a minimal example, while the implementation supports multiple formatting, validation, CDATA, and attribute ordering options.

## Decision

Add `docs/configuration.md` as the canonical configuration reference and link it from README. The document lists Gradle DSL and YAML examples, defaults, allowed values, and behavior notes for strict mode, CDATA, and SQL printer selection.

## Consequences

Users can configure the plugin without reading implementation classes, and future option changes have a single documentation target.
