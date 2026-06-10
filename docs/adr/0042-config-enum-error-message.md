# 0042 Config Enum Error Message

## Status

Accepted

## Context

MapperForge exposes enum-backed configuration values such as `dialect`, `sqlFormatStyle`, `sqlPrinter`, `tagWrapStyle`, and `attributeLayout`. Invalid values previously surfaced as Java enum errors, which are less useful to Gradle and YAML users.

## Decision

`ConfigLoader` reports invalid enum values as `CONFIG_ERROR` with the config key and allowed values.

## Consequences

Users get stable, actionable messages for enum-backed configuration mistakes.
