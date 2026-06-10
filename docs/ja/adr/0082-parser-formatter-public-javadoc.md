# 0082 Parser / Formatter 公開 Javadoc

## ステータス

採用

## コンテキスト

`PLAN.local.md` は Mapper XML parsing、formatting、recoverable parser behavior、安定した parser / formatter error classification を定義しています。
Parser と formatter の実装は既に存在し、parser / formatter error code もテスト済みでしたが、公開 entry point の日本語 Javadoc が不足していました。

## 決定

`MapperXmlParser`、`MapperXmlFormatter`、およびそれぞれの公開 exception type に日本語 Javadoc を追加します。
Parser / formatter error code 契約が具体的なテストへ結び付くように traceability を明確化します。

## 結果

生成される API documentation で XML parser と formatter の入口を日本語で確認できるようになります。
今後 parser / formatter の公開挙動を変更する場合は、Javadoc、テスト、ADR を合わせて更新する必要があります。
