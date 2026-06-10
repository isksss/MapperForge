# 0016 OGNL Parser Entrypoint

## Status

Accepted

## Context

MapperForge の OGNL 整形は既存互換のため文字列トークン整形を使っている。一方で PLAN.local.md では OGNL も SQL と同様に tokenizer、parser、AST、formatter へ段階移行する方針になっている。

## Decision

`OgnlExpressionParser` を追加し、OGNL expression を小さな AST に変換する入口を作る。

- `and`、`or`、`not`、`in`、`instanceof` と記号演算子の優先順位を Pratt parser で扱う
- property path、method call、collection literal、string/number/null/boolean literal を AST として保持する
- 解析不能な式は `OgnlUnknownExpression` に退避する
- 既存 XML formatter は当面 `OgnlFormatter` の出力を維持し、AST formatter への移行は別差分で進める

## Consequences

OGNL の意味比較や AST printer を追加するための土台ができる。現時点では formatter の経路を切り替えないため、既存 golden file 互換への影響はない。
