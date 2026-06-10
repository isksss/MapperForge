# 0094 Token Type Public Contract

## Status

Accepted

## Context

`TokenType` は tokenizer test、parser test、将来の integration で参照される公開 source model の一部です。enum 名は plugin 利用者、report、将来の IDE integration から見える可能性がありますが、これまで順序は実装に暗黙依存していました。

## Decision

`TokenType` に日本語 Javadoc を追加し、enum 名と順序を `SqlTokenizerTest` で固定します。

順序は以下のまま維持します。

```text
IDENTIFIER, KEYWORD, STRING, NUMBER, SYMBOL, PLACEHOLDER, COMMENT, EOF, UNKNOWN
```

## Consequences

token 分類を変更する場合は、test と ADR の明示更新が必要になります。既存の parser / formatter 挙動は変更しません。
