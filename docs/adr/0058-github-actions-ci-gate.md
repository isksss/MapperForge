# 0058 GitHub Actions CI Gate

## Status

Accepted

## Context

MapperForge is distributed from GitHub Packages and relies on golden tests, Gradle plugin functional tests, and Dockerized MyBatis integration tests. The repository had a tag-based publish workflow but no regular push or pull request CI gate.

## Decision

Add a GitHub Actions CI workflow for `main` pushes and pull requests. The workflow runs `spotlessCheck`, `build`, Docker-backed `integrationTest`, and `publishToMavenLocal` with a CI version to verify plugin marker publication.

## Consequences

Normal changes are verified before release tags are created. The publish workflow remains responsible only for GitHub Packages publication.
