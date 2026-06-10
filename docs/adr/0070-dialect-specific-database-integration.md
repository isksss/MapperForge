# 0070 Dialect Specific Database Integration

## Status

Accepted

## Context

`PLAN.local.md` lists PostgreSQL and MySQL as supported DBs and exposes `dialect` as user configuration. MapperForge already ran Dockerized MyBatis tests against both databases, but the MySQL cases used the default formatter config, whose dialect is `POSTGRESQL`.

## Decision

Run the PostgreSQL integration cases with `Dialect.POSTGRESQL` and the MySQL integration cases with `Dialect.MYSQL`. Keep the same mapper golden outputs so this change strengthens verification scope without changing formatting behavior.

## Consequences

The real database integration tests now prove that each supported database path is exercised with its matching MapperForge dialect setting. Future dialect-specific formatter behavior will be covered by the existing PostgreSQL/MySQL integration matrix.
