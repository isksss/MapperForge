# 0033 Formatter Config Range Validation

## Status

Accepted

## Context

PLAN.local.md defines `indentSize`, `maxLineLength`, and `lineEnding` as formatter inputs. Invalid values could previously reach formatter and layout code, making output unstable or failing later with less specific errors.

## Decision

`FormatterConfig` validates core scalar formatting options at construction time:

- `indentSize >= 0`
- `maxLineLength > 0`
- `lineEnding` is LF or CRLF, including the existing `LF`, `CRLF`, `\n`, and `\r\n` aliases

## Consequences

The same validation applies to defaults, YAML config, Gradle extension values, tests, and direct Java API usage.
