# 0060 Release Checklist Documentation

## Status

Accepted

## Context

MapperForge is published from GitHub tags to GitHub Packages. README had a minimal tag command, but it did not document the preflight checks, local plugin publication check, or post-publish verification.

## Decision

Add `docs/release.md` as the release checklist and link it from README. The checklist records preflight commands, tag naming, the publish workflow command, and consumer verification steps.

## Consequences

Release work has an explicit repeatable checklist. This reduces the chance of publishing a plugin artifact without running golden tests, Dockerized database integration tests, or plugin marker publication checks.
