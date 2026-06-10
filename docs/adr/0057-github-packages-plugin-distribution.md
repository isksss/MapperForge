# 0057 GitHub Packages Plugin Distribution

## Status

Accepted

## Context

MapperForge is a Gradle plugin and should be distributable from GitHub. The repository already uses `java-gradle-plugin` and `maven-publish`, but it did not declare a GitHub Packages repository, release version override, or publish workflow.

## Decision

Publish MapperForge to GitHub Packages as a Maven-backed Gradle plugin package. Release versions are supplied with `-PreleaseVersion`, and tags matching `v*` trigger a GitHub Actions workflow that runs checks and publishes with `GITHUB_TOKEN`.

## Consequences

Consumers can resolve the plugin by adding the GitHub Packages Maven repository to `pluginManagement.repositories`. GitHub Packages requires credentials even for package resolution, so README documents `GITHUB_ACTOR` and `GITHUB_TOKEN` or equivalent Gradle properties.
