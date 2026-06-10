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
| GitHub plugin distribution | `build.gradle.kts` publishing block, `.github/workflows/publish.yml`, `docs/adr/0088-lowercase-maven-artifact-id.md` |
| GitHub Packages consumer docs | `README.md`, `docs/release.md`, `docs/ja/release.md` |
| Release gate | `.github/workflows/publish.yml`, `docs/adr/0059-release-publish-integration-gate.md` |
| CI gate | `.github/workflows/ci.yml`, `docs/adr/0058-github-actions-ci-gate.md` |

## Configuration

| Requirement | Evidence |
| --- | --- |
| Gradle extension | `MapperForgeExtension`, `MapperForgeTask.configureFrom`, `MapperForgeExtensionTest`, `MapperForgePluginFunctionalTest` |
| `mapperforge.yml` | `ConfigLoader`, `ConfigLoaderTest`, `docs/adr/0092-config-loader-public-contract.md` |
| Gradle > YAML > defaults precedence | `MapperForgeTask.configureFrom`, `MapperForgePluginFunctionalTest` |
| SemVer formatter version | `FormatterConfig`, `ConfigLoaderTest`, `MapperForgePluginFunctionalTest` |
| Formatter defaults | `FormatterConfig.defaults`, `FormatterConfigTest`, `ConfigLoaderTest` |
| Include/exclude file selection | `MapperForgeTask`, `MapperForgePluginFunctionalTest` |
| Attribute order DSL helper | `MapperForgeExtension.attributeOrder`, `MapperForgeExtensionTest` |
| Configuration reference | `docs/configuration.md`, `docs/ja/configuration.md` |

## Gradle Tasks

| Requirement | Evidence |
| --- | --- |
| Public Java facade | `MapperForge`, `MapperForgeTest` |
| `mapperForgeFormat` | `MapperForgePlugin`, `MapperForgeFormatTask`, functional tests |
| `mapperForgeCheck` | `MapperForgePlugin`, `MapperForgeCheckTask`, functional tests |
| `mapperForgeDryRun` | `MapperForgePlugin`, `MapperForgeDryRunTask`, functional tests |
| Task behavior docs | `docs/tasks.md`, `docs/ja/tasks.md` |
| Check task cacheability | `MapperForgeCheckTask`, `MapperForgeTaskCacheAnnotationTest` |
| Check task cache key inputs | `MapperForgeTask` input annotations and relative path sensitivity, `MapperForgeTaskCacheAnnotationTest` |
| Format/dry-run no cache | `MapperForgeFormatTask`, `MapperForgeDryRunTask`, `MapperForgeTaskCacheAnnotationTest` |

## Parser And AST

| Requirement | Evidence |
| --- | --- |
| Woodstox XML parser | `MapperXmlParser` |
| Hybrid mapper AST | `ast/mapper/*`, `MapperXmlParserTest` |
| Immutable AST and setter prohibition | `ast/*`, `ElementNode`, SQL/OGNL list AST records, `AstImmutabilityTest` |
| Dedicated MyBatis nodes | `ast/mapper/*ElementNode.java`, `MapperXmlParserTest` |
| `GenericElement` fallback | `GenericElementNode`, `MapperXmlParserTest`, `ValidatorTest` |
| `TextNode`, `CommentNode`, `CDataNode` | `ast/mapper`, `MapperAstLeafNodeTest`, golden files, `ValidatorTest` |
| Placeholder parser | `PlaceholderParser`, `PlaceholderExpression`, `PlaceholderParserTest` |
| Source position records | `SourceFile`, `Position`, `Range`, `Token`, `SourceModelTest` |

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
| Document model and layout engine | `Doc`, `Docs`, `LayoutEngine`, `DocTreeTest`, `LayoutEngineTest` |

## OGNL

| Requirement | Evidence |
| --- | --- |
| OGNL tokenizer/parser | `OgnlTokenizer`, `OgnlExpressionParser`, `OgnlExpressionParserTest` |
| Operators and method/property access | `OgnlExpressionParserTest`, `OgnlFormatterTest`, `OgnlAstPrinterTest`, golden files |
| OGNL formatter | `OgnlFormatter`, `OgnlFormatterTest`, dynamic attribute golden files |
| OGNL AST printer | `OgnlAstPrinter`, `OgnlAstPrinterTest` |
| Unknown recovery | `OgnlUnknownExpression`, `OgnlExpressionParserTest` |

## Formatting

| Requirement | Evidence |
| --- | --- |
| Fixed v1 rule pipeline | `FormatterRulePipeline`, `FormatterRulePipelineTest` |
| Idempotent formatting | `GoldenFileTest.goldenFilesAreIdempotent` |
| Golden file tests | `src/test/resources/golden/*`, `GoldenFileTest` |
| Printer Doc Tree | `print/*Doc.java`, `Docs`, `LayoutEngine`, `DocTreeTest`, `LayoutEngineTest` |
| Dynamic SQL formatting | `dynamic-sql`, `foreach-choose`, `ognl-dynamic-attributes` golden files |
| Attribute order | `AttributeOrderRule`, `attribute-order` golden file |
| Attribute layout | `attribute-layout` golden file |
| Tag wrapping | `tag-wrap-always` golden file |
| CDATA behavior | CDATA golden files, `ValidatorTest`, Gradle functional tests |
| Comments | `sql-comments` golden file, `ValidatorTest`, AST diff tests |
| Comment deletion/move/merge/split rejection | `ValidatorTest` |

## Validation And Diff

| Requirement | Evidence |
| --- | --- |
| AST comparison | `Validator`, `ValidatorTest`, `docs/adr/0091-validator-public-contract.md` |
| Error code classification | `ErrorCode`, `ErrorType`, `ValidationError`, `ConfigLoader.ConfigException`, `ParserException`, `FormatterException`, `ValidationErrorTest`, `MapperXmlParserTest`, `SqlFormatterTest` |
| SQL semantic validation | `Validator`, `ValidatorTest` |
| OGNL semantic validation | `Validator`, `ValidatorTest` |
| Placeholder validation | `PlaceholderParser`, `ValidatorTest` |
| Comment/CDATA/generic validation | `ValidatorTest` |
| Whitespace validation when enabled | `ValidatorTest` |
| Validation error codes and locations | `ValidationError`, `ValidationResult`, `ValidationErrorTest`, `ValidatorTest`, `docs/adr/0090-validation-result-contract.md` |
| AST diff plus unified diff | `AstDiff`, `UnifiedDiff`, diff tests, `docs/adr/0089-diff-public-contract.md`, dry-run functional test |
| Human-readable validation report | `ValidationReportFormatter`, `ValidationReportFormatterTest`, `MapperForgeTask` |

## Logging

| Requirement | Evidence |
| --- | --- |
| SLF4J logging | `MapperForgeLoggers`, `MapperForgeLoggersTest`, Gradle dependencies |
| Parser/formatter/validator/gradle categories | `MapperForgeLoggers`, `MapperForgeLoggersTest` |
| Central logger usage | `MapperXmlParser`, `MapperXmlFormatter`, `Validator`, `MapperForgeTask`, `MapperForgeLoggersTest` |

## Integration And Quality Gates

| Requirement | Evidence |
| --- | --- |
| JUnit tests | `src/test/java` |
| Dockerized PostgreSQL/MySQL MyBatis tests | `MybatisDatabaseIntegrationTest`, `integrationTest` task |
| PostgreSQL/MySQL dialect-specific integration | `MybatisDatabaseIntegrationTest` |
| Spotless integration | `build.gradle.kts`, `spotlessApply`, `spotlessCheck` |
| Golden test workflow docs | `docs/golden-tests.md`, `docs/ja/golden-tests.md` |
| Release checklist | `docs/release.md`, `docs/ja/release.md` |
