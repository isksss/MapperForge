# 0097 SQL Expression Parser Public Contract

## Status

Accepted

## Context

`SqlExpressionParser` は SQL expression AST を構築する公開 Pratt parser entry point です。外側の空白を取り除くこと、SQL コメントを構文解析から除外すること、未対応または不正な入力を `UnknownExpression` に退避することは実装されていますが、特定 expression 向けテストに暗黙依存していました。

## Decision

`SqlExpressionParser` に日本語 Javadoc を追加し、`SqlExpressionParserTest` に以下の契約テストを追加します。

- 外側の空白を取り除くこと
- SQL コメントを expression 解析から除外すること
- 空 SQL は strip 後の raw SQL を持つ `UnknownExpression` へ退避すること

## Consequences

利用者は未対応 expression に対する parser 例外処理なしで、mapper SQL の一部を解析できます。将来 parser 挙動を変更する場合は、test と ADR の更新が必要です。
