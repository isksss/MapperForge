package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

public record WhereElementNode(List<AttributeNode> attributes, List<MapperNode> children)
    implements ElementNode {
  @Override
  public String tagName() {
    return "where";
  }
}
