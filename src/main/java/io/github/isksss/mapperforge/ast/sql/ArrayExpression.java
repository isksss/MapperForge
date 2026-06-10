package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record ArrayExpression(List<Expression> values) implements Expression {
  public ArrayExpression {
    values = List.copyOf(values);
  }
}
