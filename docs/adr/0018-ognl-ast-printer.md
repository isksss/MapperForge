# 0018 OGNL AST Printer

## Status

Accepted

## Context

OGNL は tokenizer と Pratt parser の入口を追加済みだが、formatter は文字列トークン整形だけだった。PLAN.local.md の OGNL 方針では `Tokenizer -> Pratt Parser -> AST -> Formatter` の経路が必要になる。

## Decision

`OgnlAstPrinter` を追加し、対応済み OGNL AST を文字列へ戻す。

- binary/unary/call/collection/literal/name を出力する
- 演算子の前後だけを安定した空白へ整える
- 優先順位上必要な括弧は出力する
- parser が未対応構文を検出した場合は既存 legacy formatter に fallback する

## Consequences

OGNL 整形は対応済み構文から AST 経路へ移行できる。未対応構文は構造を推測せず、従来どおり空白調整だけに留める。
