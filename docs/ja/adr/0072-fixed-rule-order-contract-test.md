# 0072 固定ルール順序の契約テスト

## ステータス

採用

## コンテキスト

`PLAN.local.md` は v1 の Rule Engine を次の固定パイプラインとして定義している。

1. `NormalizeRule`
2. `AttributeOrderRule`
3. `OgnlFormatRule`
4. `SqlFormatRule`
5. `WrapRule`

MapperForge はすでに `FormatterRulePipeline.v1()` でこの順序を使っており、既存テストは組み合わせた挙動を検証していた。しかし、順序そのものを直接検証するテストはなかった。

## 決定

`FormatterRulePipelineTest` に `v1()` のルール順序を検証する契約テストを追加する。あわせて、この契約に関係する公開 Rule API と Rule クラスへ日本語 Javadoc を追加する。

## 結果

v1 のルール順序を変更する場合は、テスト更新と ADR レベルの判断が必要になる。日本語 Javadoc により、生成ドキュメント上でも Rule API の意図を確認できる。
