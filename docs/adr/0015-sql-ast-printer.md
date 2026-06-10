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
- `SqlFormatter.format` は対応済み statement で AST printer 経路を使う
- 既存 XML golden file 互換を守るため、`MapperXmlFormatter` は当面 `SqlFormatter.formatLegacy` を使う

## Consequences

SQL printer を文字列置換から AST/Doc ベースへ段階移行できる。既存 golden file の互換性を壊さないよう、XML formatter への接続は golden file test を追加しながら進める。
