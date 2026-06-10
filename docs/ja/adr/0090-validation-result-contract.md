# 0090 Validation Result Contract

## ステータス

採用

## 背景

`PLAN.local.md` は validation を中核機能として定義し、validation error が error code、error type、message、source range を持つことを要求している。`ValidationResult` は report formatting と Gradle task 振る舞いで使う公開 result object であるため、生成後に呼び出し元が error list を変更できてはならない。

## 決定

`ValidationError` と `ValidationResult` に日本語 Javadoc を追加する。

`ValidationResult` は `errors` を `List.copyOf` で防御的コピーする。さらに、コンストラクタ入力の変更と accessor 経由の変更が result に影響しないことを focused contract test で検証する。

## 結果

Validation result object は安定した snapshot になる。今後の validation report や task code は、result 生成後に error list が変わらないことを前提にできる。
