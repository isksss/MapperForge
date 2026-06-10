package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record SelectStatement(
    String raw, List<Expression> selectItems, String from, Expression where) implements Statement {
  public SelectStatement {
    selectItems = List.copyOf(selectItems);
  }

  public SelectStatement(String raw) {
    this(raw, List.of(), "", new UnknownExpression(""));
  }
}
