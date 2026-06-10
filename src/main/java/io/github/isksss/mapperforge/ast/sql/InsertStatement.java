package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record InsertStatement(
    String raw,
    String table,
    List<String> columns,
    List<Expression> values,
    List<List<Expression>> valueRows,
    Statement selectSource,
    List<Expression> returning)
    implements Statement {
  public InsertStatement {
    columns = List.copyOf(columns);
    values = List.copyOf(values);
    valueRows = valueRows.stream().map(List::copyOf).toList();
    returning = List.copyOf(returning);
  }

  public InsertStatement(String raw) {
    this(raw, "", List.of(), List.of(), List.of(), new UnknownStatement(""), List.of());
  }

  public InsertStatement(String raw, String table, List<String> columns, List<Expression> values) {
    this(
        raw,
        table,
        columns,
        values,
        values.isEmpty() ? List.of() : List.of(values),
        new UnknownStatement(""),
        List.of());
  }
}
