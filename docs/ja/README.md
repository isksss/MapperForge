# MapperForge

MapperForge は MyBatis Mapper XML 向けの formatter / checker です。

```kotlin
plugins {
    id("io.github.isksss.mapperforge")
}

mapperForge {
    dialect = "POSTGRESQL"
    include = listOf("src/main/resources/**/*.xml")
    // LEGACY は v1 golden file と互換の SQL formatter を使います。
    // AST は AST -> Doc Tree -> Layout Engine の SQL printer を使います。
    sqlPrinter = "LEGACY"
}
```

```bash
./gradlew mapperForgeFormat
./gradlew mapperForgeCheck
./gradlew mapperForgeDryRun
```

Dockerized MyBatis integration test:

```bash
./gradlew integrationTest
```

全設定項目は [configuration.md](configuration.md) を参照してください。

Gradle task の挙動は [tasks.md](tasks.md) を参照してください。

Golden file test の運用は [golden-tests.md](golden-tests.md) を参照してください。

`PLAN.local.md` と実装の対応は [traceability.md](traceability.md) を参照してください。

## GitHub Packages

MapperForge は GitHub Packages から Gradle plugin として利用できます。

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

version tag を push すると release package を publish します。

```bash
git tag v0.1.0
git push origin v0.1.0
```

local publication check:

```bash
./gradlew publishToMavenLocal -PreleaseVersion=0.1.0-local
```

release checklist は [release.md](release.md) を参照してください。
