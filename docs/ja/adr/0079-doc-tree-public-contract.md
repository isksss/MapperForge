# 0079 Doc Tree 公開契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は printer の経路を `AST -> Doc Tree -> Layout Engine -> String` と定義し、`TextDoc`、`LineDoc`、`SoftLineDoc`、`HardLineDoc`、`IndentDoc`、`GroupDoc`、`ConcatDoc` を列挙しています。
Layout engine には描画テストがありましたが、公開 Doc Tree factory と node 契約は間接的にしか検証されていませんでした。

## 決定

`DocTreeTest` を追加し、`Docs` が PLAN 定義の Doc node type をすべて生成すること、`TextDoc` が null text を空文字へ正規化すること、`ConcatDoc` が子 node を防御コピーすることを検証します。
公開 Doc Tree node type、`Docs`、`LayoutEngine` に日本語 Javadoc を追加します。

## 結果

今後の printer 作業では、公開 document model に対する直接の互換性契約があります。
Doc Tree の構築や immutability semantics を変更する場合は、focused test とこの ADR の更新が必要になります。
