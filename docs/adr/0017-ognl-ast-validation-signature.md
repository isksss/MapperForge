# 0017 OGNL AST Validation Signature

## Status

Accepted

## Context

`test` などの MyBatis OGNL 属性は formatter により空白が変わる。属性文字列をそのまま比較すると formatting-only の変更も Validator が拒否してしまう。一方で `!=` から `==` への変更など、OGNL の意味変更は検出する必要がある。

## Decision

Validator は OGNL 対象属性を `OgnlExpressionParser` で AST 化し、signature として比較する。

- 対象属性は `if/when@test`、`bind@value`、`foreach@collection`
- binary/unary/call/collection/literal/name を構造として比較する
- 解析不能な式は whitespace normalize した raw を比較する
- signature 差分は `ErrorType.EXPRESSION` として扱う

## Consequences

OGNL の空白整形だけなら検証を通し、構造や演算子が変わった場合は検証で止められる。対象属性は MyBatis の主要な動的 SQL 属性から始め、必要に応じて追加する。
