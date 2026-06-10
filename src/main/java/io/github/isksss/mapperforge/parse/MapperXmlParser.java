package io.github.isksss.mapperforge.parse;

import com.ctc.wstx.stax.WstxInputFactory;
import io.github.isksss.mapperforge.ast.mapper.ArgElementNode;
import io.github.isksss.mapperforge.ast.mapper.AssociationElementNode;
import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.BindElementNode;
import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CaseElementNode;
import io.github.isksss.mapperforge.ast.mapper.ChooseElementNode;
import io.github.isksss.mapperforge.ast.mapper.CollectionElementNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.CommentType;
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
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.ast.mapper.TrimElementNode;
import io.github.isksss.mapperforge.ast.mapper.UpdateElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhenElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhereElementNode;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class MapperXmlParser {
  public Optional<MapperElementNode> parseMapper(SourceFile source) {
    ElementNode root = parse(source);
    if (!(root instanceof MapperElementNode mapper)) {
      return Optional.empty();
    }
    return Optional.of(mapper);
  }

  public ElementNode parse(SourceFile source) {
    XMLInputFactory factory = new WstxInputFactory();
    try {
      XMLStreamReader reader =
          factory.createXMLStreamReader(source.fileName(), new StringReader(source.content()));
      ArrayDeque<ElementBuilder> stack = new ArrayDeque<>();
      ElementNode root = null;

      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
          case XMLStreamConstants.START_ELEMENT -> stack.push(startElement(reader));
          case XMLStreamConstants.CHARACTERS, XMLStreamConstants.SPACE -> {
            if (!stack.isEmpty()) {
              String text = reader.getText();
              if (!text.isBlank()) {
                stack.peek().children().add(new TextNode(TextType.PLAIN_TEXT, text.strip()));
              }
            }
          }
          case XMLStreamConstants.CDATA -> {
            if (!stack.isEmpty()) {
              stack.peek().children().add(new CDataNode(reader.getText(), Optional.empty()));
            }
          }
          case XMLStreamConstants.COMMENT -> {
            if (!stack.isEmpty()) {
              stack.peek().children().add(new CommentNode(CommentType.XML, reader.getText()));
            }
          }
          case XMLStreamConstants.END_ELEMENT -> {
            ElementNode element = stack.pop().build();
            if (stack.isEmpty()) {
              root = element;
            } else {
              stack.peek().children().add(element);
            }
          }
          default -> {}
        }
      }
      if (root == null) {
        throw new ParserException("XML does not contain a root element: " + source.fileName());
      }
      return root;
    } catch (XMLStreamException e) {
      throw new ParserException("Failed to parse XML: " + source.fileName(), e);
    }
  }

  private ElementBuilder startElement(XMLStreamReader reader) {
    List<AttributeNode> attributes = new ArrayList<>();
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      attributes.add(
          new AttributeNode(reader.getAttributeLocalName(i), reader.getAttributeValue(i)));
    }
    return new ElementBuilder(reader.getLocalName(), attributes, new ArrayList<>());
  }

  private record ElementBuilder(
      String tagName, List<AttributeNode> attributes, List<MapperNode> children) {
    ElementNode build() {
      List<AttributeNode> immutableAttributes = List.copyOf(attributes);
      List<MapperNode> immutableChildren = List.copyOf(children);
      return switch (tagName) {
        case "mapper" -> new MapperElementNode(immutableAttributes, immutableChildren);
        case "select" -> new SelectElementNode(immutableAttributes, immutableChildren);
        case "insert" -> new InsertElementNode(immutableAttributes, immutableChildren);
        case "update" -> new UpdateElementNode(immutableAttributes, immutableChildren);
        case "delete" -> new DeleteElementNode(immutableAttributes, immutableChildren);
        case "sql" -> new SqlElementNode(immutableAttributes, immutableChildren);
        case "resultMap" -> new ResultMapElementNode(immutableAttributes, immutableChildren);
        case "association" -> new AssociationElementNode(immutableAttributes, immutableChildren);
        case "collection" -> new CollectionElementNode(immutableAttributes, immutableChildren);
        case "constructor" -> new ConstructorElementNode(immutableAttributes, immutableChildren);
        case "arg" -> new ArgElementNode(immutableAttributes, immutableChildren);
        case "idArg" -> new IdArgElementNode(immutableAttributes, immutableChildren);
        case "discriminator" ->
            new DiscriminatorElementNode(immutableAttributes, immutableChildren);
        case "case" -> new CaseElementNode(immutableAttributes, immutableChildren);
        case "if" -> new IfElementNode(immutableAttributes, immutableChildren);
        case "choose" -> new ChooseElementNode(immutableAttributes, immutableChildren);
        case "when" -> new WhenElementNode(immutableAttributes, immutableChildren);
        case "otherwise" -> new OtherwiseElementNode(immutableAttributes, immutableChildren);
        case "foreach" -> new ForeachElementNode(immutableAttributes, immutableChildren);
        case "trim" -> new TrimElementNode(immutableAttributes, immutableChildren);
        case "where" -> new WhereElementNode(immutableAttributes, immutableChildren);
        case "set" -> new SetElementNode(immutableAttributes, immutableChildren);
        case "include" -> new IncludeElementNode(immutableAttributes, immutableChildren);
        case "bind" -> new BindElementNode(immutableAttributes, immutableChildren);
        default -> new GenericElementNode(tagName, immutableAttributes, immutableChildren);
      };
    }
  }

  public static final class ParserException extends RuntimeException {
    public ParserException(String message) {
      super(message);
    }

    public ParserException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
