# 0083 FormatterConfig default 契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は dialect、file selection、layout、SQL formatting、CDATA、whitespace、strict mode、attribute ordering の default を定義しています。
`ConfigLoaderTest` は YAML file が存在しない場合に `FormatterConfig.defaults()` を返すことを検証していましたが、具体的な default 値は公開契約として直接固定されていませんでした。

## 決定

`FormatterConfigTest` を追加し、`FormatterConfig.defaults()` の全項目を検証します。
`FormatterConfig` と公開 configuration enum に日本語 Javadoc を追加します。

## 結果

default formatter behavior を変更する場合は、test と ADR の明示的な更新が必要になります。
設定モデルは生成される日本語 API documentation でも確認しやすくなります。
