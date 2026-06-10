# 0080 Gradle cache relocatability 契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は Gradle Build Cache 対応を挙げ、cache key input として file hash を定義しています。
MapperForge は task input / output annotation を既に付けていましたが、source file の path sensitivity は直接テストされていませんでした。
checkout directory をまたいで Gradle cache を再利用するには、mapper XML input が relative path sensitivity を使う必要があります。

## 決定

`MapperForgeTaskCacheAnnotationTest` を拡張し、`getSourceFiles()` に `@PathSensitive(PathSensitivity.RELATIVE)` が付いていることを検証します。
この cache 契約で触れる公開 `MapperForgeTask` API に日本語 Javadoc を追加します。

## 結果

今後 task input を変更する場合、cache contract を意図的に見直さない限り relative path sensitivity を維持する必要があります。
また、生成される日本語 API documentation 上でも Gradle task API の意図が分かりやすくなります。
