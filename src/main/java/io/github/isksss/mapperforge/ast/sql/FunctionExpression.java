package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record FunctionExpression(String name, List<Expression> arguments) implements Expression {
  public FunctionExpression {
    arguments = List.copyOf(arguments);
  }
}
