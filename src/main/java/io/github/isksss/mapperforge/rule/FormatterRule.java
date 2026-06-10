package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;

/** Mapper AST に対して単一の整形規則を適用するルールです。 */
public interface FormatterRule {
  /**
   * 指定されたノードに整形規則を適用します。
   *
   * @param node 対象の Mapper AST ノード
   * @param context 整形設定と入力ソースを持つコンテキスト
   * @return 整形規則を適用した Mapper AST ノード
   */
  MapperNode apply(MapperNode node, FormatterContext context);
}
