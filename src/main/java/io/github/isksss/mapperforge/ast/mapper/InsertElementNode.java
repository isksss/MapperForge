package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

public record InsertElementNode(List<AttributeNode> attributes, List<MapperNode> children)
    implements ElementNode {
  @Override
  public String tagName() {
    return "insert";
  }
}
