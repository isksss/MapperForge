package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;

abstract class RecursiveRule implements FormatterRule {
  @Override
  public final MapperNode apply(MapperNode node, FormatterContext context) {
    MapperNode current = applyCurrent(node, context);
    if (current instanceof ElementNode element) {
      return ElementNodes.with(
          element,
          element.attributes(),
          element.children().stream().map(child -> apply(child, context)).toList());
    }
    return current;
  }

  protected MapperNode applyCurrent(MapperNode node, FormatterContext context) {
    return node;
  }
}
