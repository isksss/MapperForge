package io.github.isksss.mapperforge.ast.ognl;

import java.util.List;

/**
 * OGNL のメソッド呼び出し式を表します。
 *
 * @param name メソッド名
 * @param arguments 引数
 */
public record OgnlCallExpression(String name, List<OgnlExpression> arguments)
    implements OgnlExpression {
  /** 引数を不変コピーして生成します。 */
  public OgnlCallExpression {
    arguments = List.copyOf(arguments);
  }
}
