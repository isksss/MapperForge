# 0013 DML Statement AST の段階的拡張

## Status

Accepted

## Context

PLAN.local.md は `INSERT`、`UPDATE`、`DELETE` を SQL Statement Parser の対象としている。これまで DML statement は raw SQL だけを保持しており、placeholder や WHERE 条件を Expression AST として扱えなかった。

## Decision

`InsertStatement`、`UpdateStatement`、`DeleteStatement` に主要句を追加する。

- `InsertStatement`: `table`、`columns`、`values`、`valueRows`
- `InsertStatement`: `selectSource`、`returning`
- `UpdateStatement`: `table`、`assignments`、`where`
- `DeleteStatement`: `table`、`using`、`where`、`returning`

Parser は top-level の `INTO`、`VALUES`、`SELECT`、`RETURNING`、`SET`、`FROM`、`USING`、`WHERE` を句境界として扱い、値や条件式は `SqlExpressionParser` に渡す。`INSERT ... SELECT` の source は `Statement` として保持し、複数行 `VALUES` は `valueRows` として保持する。既存互換のため、raw のみを受け取る constructor は残す。

## Consequences

DML の placeholder、代入値、WHERE 条件、複数行 `VALUES`、`INSERT ... SELECT`、`DELETE ... USING`、`RETURNING` を AST として検証・printer へ渡せるようになる。方言固有句は golden file test を追加しながら段階的に拡張する。
