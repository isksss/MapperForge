package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

/**
 * SQL の ARRAY 式を表します。
 *
 * @param values 配列要素
 */
public record ArrayExpression(List<Expression> values) implements Expression {
  /** 配列要素を不変コピーして生成します。 */
  public ArrayExpression {
    values = List.copyOf(values);
  }
}
