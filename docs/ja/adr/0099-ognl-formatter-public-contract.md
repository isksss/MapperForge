# 0099 OGNL Formatter Public Contract

## Status

Accepted

## Context

`OgnlFormatter` は dynamic SQL 属性の整形で使われる公開 formatting entry point です。parse に成功した場合は OGNL AST printer を使い、未対応 OGNL では legacy token formatter に fallback します。MyBatis 利用者は現在の parser coverage を超える OGNL を書く可能性があるため、この recoverable behavior は重要です。

## Decision

`OgnlFormatter` に日本語 Javadoc を追加し、`OgnlFormatterTest` に以下の契約テストを追加します。

- 空 OGNL は空文字を返すこと
- legacy fallback が string literal の中身を保持すること
- 未対応 ternary expression は fallback で整形し続けること

## Consequences

dynamic attribute formatting は、未対応 OGNL でも parser 例外や literal rewrite なしに利用できます。将来 formatter 挙動を変更する場合は、test と ADR の更新が必要です。
