# Golden File Tests

Golden file tests are the primary formatting contract for MapperForge. Add or update them before changing formatter behavior.

## Layout

Each case lives under `src/test/resources/golden/<case-name>/`.

```text
src/test/resources/golden/<case-name>/before.xml
src/test/resources/golden/<case-name>/after.xml
```

- `before.xml` is the unformatted input.
- `after.xml` is the exact expected output.
- Case names should describe the behavior, not the implementation detail.

`GoldenFileTest.defaultGoldenFiles` automatically runs all directories that use `FormatterConfig.defaults()`. Cases that require a non-default config must be listed in `CUSTOM_CONFIG_GOLDEN_FILES` and wired in `configFor`.

All golden files are also checked for idempotence by formatting `after.xml` again and comparing the result to `after.xml`.

## Test-Driven Workflow

1. Add `before.xml` and `after.xml` first. For a new formatter behavior, keep `after.xml` as the desired final output even while the implementation is still failing.
2. Run the focused golden test:

   ```bash
   mise exec java@temurin-21 -- ./gradlew test --tests 'io.github.isksss.mapperforge.format.GoldenFileTest'
   ```

3. Implement the smallest formatter, parser, validator, or config change needed to satisfy the golden output.
4. Re-run the focused golden test until the new case and idempotence pass.
5. Run the full verification gate:

   ```bash
   mise exec java@temurin-21 -- ./gradlew spotlessApply spotlessCheck build integrationTest
   ```

## Coverage Expectations

Prefer adding small, focused cases first, then a larger end-to-end case when a feature crosses mapper XML, SQL, and OGNL boundaries.

Current coverage includes:

- Basic statements: `simple-select`, `insert-update-delete`.
- Dynamic SQL: `foreach-choose`, `dynamic-sql`, `ognl-dynamic-attributes`.
- Complex SQL: `complex-sql`, `sql-set-operations`, `sql-include-fragment`.
- SQL comments and styles: `sql-comments`, `sql-single-line`, `sql-compact`.
- AST SQL printer: `ast-sql-printer`.
- Result maps and nested XML: `result-map-nested`.
- Attribute rules: `attribute-order`, `attribute-layout`.
- Tag wrapping: `tag-wrap-always`.
- Lossless mapper nodes: `lossless`.

## CDATA Cases

CDATA behavior must stay explicit because MyBatis mapper XML often uses it for SQL comparison operators and XML-reserved characters.

Keep these categories covered:

- CDATA containing SQL operators and expressions, such as `cdata-escaped-operators`.
- CDATA containing only XML-reserved characters like `<`, `>`, and `&`, such as `cdata-only-escaped-chars`.
- Preserved CDATA with SQL formatting enabled, such as `cdata-format-sql-inside`.
- CDATA converted to escaped text when `preserveCdata` is disabled, such as `cdata-not-preserved`.

When a formatter change touches CDATA, update or add a golden file before modifying implementation code.
