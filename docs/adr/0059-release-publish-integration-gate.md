# 0059 Release Publish Integration Gate

## Status

Accepted

## Context

MapperForge release packages are published from GitHub tags. CI already runs Dockerized MyBatis integration tests, but the tag-based publish workflow only ran `spotlessCheck`, `build`, and `publish`.

## Decision

Require `integrationTest` in the tag-based publish workflow before publishing to GitHub Packages. The release command now runs `spotlessCheck build integrationTest publish` with the tag-derived release version.

## Consequences

Published plugin artifacts are gated by the same real PostgreSQL/MySQL MyBatis integration coverage as normal CI.
