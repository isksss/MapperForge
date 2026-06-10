# 設定

MapperForge は Gradle の `mapperForge` extension、`mapperforge.yml`、default の順に設定を読みます。

Gradle 設定は YAML 設定を上書きします。

## Gradle DSL

```kotlin
mapperForge {
    dialect = "POSTGRESQL"
    formatterVersion = "1.0.0"
    include = listOf("src/main/resources/**/*.xml")
    exclude = listOf("**/legacy/**")

    indentSize = 4
    maxLineLength = 100
    lineEnding = "LF"

    sqlFormatStyle = "MULTI_LINE"
    sqlPrinter = "LEGACY"
    tagWrapStyle = "AUTO"
    attributeLayout = "COMPACT"

    preserveWhitespace = false
    preserveCdata = true
    formatSqlInsideCdata = false
    strict = true

    attributeOrder("result", listOf("property", "column", "javaType", "jdbcType"))
}
```

## YAML

```yaml
dialect: POSTGRESQL
formatterVersion: 1.0.0
include:
  - src/main/resources/**/*.xml
exclude:
  - "**/legacy/**"
indentSize: 4
maxLineLength: 100
lineEnding: LF
sqlFormatStyle: MULTI_LINE
sqlPrinter: LEGACY
tagWrapStyle: AUTO
attributeLayout: COMPACT
preserveWhitespace: false
preserveCdata: true
formatSqlInsideCdata: false
strict: true
attributeOrder:
  result:
    - property
    - column
    - javaType
    - jdbcType
```

## Options

| Option | Default | Values |
| --- | --- | --- |
| `dialect` | `POSTGRESQL` | `POSTGRESQL`, `MYSQL` |
| `formatterVersion` | `1.0.0` | SemVer |
| `include` | `src/main/resources/**/*.xml` | Glob list |
| `exclude` | empty | Glob list |
| `indentSize` | `4` | `0` 以上 |
| `maxLineLength` | `100` | `0` より大きい値 |
| `lineEnding` | `LF` | `LF`, `CRLF`, `\n`, `\r\n` |
| `sqlFormatStyle` | `MULTI_LINE` | `MULTI_LINE`, `COMPACT`, `SINGLE_LINE` |
| `sqlPrinter` | `LEGACY` | `LEGACY`, `AST` |
| `tagWrapStyle` | `AUTO` | `KEEP`, `AUTO`, `ALWAYS` |
| `attributeLayout` | `COMPACT` | `COMPACT`, `ONE_PER_LINE` |
| `preserveWhitespace` | `false` | Boolean |
| `preserveCdata` | `true` | Boolean |
| `formatSqlInsideCdata` | `false` | Boolean |
| `strict` | `true` | Boolean |
| `attributeOrder` | empty | tag name から ordered attribute names への map |

## Strict Mode

`strict = true` の場合、validation failure は Gradle task を失敗させます。

`strict = false` の場合、formatting 中に validation failure が起きると MapperForge は warning を出し、元ファイルを変更しません。

## CDATA

default では CDATA wrapper と raw CDATA text を保持します。

`formatSqlInsideCdata = true` を設定すると、CDATA wrapper を維持したまま CDATA 内 SQL を整形します。SQL semantic validation は引き続き実行されます。

`preserveCdata = false` を設定すると、CDATA content を通常の XML text へ変換します。MapperForge は `<` や `&` など XML 上 escape が必要な文字を escape します。

## SQL Printer

`LEGACY` は v1 golden file と互換の SQL formatter を使います。

`AST` は parser-backed SQL AST printer を使います。parser / printer 開発に有用ですが、互換性維持のため default は `LEGACY` です。
