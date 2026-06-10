package io.github.isksss.mapperforge.parse;

import com.ctc.wstx.stax.WstxInputFactory;
import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.CommentType;
import io.github.isksss.mapperforge.ast.mapper.GenericElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
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
  public Optional<GenericElementNode> parseMapper(SourceFile source) {
    GenericElementNode root = parse(source);
    if (!"mapper".equals(root.tagName())) {
      return Optional.empty();
    }
    return Optional.of(root);
  }

  public GenericElementNode parse(SourceFile source) {
    XMLInputFactory factory = new WstxInputFactory();
    try {
      XMLStreamReader reader =
          factory.createXMLStreamReader(source.fileName(), new StringReader(source.content()));
      ArrayDeque<ElementBuilder> stack = new ArrayDeque<>();
      GenericElementNode root = null;

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
            GenericElementNode element = stack.pop().build();
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
    GenericElementNode build() {
      return new GenericElementNode(tagName, List.copyOf(attributes), List.copyOf(children));
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
