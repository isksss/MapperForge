package io.github.isksss.mapperforge.ast.ognl;

import java.util.List;

public record OgnlCollectionExpression(List<OgnlExpression> values) implements OgnlExpression {
  public OgnlCollectionExpression {
    values = List.copyOf(values);
  }
}
