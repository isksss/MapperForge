# 0083 Formatter Config Default Contract

## Status

Accepted

## Context

`PLAN.local.md` defines formatter defaults for dialect, file selection, layout, SQL formatting, CDATA, whitespace, strict mode, and attribute ordering.
`ConfigLoaderTest` verified that a missing YAML file returns `FormatterConfig.defaults()`, but the concrete default values were not directly guarded as a public contract.

## Decision

Add `FormatterConfigTest` to assert every `FormatterConfig.defaults()` value.
Add Japanese Javadocs to `FormatterConfig` and the public configuration enums.

## Consequences

Changing default formatter behavior now requires an explicit test and ADR update.
The configuration model is documented for generated Japanese API documentation.
