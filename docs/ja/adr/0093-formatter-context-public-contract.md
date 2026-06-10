# 0093 Formatter Context Public Contract

## ステータス

採用

## 背景

`PLAN.local.md` は固定 formatter rule pipeline を定義している。`FormatterContext` は formatter rule に渡す共有 object で、既存の rule API はこれを可変 context として公開している。この可変性は setter によって暗黙に示されていたが、直接は文書化・テストされていなかった。

## 決定

`FormatterContext` に日本語 Javadoc を追加する。

`FormatterRulePipelineTest` を拡張し、`setConfig` と `setSource` が `config()` / `source()` で返る context value を差し替えることを focused contract test で検証する。

## 結果

Rule 実装は `FormatterContext` を可変 context 境界として扱える。将来 context を immutable にする場合は、rule API 契約とこの ADR を同時に更新する必要がある。
