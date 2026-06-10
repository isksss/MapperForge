# PLAN Traceability

This document maps the main `PLAN.local.md` requirements to implementation and verification entry points. It is a traceability aid, not a completion certificate.

## Project Metadata

| Requirement | Evidence |
| --- | --- |
| Project name `MapperForge` | `settings.gradle.kts`, `README.md` |
| Group `io.github.isksss` | `build.gradle.kts` |
| Plugin ID `io.github.isksss.mapperforge` | `build.gradle.kts`, `MapperForgePlugin` |
| Base package `io.github.isksss.mapperforge` | `src/main/java/io/github/isksss/mapperforge` |
| Java 21+ | `.mise.toml`, `build.gradle.kts` toolchain |
| Gradle 9+ | Gradle wrapper, CI workflows |

## Distribution

| Requirement | Evidence |
| --- | --- |
| GitHub plugin distribution | `build.gradle.kts` publishing block, `.github/workflows/publish.yml` |
| GitHub Packages consumer docs | `README.md`, `docs/release.md` |
| Release gate | `.github/workflows/publish.yml`, `docs/adr/0059-release-publish-integration-gate.md` |
| CI gate | `.github/workflows/ci.yml`, `docs/adr/0058-github-actions-ci-gate.md` |

## Configuration

| Requirement | Evidence |
| --- | --- |
| Gradle extension | `MapperForgeExtension`, `MapperForgeTask.configureFrom` |
| `mapperforge.yml` | `ConfigLoader`, `ConfigLoaderTest` |
| Gradle > YAML > defaults precedence | `MapperForgeTask.configureFrom`, `MapperForgePluginFunctionalTest` |
| SemVer formatter version | `FormatterConfig`, `ConfigLoaderTest`, `MapperForgePluginFunctionalTest` |
| Include/exclude file selection | `MapperForgeTask`, `MapperForgePluginFunctionalTest` |
| Configuration reference | `docs/configuration.md` |

## Gradle Tasks

| Requirement | Evidence |
| --- | --- |
| `mapperForgeFormat` | `MapperForgePlugin`, `MapperForgeFormatTask`, functional tests |
| `mapperForgeCheck` | `MapperForgePlugin`, `MapperForgeCheckTask`, functional tests |
| `mapperForgeDryRun` | `MapperForgePlugin`, `MapperForgeDryRunTask`, functional tests |
| Task behavior docs | `docs/tasks.md` |
| Check task cacheability | `MapperForgeCheckTask`, `MapperForgeTaskCacheAnnotationTest` |
| Format/dry-run no cache | `MapperForgeFormatTask`, `MapperForgeDryRunTask`, `MapperForgeTaskCacheAnnotationTest` |

## Parser And AST

| Requirement | Evidence |
| --- | --- |
| Woodstox XML parser | `MapperXmlParser` |
| Hybrid mapper AST | `ast/mapper/*`, `MapperXmlParserTest` |
| Dedicated MyBatis nodes | `ast/mapper/*ElementNode.java`, `MapperXmlParserTest` |
| `GenericElement` fallback | `GenericElementNode`, `MapperXmlParserTest`, `ValidatorTest` |
| `TextNode`, `CommentNode`, `CDataNode` | `ast/mapper`, golden files, `ValidatorTest` |
| Placeholder parser | `PlaceholderParser`, `PlaceholderParserTest` |
| Source position records | `SourceFile`, `Position`, `Range`, `Token` |

## SQL

| Requirement | Evidence |
| --- | --- |
| SQL tokenizer | `SqlTokenizer`, `SqlTokenizerTest` |
| Statement parser | `SqlStatementParser`, `SqlStatementParserTest` |
| Pratt expression parser | `SqlExpressionParser`, `SqlExpressionParserTest` |
| `SELECT`, DML, `WITH`, set operations | `SqlStatementParserTest`, golden files |
| SQL expression variants | `ast/sql/*`, `SqlExpressionParserTest` |
| Unknown recovery | `UnknownStatement`, `UnknownExpression`, parser tests |
| SQL formatter styles | `SqlFormatter`, `SqlFormatterTest`, golden files |
| AST SQL printer | `SqlAstPrinter`, `SqlAstPrinterTest`, golden files |

## OGNL

| Requirement | Evidence |
| --- | --- |
| OGNL tokenizer/parser | `OgnlTokenizer`, `OgnlExpressionParser`, `OgnlExpressionParserTest` |
| Operators and method/property access | `OgnlExpressionParserTest`, golden files |
| OGNL formatter | `OgnlFormatter`, `OgnlFormatterTest`, dynamic attribute golden files |
| OGNL AST printer | `OgnlAstPrinter`, `OgnlAstPrinterTest` |
| Unknown recovery | `OgnlUnknownExpression`, `OgnlExpressionParserTest` |

## Formatting

| Requirement | Evidence |
| --- | --- |
| Idempotent formatting | `GoldenFileTest.goldenFilesAreIdempotent` |
| Golden file tests | `src/test/resources/golden/*`, `GoldenFileTest` |
| Dynamic SQL formatting | `dynamic-sql`, `foreach-choose`, `ognl-dynamic-attributes` golden files |
| Attribute order | `AttributeOrderRule`, `attribute-order` golden file |
| Attribute layout | `attribute-layout` golden file |
| Tag wrapping | `tag-wrap-always` golden file |
| CDATA behavior | CDATA golden files, `ValidatorTest`, Gradle functional tests |
| Comments | `sql-comments` golden file, `ValidatorTest`, AST diff tests |

## Validation And Diff

| Requirement | Evidence |
| --- | --- |
| AST comparison | `Validator`, `ValidatorTest` |
| SQL semantic validation | `Validator`, `ValidatorTest` |
| OGNL semantic validation | `Validator`, `ValidatorTest` |
| Placeholder validation | `PlaceholderParser`, `ValidatorTest` |
| Comment/CDATA/generic validation | `ValidatorTest` |
| Whitespace validation when enabled | `ValidatorTest` |
| Validation error codes and locations | `ValidationError`, `ValidationErrorTest`, `ValidatorTest` |
| AST diff plus unified diff | `AstDiff`, `UnifiedDiff`, diff tests, dry-run functional test |

## Integration And Quality Gates

| Requirement | Evidence |
| --- | --- |
| JUnit tests | `src/test/java` |
| Dockerized PostgreSQL/MySQL MyBatis tests | `MybatisDatabaseIntegrationTest`, `integrationTest` task |
| Spotless integration | `build.gradle.kts`, `spotlessApply`, `spotlessCheck` |
| Release checklist | `docs/release.md` |
