package io.github.isksss.mapperforge.ast.sql;

import java.util.List;

/**
 * SQL 関数呼び出し式を表します。
 *
 * @param name 関数名
 * @param arguments 引数
 */
public record FunctionExpression(String name, List<Expression> arguments) implements Expression {
  /** 引数を不変コピーして生成します。 */
  public FunctionExpression {
    arguments = List.copyOf(arguments);
  }
}
