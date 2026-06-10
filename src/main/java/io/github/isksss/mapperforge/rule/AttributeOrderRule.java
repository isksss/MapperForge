package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;
import java.util.Comparator;
import java.util.List;

public final class AttributeOrderRule extends RecursiveRule {
  @Override
  protected MapperNode applyCurrent(MapperNode node, FormatterContext context) {
    if (!(node instanceof ElementNode element)) {
      return node;
    }
    List<String> order = context.config().attributeOrder().get(element.tagName());
    if (order == null || order.isEmpty()) {
      return node;
    }
    List<AttributeNode> attributes =
        element.attributes().stream()
            .sorted(
                Comparator.comparingInt(
                    attribute -> {
                      int index = order.indexOf(attribute.name());
                      return index >= 0 ? index : Integer.MAX_VALUE;
                    }))
            .toList();
    return ElementNodes.with(element, attributes, element.children());
  }
}
