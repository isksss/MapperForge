# 0087 Mapper Leaf Node Lossless Contract

## ステータス

採用

## 背景

`PLAN.local.md` はコメント、CDATA、プレースホルダ、未知ノード、汎用要素の lossless 保持を要求している。Mapper leaf node は validation と formatting が保持すべきテキスト境界を担い、`<`、`>`、`&` のような XML エスケープ必須文字を含む CDATA もそのまま扱う必要がある。

## 決定

mapper leaf の record と enum に日本語 Javadoc を追加する。

`MapperAstLeafNodeTest` を追加し、`AttributeNode`、`TextNode`、`CommentNode`、`CDataNode` の値が正確に保持されることを検証する。CDATA については `<`、`>`、`&` を含む入力も対象にする。

## 結果

今後 mapper leaf node を変更する場合、別 ADR で明示的に意味変更を記録しない限り、raw テキスト値を正確に保持する必要がある。
