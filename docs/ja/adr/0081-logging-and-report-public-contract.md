# 0081 Logging と Report の公開契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は SLF4J logging category を `parser`、`formatter`、`validator`、`gradle` の順で定義し、Report も拡張対象として挙げています。
実装では logger の集約と `ValidationReportFormatter` の公開は済んでいましたが、PLAN 上の category 順序と公開 API documentation は直接固定されていませんでした。

## 決定

`MapperForgeLoggersTest` を拡張し、PLAN の logging category を PLAN 順で検証します。
`MapperForgeLoggers` と `ValidationReportFormatter` に日本語 Javadoc を追加します。

## 結果

Logging category を変更する場合は、test と ADR の明示的な更新が必要になります。
Report formatter は今後の CLI、IDE、report integration 向けの公開 entry point として文書化されます。
