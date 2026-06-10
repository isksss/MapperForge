# 0019 AST Diff Prefix

## Status

Accepted

## Context

PLAN.local.md では Diff Engine を `AST Diff + Unified Diff` としている。既存実装は unified diff のみで、dry-run はテキスト差分を表示していた。

## Decision

`AstDiff` を追加し、`MapperForge.diff` は AST descriptor 差分を unified diff の前に出力する。

- XML mapper AST から path 付き descriptor を生成する
- SQL text/CDATA は空白と大文字小文字を正規化して比較する
- 属性は path と属性名付きで比較する
- formatting-only の変更では AST diff を出さない
- unified diff は従来どおり必ず残し、dry-run の実用性を維持する

## Consequences

dry-run は構造的な差分と実際のテキスト差分を同時に確認できる。AST diff は descriptor ベースの初期実装であり、SQL/OGNL AST signature との統合は今後段階的に強化する。
