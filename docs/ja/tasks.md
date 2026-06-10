# Gradle Tasks

MapperForge は 3 つの Gradle task を登録します。

## `mapperForgeFormat`

対象の MyBatis mapper XML file をその場で整形します。

```bash
./gradlew mapperForgeFormat
```

挙動:

- 設定された `include` pattern を走査し、`exclude` pattern に一致する file を除外します。
- root element が `<mapper>` の XML file だけを処理します。
- file を整形し、整形後 output を検証し、validation が成功した場合だけ file に書き戻します。
- `strict = true` では validation failure により task が失敗します。
- `strict = false` では validation failure を warning として出力し、元ファイルを変更しません。

`mapperForgeFormat` は file をその場で書き換えるため build cache 対象ではありません。

## `mapperForgeCheck`

対象 mapper XML file がすでに整形済みかを検査します。

```bash
./gradlew mapperForgeCheck
```

挙動:

- `mapperForgeFormat` と同じ file discovery と validation を使います。
- mapper XML file には書き込みません。
- 整形すると変更が発生する mapper XML file がある場合に失敗します。
- Gradle task tracking 用の task state を `build/mapperforge/` に書きます。

`mapperForgeCheck` は cacheable です。
Gradle cache input には formatter mode、formatter configuration、選択された source file、task implementation classpath が含まれます。

## `mapperForgeDryRun`

`mapperForgeFormat` が適用する diff を表示します。

```bash
./gradlew mapperForgeDryRun
```

挙動:

- `mapperForgeFormat` と同じ file discovery と validation を使います。
- mapper XML file には書き込みません。
- 変更対象 file について、MapperForge の AST diff prefix と unified diff を表示します。
- console output が現在の file を反映するように、実行時は常に動作します。

`mapperForgeDryRun` は console output が目的のため build cache 対象ではありません。

## File Selection

default file selection:

```kotlin
mapperForge {
    include = listOf("src/main/resources/**/*.xml")
    exclude = listOf()
}
```

MapperForge は候補 XML file を読み、parser が `<mapper>` root element を見つけた場合だけ処理します。`<mapper>` が comment 内にだけ現れる XML file は無視します。

## Validation

すべての task は、動作前に整形後 output を検証します。Validation は次を比較します。

- SQL statement / expression signature
- OGNL expression signature
- Placeholder sequence と option
- XML comment と SQL comment
- preserve 対象の CDATA section
- Generic XML element
- `preserveWhitespace = true` の場合の whitespace

Validation error には、error code、error type、message、利用可能な場合は source range が含まれます。
