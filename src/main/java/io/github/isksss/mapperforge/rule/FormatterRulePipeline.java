package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;
import java.util.List;

public final class FormatterRulePipeline {
  private final List<FormatterRule> rules;

  public FormatterRulePipeline(List<FormatterRule> rules) {
    this.rules = List.copyOf(rules);
  }

  public static FormatterRulePipeline v1() {
    return new FormatterRulePipeline(
        List.of(
            new NormalizeRule(),
            new AttributeOrderRule(),
            new OgnlFormatRule(),
            new SqlFormatRule(),
            new WrapRule()));
  }

  public MapperNode apply(MapperNode node, FormatterContext context) {
    MapperNode current = node;
    for (FormatterRule rule : rules) {
      current = rule.apply(current, context);
    }
    return current;
  }
}
