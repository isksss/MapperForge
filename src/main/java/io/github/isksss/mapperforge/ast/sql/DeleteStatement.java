package io.github.isksss.mapperforge.ast.sql;

public record DeleteStatement(String raw, String table, Expression where) implements Statement {
  public DeleteStatement(String raw) {
    this(raw, "", new UnknownExpression(""));
  }
}
