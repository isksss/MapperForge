package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record InExpression(Expression expression, List<Expression> values) implements Expression {
  public InExpression {
    values = List.copyOf(values);
  }
}
