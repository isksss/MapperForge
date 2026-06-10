package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

/**
 * MyBatis XML 要素を表す Mapper AST ノードです。
 *
 * <p>属性と子ノードは生成時に不変コピーへ変換されます。
 *
 * @param attributes 要素属性
 * @param children 子ノード
 */
public record OtherwiseElementNode(List<AttributeNode> attributes, List<MapperNode> children)
    implements ElementNode {
  /** 属性と子ノードを不変コピーして生成します。 */
  public OtherwiseElementNode {
    attributes = ElementNode.copyAttributes(attributes);
    children = ElementNode.copyChildren(children);
  }

  @Override
  public String tagName() {
    return "otherwise";
  }
}
