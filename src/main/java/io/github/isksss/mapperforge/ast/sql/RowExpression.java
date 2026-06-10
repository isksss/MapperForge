package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

/**
 * SQL の ROW 式を表します。
 *
 * @param values 行要素
 */
public record RowExpression(List<Expression> values) implements Expression {
  /** 行要素を不変コピーして生成します。 */
  public RowExpression {
    values = List.copyOf(values);
  }
}
