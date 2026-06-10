# 0078 OGNL property access parser 契約

## ステータス

採用

## コンテキスト

`PLAN.local.md` は OGNL の property access と comparison operator を parser 対象として挙げています。
Formatter と printer のテストでは property 風の名前を間接的に検証していましたが、OGNL parser の契約として dotted property access を安定した名前として扱い、comparison operator を parse することは直接固定されていませんでした。

## 決定

`>=` と `<=` を含む boolean expression の中で dotted property access を扱う focused parser test を追加します。
この契約で触れる公開 entry point の `OgnlExpressionParser` に日本語 Javadoc を追加します。

## 結果

今後 OGNL parser を変更する場合、契約と ADR を明示的に更新しない限り dotted property access の名前保持と comparison operator parsing を維持する必要があります。
また、生成される日本語 API documentation 上でも parser entry point の意図が分かりやすくなります。
