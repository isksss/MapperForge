# 0100 OGNL AST Printer Public Contract

## Status

Accepted

## Context

`OgnlAstPrinter` is the public printer used by `OgnlFormatter` after successful OGNL parsing. Its spacing, precedence handling, collection rendering, and unknown-expression passthrough define the formatter output surface.

## Decision

Add Japanese Javadoc to `OgnlAstPrinter` and extend `OgnlAstPrinterTest` with contract tests for:

- collection values rendered without added spaces
- `OgnlUnknownExpression` returning its raw value
- existing stable operator spacing and required parentheses behavior

## Consequences

OGNL formatter output remains stable for dynamic MyBatis attributes. Changing printer spacing or unknown passthrough now requires an explicit test and ADR update.
