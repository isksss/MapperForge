# 0010 Document Model と Layout Engine

## Status

Accepted

## Context

MapperForge は XML/SQL/OGNL の整形規則を段階的に AST rule pipeline へ移している。次の段階では、文字列を直接組み立てる前に、折り返し候補とインデントを表現できる中間表現が必要になる。

## Decision

`io.github.isksss.mapperforge.print` に Doc Tree を追加する。

- `TextDoc`: そのまま出力する文字列
- `LineDoc`: group が flat の場合は空白、break の場合は改行
- `SoftLineDoc`: group が flat の場合は空文字、break の場合は改行
- `HardLineDoc`: 常に改行
- `IndentDoc`: 子要素の改行後インデントを `indentSize` 分増やす
- `GroupDoc`: `maxLineLength` に収まる場合は flat、収まらない場合は break
- `ConcatDoc`: 子 Doc を順に連結する

`LayoutEngine.render` は `FormatterConfig.indentSize`、`maxLineLength`、`lineEnding` を使用する。現時点では既存 formatter への全面接続は行わず、printer の振る舞いを JUnit で先に固定する。

## Consequences

今後の XML/SQL printer は、直接文字列を返す実装から Doc Tree を生成する実装へ段階的に移行できる。既存 formatter の出力互換性を崩さないため、接続は golden file test を追加しながら進める。
