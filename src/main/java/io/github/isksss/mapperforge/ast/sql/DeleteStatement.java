package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record DeleteStatement(
    String raw, String table, String using, Expression where, List<Expression> returning)
    implements Statement {
  public DeleteStatement {
    returning = List.copyOf(returning);
  }

  public DeleteStatement(String raw) {
    this(raw, "", "", new UnknownExpression(""), List.of());
  }

  public DeleteStatement(String raw, String table, Expression where) {
    this(raw, table, "", where, List.of());
  }
}
