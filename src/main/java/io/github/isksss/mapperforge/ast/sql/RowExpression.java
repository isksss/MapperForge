package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record RowExpression(List<Expression> values) implements Expression {
  public RowExpression {
    values = List.copyOf(values);
  }
}
