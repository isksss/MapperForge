# 0073 Source Model 契約テスト

## ステータス

採用

## コンテキスト

`PLAN.local.md` は parser、tokenizer、validator、diff、report で使う Source 管理レコードとして `SourceFile`、`Position`、`Range`、`Token` を定義している。これらの record は既に存在していたが、契約は下流のテストから間接的に確認されるだけだった。

## 決定

Source model の record に日本語 Javadoc を追加し、`SourceModelTest` を追加する。

このテストでは `SourceFile`、`Position`、`Range`、`Token` が Java record であり続けることを検証する。また、座標の規約として offset は 0 始まり、line と column は 1 始まりであることを明示する。

## 結果

今後 Source model を変更する場合は、record ベースの API と座標規約を維持するか、契約テストと ADR を明示的に更新する必要がある。
