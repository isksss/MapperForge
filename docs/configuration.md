# Configuration

MapperForge reads configuration from the Gradle `mapperForge` extension, then `mapperforge.yml`, then defaults.

Gradle configuration overrides YAML configuration.

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
| `indentSize` | `4` | `0` or greater |
| `maxLineLength` | `100` | Greater than `0` |
| `lineEnding` | `LF` | `LF`, `CRLF`, `\n`, `\r\n` |
| `sqlFormatStyle` | `MULTI_LINE` | `MULTI_LINE`, `COMPACT`, `SINGLE_LINE` |
| `sqlPrinter` | `LEGACY` | `LEGACY`, `AST` |
| `tagWrapStyle` | `AUTO` | `KEEP`, `AUTO`, `ALWAYS` |
| `attributeLayout` | `COMPACT` | `COMPACT`, `ONE_PER_LINE` |
| `preserveWhitespace` | `false` | Boolean |
| `preserveCdata` | `true` | Boolean |
| `formatSqlInsideCdata` | `false` | Boolean |
| `strict` | `true` | Boolean |
| `attributeOrder` | empty | Map of tag name to ordered attribute names |

## Strict Mode

When `strict = true`, validation failures fail the Gradle task.

When `strict = false`, MapperForge logs a warning and keeps the original file if validation fails during formatting.

## CDATA

By default, CDATA wrappers and raw CDATA text are preserved.

Set `formatSqlInsideCdata = true` to format SQL inside CDATA while keeping the CDATA wrapper. SQL semantic validation still runs.

Set `preserveCdata = false` to convert CDATA content to normal XML text. MapperForge escapes XML-sensitive characters such as `<` and `&`.

## SQL Printer

`LEGACY` keeps the v1 golden-compatible SQL formatter.

`AST` enables the parser-backed SQL AST printer. It is useful for parser and printer development, but `LEGACY` remains the default compatibility mode.
