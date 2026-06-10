# Gradle Tasks

MapperForge registers three Gradle tasks.

## `mapperForgeFormat`

Formats matching MyBatis mapper XML files in place.

```bash
./gradlew mapperForgeFormat
```

Behavior:

- Scans configured `include` patterns and skips configured `exclude` patterns.
- Processes only XML files whose root element is `<mapper>`.
- Formats the file, validates the formatted output, and writes the file only after validation succeeds.
- With `strict = true`, validation failure fails the task.
- With `strict = false`, validation failure logs a warning and keeps the original file unchanged.

`mapperForgeFormat` is not build-cacheable because it writes files in place.

## `mapperForgeCheck`

Checks whether matching mapper XML files are already formatted.

```bash
./gradlew mapperForgeCheck
```

Behavior:

- Uses the same file discovery and validation behavior as `mapperForgeFormat`.
- Does not write mapper XML files.
- Fails when formatting would change any mapper XML file.
- Writes task state under `build/mapperforge/` for Gradle task tracking.

`mapperForgeCheck` is cacheable.

## `mapperForgeDryRun`

Prints the diff that `mapperForgeFormat` would apply.

```bash
./gradlew mapperForgeDryRun
```

Behavior:

- Uses the same file discovery and validation behavior as `mapperForgeFormat`.
- Does not write mapper XML files.
- Prints MapperForge's AST diff prefix followed by a unified diff for changed files.
- Always runs when invoked so the diff reflects current files.

`mapperForgeDryRun` is not build-cacheable because its output is intended for the console.

## File Selection

Default file selection:

```kotlin
mapperForge {
    include = listOf("src/main/resources/**/*.xml")
    exclude = listOf()
}
```

MapperForge reads each candidate XML file and processes it only when the parser finds a `<mapper>` root element. XML files where `<mapper>` appears only in comments are ignored.

## Validation

All tasks validate formatted output before acting on it. Validation compares:

- SQL statement and expression signatures
- OGNL expression signatures
- Placeholder sequences and options
- XML and SQL comments
- CDATA sections when preserved
- Generic XML elements
- Whitespace when `preserveWhitespace = true`

Validation errors include an error code, error type, message, and source range when available.
