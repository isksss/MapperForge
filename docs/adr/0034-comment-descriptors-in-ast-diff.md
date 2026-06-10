# 0034 Comment Descriptors in AST Diff

## Status

Accepted

## Context

PLAN.local.md requires comments to be preserved and validates comment changes. MapperForge also exposes SQL comments as `CommentNode(CommentType.SQL)`. `AstDiff` previously omitted `CommentNode`, so dry-run and library diff output could miss comment-only structural changes in the AST prefix.

## Decision

`AstDiff` includes comment descriptors with the comment type and normalized content:

```text
/mapper[0]/select[0] comment(SQL): -- changed
```

## Consequences

XML and SQL comment changes appear in the AST diff prefix before the unified diff. Formatting-only comment indentation changes remain ignored by the normalized descriptor.
