# 0096 SQL Statement Parser Public Contract

## Status

Accepted

## Context

`SqlStatementParser` は statement AST を構築する公開 parser entry point です。外側の空白を取り除くこと、statement 種別判定では SQL コメントを除外すること、未対応または空 SQL を `UnknownStatement` として返すことは既に実装されていますが、公開契約として文書化されていませんでした。

## Decision

`SqlStatementParser` に日本語 Javadoc を追加し、`SqlStatementParserTest` に以下の契約テストを追加します。

- 外側の空白を取り除くこと
- statement 種別判定から SQL コメントを除外すること
- 空 SQL は strip 後の raw SQL を持つ `UnknownStatement` へ退避すること

## Consequences

parser 利用者は、未完成または未対応 SQL に対する recoverable な挙動に依存できます。将来 statement 種別判定を変更する場合は、test と ADR の更新が必要です。
