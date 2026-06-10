# 0064 Golden Test Workflow Documentation

## Status

Accepted

## Context

Golden file tests are MapperForge's main formatting contract, and the test suite already contains simple, dynamic, complex SQL, OGNL, XML, and CDATA cases. The repository did not yet have a contributor-facing workflow that explained how to add cases, when custom formatter config is required, or how idempotence is verified.

## Decision

Add `docs/golden-tests.md` and link it from README. The document defines the `before.xml` / `after.xml` layout, the golden-first workflow, focused and full verification commands, coverage expectations, and CDATA-specific requirements.

## Consequences

Formatter changes have a documented test-driven path. Future contributors can add small and complex golden cases consistently, and CDATA edge cases remain visible as required coverage rather than incidental fixtures.
