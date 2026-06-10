# 0028 Line Ending Normalization

## Status

Accepted

## Context

`lineEnding` is part of formatter configuration. YAML config normalized aliases such as `LF` and `CRLF`, but Gradle DSL values were passed directly into `FormatterConfig`, which could write literal `CRLF` strings into formatted output.

## Decision

Normalize line ending aliases in `FormatterConfig`.

- `LF` and `\n` become newline
- `CRLF` and `\r\n` become carriage-return newline
- Normalization applies uniformly to defaults, YAML config, and Gradle DSL config

## Consequences

Line ending behavior is consistent regardless of configuration source, and Gradle DSL users can use the documented alias style.
