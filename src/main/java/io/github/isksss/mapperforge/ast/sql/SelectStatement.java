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
    Expression offset)
    implements Statement {
  public SelectStatement {
    selectItems = List.copyOf(selectItems);
    groupBy = List.copyOf(groupBy);
    orderBy = List.copyOf(orderBy);
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
        new UnknownExpression(""));
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
        new UnknownExpression(""));
  }

  public record OrderByItem(Expression expression, String direction) {}
}
