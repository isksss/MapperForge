# 0098 SQL Formatter Public Contract

## Status

Accepted

## Context

`SqlFormatter` は `MapperXmlFormatter`、test、将来の integration から使われる公開 formatting entry point です。AST printer、legacy regex formatter、single-line formatting、空 SQL の扱いを持ちます。SQL parser はすべての dialect 構文を網羅しないため、AST printing は recoverable である必要があります。

## Decision

`SqlFormatter` に日本語 Javadoc を追加し、`SqlFormatterTest` に以下の契約テストを追加します。

- 空 SQL は空文字を返すこと
- 未対応 SQL では AST printer から legacy formatting へ fallback すること
- `formatLegacy` は config が `SqlPrinter.AST` でも AST printer を使わないこと

## Consequences

利用者は未対応 SQL でも出力を失わずに AST formatting を要求できます。legacy formatter は安定した escape hatch として残ります。
