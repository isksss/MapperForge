package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;
import io.github.isksss.mapperforge.format.OgnlFormatter;

/** MyBatis 動的 SQL の OGNL 属性を整形するルールです。 */
public final class OgnlFormatRule extends RecursiveRule {
  private final OgnlFormatter formatter = new OgnlFormatter();

  /** OGNL 整形ルールを作成します。 */
  public OgnlFormatRule() {}

  @Override
  protected MapperNode applyCurrent(MapperNode node, FormatterContext context) {
    if (!(node instanceof ElementNode element)) {
      return node;
    }
    var attributes =
        element.attributes().stream()
            .map(
                attribute ->
                    "test".equals(attribute.name())
                        ? new AttributeNode(attribute.name(), formatter.format(attribute.value()))
                        : attribute)
            .toList();
    return ElementNodes.with(element, attributes, element.children());
  }
}
