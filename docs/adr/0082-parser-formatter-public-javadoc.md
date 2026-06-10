# 0082 Parser Formatter Public Javadoc

## Status

Accepted

## Context

`PLAN.local.md` defines Mapper XML parsing, formatting, recoverable parser behavior, and stable parser/formatter error classifications.
The parser and formatter implementations already existed and tests covered parser and formatter error codes, but the public entry points still lacked Japanese Javadocs.

## Decision

Add Japanese Javadocs to `MapperXmlParser`, `MapperXmlFormatter`, and their public exception types.
Clarify traceability so parser and formatter error code contracts point to the concrete tests that cover them.

## Consequences

Generated API documentation now describes the XML parser and formatter entry points in Japanese.
Future changes to parser/formatter public behavior should update Javadocs, tests, and ADRs together.
