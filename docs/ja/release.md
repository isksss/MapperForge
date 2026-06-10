# Release Checklist

MapperForge は GitHub Packages 経由で Gradle plugin package として publish します。

## Prerequisites

- `github.com/isksss/MapperForge` に tag を push できること。
- GitHub Actions が repository で有効であること。
- repository が `GITHUB_TOKEN` で package を publish できること。
- local `integrationTest` 検証のために Docker が利用できること。

## Preflight

CI と release publication を gate するものと同じ check を実行します。

```bash
mise exec java@temurin-21 -- ./gradlew spotlessCheck build integrationTest
mise exec java@temurin-21 -- ./gradlew integrationTest --rerun-tasks
mise exec java@temurin-21 -- ./gradlew publishToMavenLocal -PreleaseVersion=0.1.0-local
```

必要に応じて temporary consumer project から `mavenLocal()` と次の plugin 指定で plugin resolution を確認します。

```kotlin
plugins {
    id("io.github.isksss.mapperforge") version "0.1.0-local"
}
```

## Publish

version tag を作成して push します。tag name は `v` で始めます。package version は tag から `v` prefix を除いた値です。

```bash
git tag v0.1.0
git push origin v0.1.0
```

`Publish` workflow は次を実行します。

```bash
./gradlew spotlessCheck build integrationTest publish -PreleaseVersion=0.1.0
```

## Verify

workflow 成功後に次を確認します。

- repository の GitHub Packages に package が存在すること。
- package version が push した tag から `v` prefix を除いた値と一致すること。
- GitHub Packages repository を `pluginManagement` に設定した consumer build で利用できること。

```kotlin
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
plugins {
    id("io.github.isksss.mapperforge") version "0.1.0"
}
```
