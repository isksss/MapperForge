package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.ArgElementNode;
import io.github.isksss.mapperforge.ast.mapper.AssociationElementNode;
import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.BindElementNode;
import io.github.isksss.mapperforge.ast.mapper.CaseElementNode;
import io.github.isksss.mapperforge.ast.mapper.ChooseElementNode;
import io.github.isksss.mapperforge.ast.mapper.CollectionElementNode;
import io.github.isksss.mapperforge.ast.mapper.ConstructorElementNode;
import io.github.isksss.mapperforge.ast.mapper.DeleteElementNode;
import io.github.isksss.mapperforge.ast.mapper.DiscriminatorElementNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.ForeachElementNode;
import io.github.isksss.mapperforge.ast.mapper.GenericElementNode;
import io.github.isksss.mapperforge.ast.mapper.IdArgElementNode;
import io.github.isksss.mapperforge.ast.mapper.IfElementNode;
import io.github.isksss.mapperforge.ast.mapper.IncludeElementNode;
import io.github.isksss.mapperforge.ast.mapper.InsertElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.OtherwiseElementNode;
import io.github.isksss.mapperforge.ast.mapper.ResultMapElementNode;
import io.github.isksss.mapperforge.ast.mapper.SelectElementNode;
import io.github.isksss.mapperforge.ast.mapper.SetElementNode;
import io.github.isksss.mapperforge.ast.mapper.SqlElementNode;
import io.github.isksss.mapperforge.ast.mapper.TrimElementNode;
import io.github.isksss.mapperforge.ast.mapper.UpdateElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhenElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhereElementNode;
import java.util.List;

final class ElementNodes {
  private ElementNodes() {}

  static ElementNode with(
      ElementNode element, List<AttributeNode> attributes, List<MapperNode> children) {
    List<AttributeNode> nextAttributes = List.copyOf(attributes);
    List<MapperNode> nextChildren = List.copyOf(children);
    return switch (element) {
      case MapperElementNode ignored -> new MapperElementNode(nextAttributes, nextChildren);
      case SelectElementNode ignored -> new SelectElementNode(nextAttributes, nextChildren);
      case InsertElementNode ignored -> new InsertElementNode(nextAttributes, nextChildren);
      case UpdateElementNode ignored -> new UpdateElementNode(nextAttributes, nextChildren);
      case DeleteElementNode ignored -> new DeleteElementNode(nextAttributes, nextChildren);
      case SqlElementNode ignored -> new SqlElementNode(nextAttributes, nextChildren);
      case ResultMapElementNode ignored -> new ResultMapElementNode(nextAttributes, nextChildren);
      case AssociationElementNode ignored ->
          new AssociationElementNode(nextAttributes, nextChildren);
      case CollectionElementNode ignored -> new CollectionElementNode(nextAttributes, nextChildren);
      case ConstructorElementNode ignored ->
          new ConstructorElementNode(nextAttributes, nextChildren);
      case ArgElementNode ignored -> new ArgElementNode(nextAttributes, nextChildren);
      case IdArgElementNode ignored -> new IdArgElementNode(nextAttributes, nextChildren);
      case DiscriminatorElementNode ignored ->
          new DiscriminatorElementNode(nextAttributes, nextChildren);
      case CaseElementNode ignored -> new CaseElementNode(nextAttributes, nextChildren);
      case IfElementNode ignored -> new IfElementNode(nextAttributes, nextChildren);
      case ChooseElementNode ignored -> new ChooseElementNode(nextAttributes, nextChildren);
      case WhenElementNode ignored -> new WhenElementNode(nextAttributes, nextChildren);
      case OtherwiseElementNode ignored -> new OtherwiseElementNode(nextAttributes, nextChildren);
      case ForeachElementNode ignored -> new ForeachElementNode(nextAttributes, nextChildren);
      case TrimElementNode ignored -> new TrimElementNode(nextAttributes, nextChildren);
      case WhereElementNode ignored -> new WhereElementNode(nextAttributes, nextChildren);
      case SetElementNode ignored -> new SetElementNode(nextAttributes, nextChildren);
      case IncludeElementNode ignored -> new IncludeElementNode(nextAttributes, nextChildren);
      case BindElementNode ignored -> new BindElementNode(nextAttributes, nextChildren);
      case GenericElementNode generic ->
          new GenericElementNode(generic.tagName(), nextAttributes, nextChildren);
    };
  }
}
