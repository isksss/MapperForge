package io.github.isksss.mapperforge.ast.ognl;

import java.util.List;

/**
 * OGNL のコレクションリテラルを表します。
 *
 * @param values 要素
 */
public record OgnlCollectionExpression(List<OgnlExpression> values) implements OgnlExpression {
  /** 要素を不変コピーして生成します。 */
  public OgnlCollectionExpression {
    values = List.copyOf(values);
  }
}
