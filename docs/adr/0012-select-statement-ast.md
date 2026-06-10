# 0012 SELECT Statement AST の段階的拡張

## Status

Accepted

## Context

SQL Statement Parser は statement 種別の判定を先行して実装しており、`SelectStatement` は raw SQL のみを保持していた。一方で PLAN.local.md は SQL Parser を AST 化し、Expression Parser と接続する方針を示している。

## Decision

`SelectStatement` に以下を追加する。

- `selectItems`: top-level の select list を `Expression` として保持する
- `from`: top-level `FROM` 句の raw text
- `where`: top-level `WHERE` 句を `Expression` として保持する

Parser は top-level の `FROM` と `WHERE` のみを句境界として扱う。select list は括弧と配列リテラル内の comma を無視して分割し、各 item を `SqlExpressionParser` に渡す。

## Consequences

SQL formatter、validator、printer が SELECT の主要部分を Expression AST として参照できるようになる。JOIN、GROUP BY、HAVING、ORDER BY、LIMIT、方言固有句は今後 golden file test を追加しながら段階的に AST 化する。
