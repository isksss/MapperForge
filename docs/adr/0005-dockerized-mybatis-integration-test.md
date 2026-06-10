# ADR 0005: Dockerized MyBatis Integration Test

## Status

Accepted

## Context

MapperForge formats MyBatis Mapper XML, so formatting must not only be syntactically valid XML.
The formatted mapper should still load in MyBatis and execute against supported databases.

## Decision

- Add a dedicated `integrationTest` Gradle task.
- Use Testcontainers to start PostgreSQL and MySQL with Docker.
- Keep the test outside the default `build` task so local unit/golden feedback stays fast and does not require Docker.
- Use golden mapper XML as the input and expected formatted output for the integration test.

## Consequences

Developers can run `./gradlew integrationTest` when Docker is available.
The same test verifies formatter output, MyBatis XML loading, and execution against both supported dialects.
