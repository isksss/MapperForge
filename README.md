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

See [docs/configuration.md](docs/configuration.md) for all configuration options.

See [docs/tasks.md](docs/tasks.md) for task behavior.

See [docs/traceability.md](docs/traceability.md) for the PLAN-to-implementation traceability map.

## GitHub Packages

MapperForge can be consumed as a Gradle plugin from GitHub Packages.

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.github.com/isksss/MapperForge") {
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

```kotlin
// build.gradle.kts
plugins {
    id("io.github.isksss.mapperforge") version "0.1.0"
}
```

Publish a release package by pushing a version tag:

```bash
git tag v0.1.0
git push origin v0.1.0
```

Local publication check:

```bash
./gradlew publishToMavenLocal -PreleaseVersion=0.1.0-local
```

See [docs/release.md](docs/release.md) for the release checklist.
