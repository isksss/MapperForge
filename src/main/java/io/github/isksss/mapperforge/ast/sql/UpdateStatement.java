package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record UpdateStatement(
    String raw, String table, List<Assignment> assignments, Expression where) implements Statement {
  public UpdateStatement {
    assignments = List.copyOf(assignments);
  }

  public UpdateStatement(String raw) {
    this(raw, "", List.of(), new UnknownExpression(""));
  }

  public record Assignment(String column, Expression value) {}
}
