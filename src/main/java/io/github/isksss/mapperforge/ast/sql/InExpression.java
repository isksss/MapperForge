package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

/**
 * SQL の IN 式を表します。
 *
 * @param expression 判定対象式
 * @param values 候補値
 */
public record InExpression(Expression expression, List<Expression> values) implements Expression {
  /** 候補値を不変コピーして生成します。 */
  public InExpression {
    values = List.copyOf(values);
  }
}
