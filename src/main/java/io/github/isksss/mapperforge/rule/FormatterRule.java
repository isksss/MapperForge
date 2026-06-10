package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;

public interface FormatterRule {
  MapperNode apply(MapperNode node, FormatterContext context);
}
