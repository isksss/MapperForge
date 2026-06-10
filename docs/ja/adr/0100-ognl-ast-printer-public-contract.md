# 0100 OGNL AST Printer Public Contract

## Status

Accepted

## Context

`OgnlAstPrinter` は OGNL parse 成功後に `OgnlFormatter` が使う公開 printer です。空白、precedence handling、collection rendering、unknown-expression passthrough は formatter output surface を定義します。

## Decision

`OgnlAstPrinter` に日本語 Javadoc を追加し、`OgnlAstPrinterTest` に以下の契約テストを追加します。

- collection values は追加空白なしで出力すること
- `OgnlUnknownExpression` は raw value を返すこと
- 既存の operator spacing と必要な括弧の挙動を維持すること

## Consequences

dynamic MyBatis 属性向けの OGNL formatter output が安定します。printer の空白や unknown passthrough を変更する場合は、test と ADR の明示更新が必要です。
