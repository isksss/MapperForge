# 0091 Validator Public Contract

## ステータス

採用

## 背景

`PLAN.local.md` は SQL、OGNL、XML、placeholder、comment、CDATA、whitespace の意味変更を防ぐ中核機能として validation を定義している。`Validator` は facade と Gradle task から使う公開境界だが、公開契約と返却 result の snapshot 性が直接は文書化されていなかった。

## 決定

`Validator` と `Validator.validate` に日本語 Javadoc を追加する。

`ValidatorTest` を拡張し、公開 `validate` 入口から返る validation failure の error list が不変であることを検証する。

## 結果

呼び出し元は `Validator.validate` の result を安定した snapshot として扱える。今後 validation category を追加する場合も、公開 `ValidationResult` 契約を維持する必要がある。
