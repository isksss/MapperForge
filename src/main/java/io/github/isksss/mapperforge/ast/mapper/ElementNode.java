package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

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
  String tagName();

  List<AttributeNode> attributes();

  List<MapperNode> children();
}
