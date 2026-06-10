# 0074 公開 Facade 契約テスト

## ステータス

採用

## コンテキスト

`PLAN.local.md` は Format、Check、Diff、Validation を中核機能として定義している。Gradle task や下位コンポーネントには既にテストがあるが、公開 API である `MapperForge` facade を直接検証する focused test と日本語 Javadoc はなかった。

## 決定

公開 `MapperForge` API に日本語 Javadoc を追加し、`MapperForgeTest` を追加する。

このテストでは、facade が Mapper XML を整形し、整形済み判定を返し、整形のみの変更を検証し、同じ入口から unified diff を返せることを検証する。

## 結果

公開 Java API が文書化され、直接テストされる。今後 facade を変更する場合は、PLAN の中核機能を維持するか、テストと ADR を明示的に更新する必要がある。
