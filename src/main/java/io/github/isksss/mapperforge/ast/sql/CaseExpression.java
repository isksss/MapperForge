package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

public record CaseExpression(List<WhenClause> whenClauses, Expression elseExpression)
    implements Expression {
  public CaseExpression {
    whenClauses = List.copyOf(whenClauses);
  }

  public record WhenClause(Expression condition, Expression result) {}
}
