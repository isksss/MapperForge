# 0023 Preserve Whitespace Validation

## Status

Accepted

## Context

PLAN.local.md states that whitespace should be compared only when `preserveWhitespace=true`. The mapper AST parser intentionally discards blank XML text nodes in the normal formatting path, so default validation accepted whitespace-only formatting changes.

## Decision

Validator performs a separate whitespace sequence comparison when `preserveWhitespace=true`.

- Default `preserveWhitespace=false` continues to ignore indentation-only changes
- When enabled, whitespace-only text between XML tags is compared before AST/signature checks
- Whitespace changes are reported as `ErrorType.WHITESPACE`

## Consequences

Users can opt into stricter lossless validation for XML whitespace without changing the default formatter behavior.
