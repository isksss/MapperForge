# 0014 SQL AST Signature による Validation

## Status

Accepted

## Context

Validator は XML AST、placeholder、comment、CDATA の保持を検証していた。一方で SQL text は `TextNode` 比較から除外していたため、`WHERE active = 1` が `WHERE active = 0` に変わるような SQL 意味変更を検出できなかった。

SQL formatter は keyword の大文字化や改行を行うため、raw SQL 文字列比較では正当な整形も拒否してしまう。

## Decision

Validator は SQL text と CDATA 内 SQL を `SqlStatementParser` で parse し、raw を除いた statement signature を比較する。

- `SELECT`、`INSERT`、`UPDATE`、`DELETE` は AST の主要フィールドを比較する
- `Expression` は AST record を比較し、`UnknownExpression` は空白・大小文字を正規化した raw を比較する
- `WITH`、set operation、unknown statement は空白・大小文字を正規化した raw を fallback として比較する
- SQL signature が変わった場合は `ErrorType.STATEMENT` とする

## Consequences

整形による keyword 大文字化や改行は許容しつつ、対応済み statement/expression の意味変更を検出できる。未知構文は保守的な normalized raw 比較に留め、AST 対応範囲の拡張に合わせて signature 比較も強化する。
