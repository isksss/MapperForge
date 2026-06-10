# Release Checklist

MapperForge is published as a Gradle plugin package through GitHub Packages.

## Prerequisites

- You can push tags to `github.com/isksss/MapperForge`.
- GitHub Actions is enabled for the repository.
- The repository has permission to publish packages with `GITHUB_TOKEN`.
- Docker is available for local `integrationTest` verification.

## Preflight

Run the same checks that gate CI and release publication:

```bash
mise exec java@temurin-21 -- ./gradlew spotlessCheck build integrationTest
mise exec java@temurin-21 -- ./gradlew integrationTest --rerun-tasks
mise exec java@temurin-21 -- ./gradlew publishToMavenLocal -PreleaseVersion=0.1.0-local
```

Optionally verify plugin resolution from a temporary consumer project with `mavenLocal()` and:

```kotlin
plugins {
    id("io.github.isksss.mapperforge") version "0.1.0-local"
}
```

## Publish

Create and push a version tag. The tag name must start with `v`; the package version is the tag without the `v` prefix.

```bash
git tag v0.1.0
git push origin v0.1.0
```

The `Publish` workflow runs:

```bash
./gradlew spotlessCheck build integrationTest publish -PreleaseVersion=0.1.0
```

## Verify

After the workflow succeeds:

- Confirm the package exists under GitHub Packages for the repository.
- Confirm the package version matches the pushed tag without the `v` prefix.
- Test a consumer build with the GitHub Packages repository in `pluginManagement`.

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
