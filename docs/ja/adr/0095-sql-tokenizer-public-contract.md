# 0095 SQL Tokenizer Public Contract

## Status

Accepted

## Context

`SqlTokenizer` は parser test と将来の integration から使われる公開 parser entry point です。予約語の大文字化、文字列 literal と識別子表記の保持、EOF token の追加、不変 token list の返却は既に実装されていますが、公開契約として十分に文書化されていませんでした。

## Decision

`SqlTokenizer` に日本語 Javadoc を追加し、`SqlTokenizerTest` に以下の契約テストを追加します。

- 返却 token list が不変であること
- 末尾に `EOF` token があること
- EOF range が token 化後の現在位置を指すこと

## Consequences

利用者は token stream の形と不変性に依存できます。将来 tokenizer の挙動を変える場合は、test と ADR の明示更新が必要です。
