# Golden File Tests

Golden file test は MapperForge の formatting contract の中心です。formatter の挙動を変更する前に、まず追加または更新してください。

## Layout

各 case は `src/test/resources/golden/<case-name>/` 配下に置きます。

```text
src/test/resources/golden/<case-name>/before.xml
src/test/resources/golden/<case-name>/after.xml
```

- `before.xml` は未整形 input です。
- `after.xml` は期待される exact output です。
- case name は実装詳細ではなく、振る舞いを表す名前にします。

`GoldenFileTest.defaultGoldenFiles` は `FormatterConfig.defaults()` を使う全 directory を自動実行します。default 以外の config が必要な case は `CUSTOM_CONFIG_GOLDEN_FILES` に列挙し、`configFor` に配線してください。

すべての golden file は idempotence も検証します。`after.xml` を再度 format し、その結果が `after.xml` と一致することを確認します。

## Test-Driven Workflow

1. 先に `before.xml` と `after.xml` を追加します。新しい formatter behavior の場合、実装がまだ失敗していても `after.xml` には望ましい最終 output を置きます。
2. focused golden test を実行します。

   ```bash
   mise exec java@temurin-21 -- ./gradlew test --tests 'io.github.isksss.mapperforge.format.GoldenFileTest'
   ```

3. golden output を満たすために必要な最小の formatter、parser、validator、config 変更を実装します。
4. 新しい case と idempotence が通るまで focused golden test を再実行します。
5. full verification gate を実行します。

   ```bash
   mise exec java@temurin-21 -- ./gradlew spotlessApply spotlessCheck build integrationTest
   ```

## Coverage Expectations

まず小さく focused な case を追加し、mapper XML、SQL、OGNL の境界をまたぐ feature では大きめの end-to-end case を追加してください。

現在の coverage:

- Basic statements: `simple-select`, `insert-update-delete`
- Dynamic SQL: `foreach-choose`, `dynamic-sql`, `ognl-dynamic-attributes`
- Complex SQL: `complex-sql`, `sql-set-operations`, `sql-include-fragment`
- SQL comments and styles: `sql-comments`, `sql-single-line`, `sql-compact`
- AST SQL printer: `ast-sql-printer`
- Result maps and nested XML: `result-map-nested`
- Attribute rules: `attribute-order`, `attribute-layout`
- Tag wrapping: `tag-wrap-always`
- Lossless mapper nodes: `lossless`

## CDATA Cases

MyBatis mapper XML では SQL comparison operator や XML reserved character のために CDATA を使うことが多いため、CDATA behavior は明示的に coverage を維持します。

次の category を維持してください。

- `cdata-escaped-operators` のような、SQL operator / expression を含む CDATA
- `cdata-only-escaped-chars` のような、`<`、`>`、`&` など XML reserved character だけを含む CDATA
- `cdata-format-sql-inside` のような、CDATA を保持しつつ SQL formatting を有効にする case
- `cdata-not-preserved` のような、`preserveCdata` 無効時に escaped text へ変換する case

formatter 変更が CDATA に触れる場合、実装を変更する前に golden file を更新または追加してください。
