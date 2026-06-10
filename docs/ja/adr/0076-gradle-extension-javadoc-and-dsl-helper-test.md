# 0076 Gradle 拡張の Javadoc と DSL ヘルパー契約テスト

## ステータス

採用

## コンテキスト

`MapperForgeExtension` は `mapperForge { ... }` として公開される Gradle DSL の入口です。
生成される API ドキュメントが利用者向けになるため、拡張の Javadoc は日本語で用意する必要があります。
また、`attributeOrder(String, List<String>)` ヘルパーは formatter の意味検証の詳細に依存せず、公開拡張としての挙動を固定する JUnit 契約テストが不足していました。

## 決定

Gradle plugin の入口と拡張 accessor に日本語 Javadoc を追加します。
Gradle `ProjectBuilder` の JUnit テストで plugin を適用し、`MapperForgeExtension` を取得したうえで `attributeOrder(...)` が設定の追加と上書きを行うことを検証します。

## 結果

公開 Gradle DSL を日本語利用者向けに説明しやすくなり、ヘルパーメソッドに実行可能な互換性契約ができます。
今後、公開 DSL に影響する拡張変更では Javadoc と機能テストの両方を更新します。
