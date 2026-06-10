package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record InsertStatement(
    String raw, String table, List<String> columns, List<Expression> values) implements Statement {
  public InsertStatement {
    columns = List.copyOf(columns);
    values = List.copyOf(values);
  }

  public InsertStatement(String raw) {
    this(raw, "", List.of(), List.of());
  }
}
