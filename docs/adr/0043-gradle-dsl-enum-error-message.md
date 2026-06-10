# 0043 Gradle DSL Enum Error Message

## Status

Accepted

## Context

ADR 0042 improved enum-backed configuration errors for `mapperforge.yml`. Gradle DSL values are also strings and previously flowed through Java `Enum.valueOf`, producing less actionable errors for invalid values.

## Decision

Gradle task config parsing reports invalid enum values as `CONFIG_ERROR` with the DSL property name and allowed values.

## Consequences

YAML and Gradle DSL configuration errors now use consistent wording for enum-backed options.
