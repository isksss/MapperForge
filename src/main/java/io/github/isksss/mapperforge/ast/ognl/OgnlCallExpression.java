package io.github.isksss.mapperforge.ast.ognl;

import java.util.List;

public record OgnlCallExpression(String name, List<OgnlExpression> arguments)
    implements OgnlExpression {
  public OgnlCallExpression {
    arguments = List.copyOf(arguments);
  }
}
