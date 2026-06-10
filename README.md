# MapperForge

MapperForge is a formatter and checker for MyBatis Mapper XML.

```kotlin
plugins {
    id("io.github.isksss.mapperforge")
}

mapperForge {
    dialect = "POSTGRESQL"
    include = listOf("src/main/resources/**/*.xml")
}
```

```bash
./gradlew mapperForgeFormat
./gradlew mapperForgeCheck
./gradlew mapperForgeDryRun
```
