package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

/**
 * SQL の CASE 式を表します。
 *
 * @param whenClauses WHEN 句
 * @param elseExpression ELSE 式
 */
public record CaseExpression(List<WhenClause> whenClauses, Expression elseExpression)
    implements Expression {
  /** WHEN 句を不変コピーして生成します。 */
  public CaseExpression {
    whenClauses = List.copyOf(whenClauses);
  }

  /**
   * CASE 式の WHEN 句を表します。
   *
   * @param condition 条件式
   * @param result 結果式
   */
  public record WhenClause(Expression condition, Expression result) {}
}
