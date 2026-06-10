# 0085 Mapper Element List Immutability

## ステータス

採用

## 背景

`PLAN.local.md` は AST ノードの不変性を要求している。mapper element の record は構造を record component として公開しているが、リスト component はコンストラクタ引数を保持すると外部から変更される余地が残る。

## 決定

すべての `ElementNode` 実装は compact constructor で `attributes` と `children` を `ElementNode.copyAttributes` / `ElementNode.copyChildren` に通して不変コピーへ変換する。

`AstImmutabilityTest` は、全 mapper element node についてコンストラクタへ渡した元リストの変更と accessor 経由の変更を検証する。

## 結果

Mapper AST の利用者は、ノード生成後に属性や子ノードを変更できない。今後 mapper element node を追加する場合も同じ防御的コピー契約を維持し、契約テストへ追加する。
