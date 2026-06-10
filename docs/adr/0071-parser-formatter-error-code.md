# 0071 Parser And Formatter Error Code

## Status

Accepted

## Context

`PLAN.local.md` defines broad `ErrorCode` values including `PARSER_ERROR` and `FORMAT_ERROR`. Validation errors and configuration errors already exposed stable codes, but XML parser and formatter exceptions only carried messages and causes.

## Decision

Add `code()` to `MapperXmlParser.ParserException` and `MapperXmlFormatter.FormatterException`.

- Parser failures return `ErrorCode.PARSER_ERROR`.
- Formatter failures return `ErrorCode.FORMAT_ERROR`.
- Existing constructors remain compatible.

## Consequences

Callers can classify parser, formatter, validation, and configuration failures through the same error-code vocabulary. Future report or IDE integrations can surface these categories without parsing exception messages.
