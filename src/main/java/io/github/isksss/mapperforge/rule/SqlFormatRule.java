package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.format.FormatterContext;
import io.github.isksss.mapperforge.format.SqlFormatter;

/** SQL テキストノードを設定に従って整形するルールです。 */
public final class SqlFormatRule extends RecursiveRule {
  private final SqlFormatter formatter = new SqlFormatter();

  /** SQL 整形ルールを作成します。 */
  public SqlFormatRule() {}

  @Override
  protected MapperNode applyCurrent(MapperNode node, FormatterContext context) {
    if (node instanceof TextNode text && text.type() == TextType.SQL) {
      return new TextNode(text.type(), formatter.format(text.value(), context.config()));
    }
    return node;
  }
}
