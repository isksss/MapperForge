# MapperForge

MapperForge is a formatter and checker for MyBatis Mapper XML.

```kotlin
plugins {
    id("io.github.isksss.mapperforge")
}

mapperForge {
    dialect = "POSTGRESQL"
    include = listOf("src/main/resources/**/*.xml")
    // LEGACY keeps the v1 golden-compatible SQL formatter.
    // AST enables the AST -> Doc Tree -> Layout Engine SQL printer.
    sqlPrinter = "LEGACY"
}
```

```bash
./gradlew mapperForgeFormat
./gradlew mapperForgeCheck
./gradlew mapperForgeDryRun
```

Dockerized MyBatis integration tests:

```bash
./gradlew integrationTest
```
