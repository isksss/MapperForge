# 0092 Config Loader Public Contract

## ステータス

採用

## 背景

`PLAN.local.md` は補助設定ファイルとして `mapperforge.yml` を定義し、設定が存在しない場合は default を使うことを要求している。`ConfigLoader` はそのファイルを読み込む公開境界だが、公開契約と mapping ではない YAML の失敗挙動が直接は文書化されていなかった。

## 決定

`ConfigLoader` と `ConfigLoader.ConfigException` に日本語 Javadoc を追加する。

`ConfigLoaderTest` を拡張し、YAML root value が mapping ではない場合に `CONFIG_ERROR` と安定メッセージ `mapperforge.yml must contain a mapping` で失敗することを検証する。

## 結果

設定読み込みの公開境界が文書化される。今後 YAML schema を変更する場合は、root mapping 契約を維持するか ADR と test を更新する必要がある。
