# 0075 Error 分類の契約テスト

## ステータス

採用

## コンテキスト

`PLAN.local.md` は安定した上位エラー分類として `ErrorCode` を定義している。MapperForge は validation、configuration、parser、formatter、report、Gradle 出力で error code を既に使っているが、enum 値と順序はテストや日本語 Javadoc で直接固定されていなかった。

## 決定

`ErrorCode` と `ErrorType` に日本語 Javadoc を追加し、`ValidationErrorTest` に enum 契約テストを追加する。

このテストでは `ErrorCode` が PLAN の分類順序と一致すること、`ErrorType` が現在の validation 差分分類を網羅していることを検証する。

## 結果

report、Gradle、将来の IDE 連携は安定した error classification 名に依存できる。これらの値を追加、削除、並べ替えする場合は、テストと ADR の明示的な更新が必要になる。
