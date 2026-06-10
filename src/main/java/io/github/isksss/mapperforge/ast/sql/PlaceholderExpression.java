package io.github.isksss.mapperforge.ast.sql;

import java.util.Map;

/**
 * MyBatis placeholder expression です。
 *
 * @param type placeholder の種類
 * @param expression placeholder 本体の式
 * @param options jdbcType などの option
 */
public record PlaceholderExpression(
    PlaceholderType type, String expression, Map<String, String> options) implements Expression {
  /** option map を防御コピーします。 */
  public PlaceholderExpression {
    options = Map.copyOf(options);
  }
}
