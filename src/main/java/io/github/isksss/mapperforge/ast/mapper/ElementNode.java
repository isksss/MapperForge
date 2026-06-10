package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

/**
 * XML 要素を表す Mapper AST ノードです。
 *
 * <p>属性と子ノードのリストは、実装 record の生成時に不変コピーへ変換します。
 */
public sealed interface ElementNode extends MapperNode
    permits MapperElementNode,
        SelectElementNode,
        InsertElementNode,
        UpdateElementNode,
        DeleteElementNode,
        SqlElementNode,
        ResultMapElementNode,
        AssociationElementNode,
        CollectionElementNode,
        ConstructorElementNode,
        ArgElementNode,
        IdArgElementNode,
        DiscriminatorElementNode,
        CaseElementNode,
        IfElementNode,
        ChooseElementNode,
        WhenElementNode,
        OtherwiseElementNode,
        ForeachElementNode,
        TrimElementNode,
        WhereElementNode,
        SetElementNode,
        IncludeElementNode,
        BindElementNode,
        GenericElementNode {
  /**
   * XML タグ名を返します。
   *
   * @return タグ名
   */
  String tagName();

  /**
   * 要素属性を宣言順で返します。
   *
   * @return 不変な属性リスト
   */
  List<AttributeNode> attributes();

  /**
   * 子ノードを出現順で返します。
   *
   * @return 不変な子ノードリスト
   */
  List<MapperNode> children();

  /**
   * 属性リストを不変コピーに変換します。
   *
   * @param attributes 属性リスト
   * @return 不変コピー
   */
  static List<AttributeNode> copyAttributes(List<AttributeNode> attributes) {
    return List.copyOf(attributes);
  }

  /**
   * 子ノードリストを不変コピーに変換します。
   *
   * @param children 子ノードリスト
   * @return 不変コピー
   */
  static List<MapperNode> copyChildren(List<MapperNode> children) {
    return List.copyOf(children);
  }
}
