# 0079 Doc Tree Public Contract

## Status

Accepted

## Context

`PLAN.local.md` defines the printer path as `AST -> Doc Tree -> Layout Engine -> String` and lists `TextDoc`, `LineDoc`, `SoftLineDoc`, `HardLineDoc`, `IndentDoc`, `GroupDoc`, and `ConcatDoc`.
The layout engine already had rendering tests, but the public Doc Tree factory and node contract were only indirectly covered.

## Decision

Add `DocTreeTest` to verify that `Docs` creates every PLAN-defined Doc node type, `TextDoc` normalizes null text to an empty string, and `ConcatDoc` defensively copies child nodes.
Add Japanese Javadocs to the public Doc Tree node types, `Docs`, and `LayoutEngine`.

## Consequences

Future printer work has a direct compatibility contract for the public document model.
Changing Doc Tree construction or immutability semantics now requires updating the focused test and this ADR.
