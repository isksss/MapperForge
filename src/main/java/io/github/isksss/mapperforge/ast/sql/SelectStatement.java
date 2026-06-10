package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record SelectStatement(
    String raw,
    List<Expression> selectItems,
    String from,
    Expression where,
    List<Expression> groupBy,
    Expression having,
    List<OrderByItem> orderBy,
    Expression limit,
    Expression offset,
    List<JoinItem> joins,
    List<WindowItem> windows,
    FetchClause fetch)
    implements Statement {
  public SelectStatement {
    selectItems = List.copyOf(selectItems);
    groupBy = List.copyOf(groupBy);
    orderBy = List.copyOf(orderBy);
    joins = List.copyOf(joins);
    windows = List.copyOf(windows);
  }

  public SelectStatement(String raw) {
    this(
        raw,
        List.of(),
        "",
        new UnknownExpression(""),
        List.of(),
        new UnknownExpression(""),
        List.of(),
        new UnknownExpression(""),
        new UnknownExpression(""),
        List.of(),
        List.of(),
        new FetchClause("", new UnknownExpression("")));
  }

  public SelectStatement(String raw, List<Expression> selectItems, String from, Expression where) {
    this(
        raw,
        selectItems,
        from,
        where,
        List.of(),
        new UnknownExpression(""),
        List.of(),
        new UnknownExpression(""),
        new UnknownExpression(""),
        List.of(),
        List.of(),
        new FetchClause("", new UnknownExpression("")));
  }

  public record OrderByItem(Expression expression, String direction) {}

  public record JoinItem(String kind, String table, Expression on) {}

  public record WindowItem(String name, String spec) {}

  public record FetchClause(String raw, Expression count) {}
}
