# 0086 SQL and OGNL List Immutability Contract

## ステータス

採用

## 背景

`PLAN.local.md` は AST ノードの不変性を要求している。SQL と OGNL の式 record はリスト component を防御的コピーしているが、その振る舞いを明示する契約テストが不足していた。

## 決定

リストを持つ SQL/OGNL 式 record は、コンストラクタ引数を `List.copyOf` で防御的コピーする契約を維持する。

`AstImmutabilityTest` を拡張し、SQL/OGNL 式のリスト accessor が元のコンストラクタ引数から独立し、変更操作を拒否することを検証する。

## 結果

今後リスト component を持つ SQL/OGNL AST を追加する場合も、同じ不変リスト契約を維持し、アーキテクチャテストへ追加する。
