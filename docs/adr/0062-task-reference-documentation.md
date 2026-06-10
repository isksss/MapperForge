# 0062 Task Reference Documentation

## Status

Accepted

## Context

PLAN.local.md defines `Format`, `Check`, `DryRun`, `Diff`, and `Validation` as core plugin features. README listed the task names but did not document write behavior, validation behavior, cacheability, or file selection.

## Decision

Add `docs/tasks.md` as the Gradle task reference and link it from README. The document records the behavior of `mapperForgeFormat`, `mapperForgeCheck`, and `mapperForgeDryRun`, including validation, diff output, and cache expectations.

## Consequences

Users can choose the correct task for local formatting, CI checks, or review diffs without reading Gradle task implementation.
