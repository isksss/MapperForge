# 0089 Diff Public Contract

## ステータス

採用

## 背景

`PLAN.local.md` は diff engine を AST diff と unified diff の組み合わせとして定義している。`AstDiff` と `UnifiedDiff` は facade と Gradle dry-run 振る舞いから使われる公開入口だが、公開契約と末尾改行なし入力の扱いが直接は文書化されていなかった。

## 決定

`AstDiff` と `UnifiedDiff` に日本語 Javadoc を追加する。

`UnifiedDiffTest` を拡張し、末尾改行がない変更済み content の振る舞いを固定する。diff は引き続き file 全体の range と line edit を表示し、content が同一なら空文字を返す。

## 結果

公開 diff API の振る舞いを安定契約として文書化する。今後 unified diff の line split や AST descriptor 出力を変更する場合は、focused test とこの契約を更新する必要がある。
