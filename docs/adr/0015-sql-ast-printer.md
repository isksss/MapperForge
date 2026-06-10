# 0015 SQL AST Printer

## Status

Accepted

## Context

MapperForge は SQL Statement/Expression AST と Document Model を追加済みだが、既存 `SqlFormatter` は文字列ベースの整形を継続していた。PLAN.local.md の Printer 方針では `AST -> Doc Tree -> Layout Engine -> String` の経路が必要になる。

## Decision

`SqlAstPrinter` を追加し、SQL AST から `Doc` を生成する入口を作る。

- `SELECT`、`INSERT`、`UPDATE`、`DELETE` の主要句を構造化して出力する
- `Expression` は対応済み AST を文字列表現へ戻す
- 未知 statement/expression は normalized raw に fallback する
- 現時点では既存 `SqlFormatter` へ全面接続せず、JUnit で printer の出力契約を先に固定する

## Consequences

SQL printer を文字列置換から AST/Doc ベースへ段階移行できる。既存 golden file の互換性を壊さないよう、`SqlFormatter` への接続は golden file test を追加しながら進める。
