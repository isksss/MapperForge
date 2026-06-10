package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;

/** 子ノードへ再帰的に同じルールを適用する基底クラスです。 */
abstract class RecursiveRule implements FormatterRule {
  @Override
  public final MapperNode apply(MapperNode node, FormatterContext context) {
    MapperNode current = applyCurrent(node, context);
    if (current instanceof ElementNode element) {
      return ElementNodes.with(
          element,
          element.attributes(),
          element.children().stream().map(child -> apply(child, context)).toList());
    }
    return current;
  }

  /**
   * 現在のノードだけにルールを適用します。
   *
   * @param node 対象の Mapper AST ノード
   * @param context 整形設定と入力ソースを持つコンテキスト
   * @return 現在ノードへルールを適用した結果
   */
  protected MapperNode applyCurrent(MapperNode node, FormatterContext context) {
    return node;
  }
}
