# 0092 Config Loader Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines `mapperforge.yml` as the supplemental configuration file and requires defaults when no configuration exists. `ConfigLoader` is the public boundary for loading that file, but its public contract and non-mapping YAML failure behavior were not documented directly.

## Decision

Add Japanese Javadocs to `ConfigLoader` and `ConfigLoader.ConfigException`.

Extend `ConfigLoaderTest` to verify that a YAML root value that is not a mapping fails with `CONFIG_ERROR` and the stable message `mapperforge.yml must contain a mapping`.

## Consequences

Configuration loading has a documented public boundary. Future YAML schema changes must preserve the root mapping contract or update the ADR and tests.
