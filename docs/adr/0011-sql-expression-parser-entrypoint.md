# 0011 SQL Expression Parser の入口

## Status

Accepted

## Context

PLAN.local.md では SQL Expression Parser に Pratt Parser を採用し、`LiteralExpression`、`ColumnExpression`、`FunctionExpression`、`BinaryExpression`、`UnaryExpression`、`CASE`、`BETWEEN`、`IN`、`CAST`、`ARRAY`、`ROW`、`EXISTS`、`PlaceholderExpression` などを扱う方針としている。

既存の SQL Statement Parser は statement 種別の判定と recoverable fallback を優先しており、expression の AST は `PlaceholderExpression` と `UnknownExpression` に限られていた。

## Decision

`io.github.isksss.mapperforge.ast.sql` に PLAN の Expression 型を追加し、`SqlExpressionParser` を Pratt Parser の入口として実装する。

- 二項演算子は precedence table で処理する。
- `BETWEEN` と `IN` は SQL 固有の infix expression として扱う。
- `CASE`、`CAST`、`ARRAY`、`ROW`、`EXISTS` は prefix/special form として扱う。
- 括弧内 `SELECT`/`WITH` は `SubQueryExpression` として扱う。
- PostgreSQL 系 JSON operator の `->`、`->>`、`#>`、`#>>` は postfix expression として扱う。
- `OVER (...)` は直前の expression を `WindowExpression` で包む。
- MyBatis placeholder は既存の `PlaceholderParser` を再利用する。
- 解析できない構文は例外を外へ出さず `UnknownExpression` へ退避する。

## Consequences

SQL expression の比較・printer・validator を段階的に強化するための AST 入口ができる。一方で、現時点では full SQL grammar ではなく recoverable subset である。方言固有構文や複雑な nested query は、利用箇所と golden file test を追加しながら拡張する。
