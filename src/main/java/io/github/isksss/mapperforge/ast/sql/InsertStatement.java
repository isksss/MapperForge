package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record InsertStatement(
    String raw,
    String table,
    List<String> columns,
    List<Expression> values,
    Statement selectSource,
    List<Expression> returning)
    implements Statement {
  public InsertStatement {
    columns = List.copyOf(columns);
    values = List.copyOf(values);
    returning = List.copyOf(returning);
  }

  public InsertStatement(String raw) {
    this(raw, "", List.of(), List.of(), new UnknownStatement(""), List.of());
  }

  public InsertStatement(String raw, String table, List<String> columns, List<Expression> values) {
    this(raw, table, columns, values, new UnknownStatement(""), List.of());
  }
}
