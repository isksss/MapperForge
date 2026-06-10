# 0084 PlaceholderExpression 契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は `PlaceholderExpression` を `PlaceholderType`、expression text、`Map<String,String>` options で定義しています。
Parser は `#{...}`、`${...}`、quoted option value を既に検証していましたが、公開 placeholder AST の option immutability は文書化・直接検証されていませんでした。

## 決定

`PlaceholderExpression`、`PlaceholderType`、`PlaceholderParser` に日本語 Javadoc を追加します。
`PlaceholderExpression` が options map を防御コピーするようにし、その契約を focused test で固定します。

## 結果

Placeholder options は安定した immutable AST data として扱えます。
今後 placeholder model を変更する場合は、この契約を維持するか、test と ADR を更新する必要があります。
