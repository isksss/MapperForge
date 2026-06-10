# 0087 Mapper Leaf Node Lossless Contract

## Status

Accepted

## Context

`PLAN.local.md` requires lossless preservation for comments, CDATA, placeholders, unknown nodes, and generic elements. Mapper leaf nodes carry the exact text surfaces that validation and formatting must preserve, including CDATA text that contains XML escaping-sensitive characters such as `<`, `>`, and `&`.

## Decision

Document mapper leaf records and enums with Japanese Javadoc.

Add `MapperAstLeafNodeTest` to verify exact value preservation for `AttributeNode`, `TextNode`, `CommentNode`, and `CDataNode`, including CDATA containing `<`, `>`, and `&`.

## Consequences

Future mapper leaf changes must preserve raw textual values exactly unless a separate ADR records an intentional semantic change.
