# 0025 All Golden Idempotence

## Status

Accepted

## Context

PLAN.local.md requires idempotent formatting. The previous test covered only one golden file, while MapperForge has many golden fixtures including DML, dynamic SQL, CDATA, lossless, OGNL, and AST printer cases.

## Decision

Run idempotence checks for every golden fixture.

- Default golden fixtures use default formatter config
- Custom fixtures reuse their custom config
- Formatter buffers contiguous XML text events before formatting SQL/plain text

## Consequences

Formatter regressions caused by StAX text chunking or custom config drift are caught by golden tests. Golden files now serve as both expected-output tests and `format(format(x)) == format(x)` tests.
