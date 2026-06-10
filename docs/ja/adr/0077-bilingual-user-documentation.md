# 0077 英日併記のユーザードキュメント

## ステータス

採用

## コンテキスト

MapperForge の利用者向けドキュメントは主に `docs/` 配下の英語版だけでした。
今後のドキュメント変更では日本語版も同時に用意する必要があり、GitHub 配布、設定、task、golden test、release、traceability の案内を日本語でも辿れる状態にする必要があります。

## 決定

現在の利用者向けドキュメントの日本語版を `docs/ja/` 配下に追加します。

- `README.md`
- `configuration.md`
- `tasks.md`
- `golden-tests.md`
- `release.md`
- `traceability.md`

root README から日本語ドキュメントへ link し、今後の利用者向けドキュメント更新では英語版と日本語版を対で更新します。

## 結果

日本語利用者は plugin setup、task behavior、golden test workflow、release procedure、traceability information を英語版に依存せず確認できます。
今後のドキュメント変更では両言語版を更新するか、言語固有の文書である理由を明示的に残す必要があります。
