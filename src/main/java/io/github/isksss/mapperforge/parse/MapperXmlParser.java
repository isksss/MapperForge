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
import io.github.isksss.mapperforge.error.ErrorCode;
import io.github.isksss.mapperforge.logging.MapperForgeLoggers;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import io.github.isksss.mapperforge.parse.sql.SqlTokenizer;
import io.github.isksss.mapperforge.source.SourceFile;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/** MyBatis Mapper XML を Woodstox で読み取り、Mapper AST へ変換します。 */
public final class MapperXmlParser {
  private static final Set<String> SQL_TEXT_TAGS =
      Set.of(
          "select",
          "insert",
          "update",
          "delete",
          "sql",
          "if",
          "when",
          "otherwise",
          "foreach",
          "trim",
          "where",
          "set");

  /** parser instance を作成します。 */
  public MapperXmlParser() {}

  /**
   * source を MyBatis mapper として parse します。
   *
   * @param source parse 対象の source
   * @return root が {@code <mapper>} の場合は mapper node、それ以外は空
   * @throws ParserException XML として parse できない場合
   */
  public Optional<MapperElementNode> parseMapper(SourceFile source) {
    MapperForgeLoggers.PARSER.debug("Parsing mapper XML: {}", source.fileName());
    ElementNode root = parse(source);
    if (!(root instanceof MapperElementNode mapper)) {
      return Optional.empty();
    }
    return Optional.of(mapper);
  }

  /**
   * source XML を root element node へ parse します。
   *
   * @param source parse 対象の source
   * @return root element node
   * @throws ParserException XML として parse できない場合、または root element がない場合
   */
  public ElementNode parse(SourceFile source) {
    MapperForgeLoggers.PARSER.debug("Parsing XML source: {}", source.fileName());
    XMLInputFactory factory = new WstxInputFactory();
    try {
      XMLStreamReader reader =
          factory.createXMLStreamReader(source.fileName(), new StringReader(source.content()));
      ArrayDeque<ElementBuilder> stack = new ArrayDeque<>();
      StringBuilder textBuffer = new StringBuilder();
      ElementNode root = null;

      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
          case XMLStreamConstants.START_ELEMENT -> {
            flushText(stack, textBuffer);
            stack.push(startElement(reader));
          }
          case XMLStreamConstants.CHARACTERS, XMLStreamConstants.SPACE -> {
            if (!stack.isEmpty()) {
              String text = reader.getText();
              if (!text.isBlank()) {
                textBuffer.append(text);
              }
            }
          }
          case XMLStreamConstants.CDATA -> {
            flushText(stack, textBuffer);
            if (!stack.isEmpty()) {
              String raw = reader.getText();
              stack
                  .peek()
                  .children()
                  .add(new CDataNode(raw, Optional.of(new SqlStatementParser(raw).parse())));
            }
          }
          case XMLStreamConstants.COMMENT -> {
            flushText(stack, textBuffer);
            if (!stack.isEmpty()) {
              stack.peek().children().add(new CommentNode(CommentType.XML, reader.getText()));
            }
          }
          case XMLStreamConstants.END_ELEMENT -> {
            flushText(stack, textBuffer);
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

  private void flushText(ArrayDeque<ElementBuilder> stack, StringBuilder textBuffer) {
    if (stack.isEmpty() || textBuffer.isEmpty()) {
      return;
    }
    stack.peek().children().addAll(textNodes(stack.peek().tagName(), textBuffer.toString()));
    textBuffer.setLength(0);
  }

  private TextType textType(String tagName) {
    return SQL_TEXT_TAGS.contains(tagName) ? TextType.SQL : TextType.PLAIN_TEXT;
  }

  private List<MapperNode> textNodes(String tagName, String text) {
    TextType type = textType(tagName);
    if (type != TextType.SQL) {
      return List.of(new TextNode(type, text.strip()));
    }
    List<MapperNode> nodes = new ArrayList<>();
    int offset = 0;
    for (Token token : new SqlTokenizer(text).tokenize()) {
      if (token.type() != TokenType.COMMENT) {
        continue;
      }
      int start = token.range().start().offset();
      if (offset < start) {
        addSqlTextNode(nodes, text.substring(offset, start));
      }
      nodes.add(new CommentNode(CommentType.SQL, token.text()));
      offset = token.range().end().offset();
    }
    if (offset < text.length()) {
      addSqlTextNode(nodes, text.substring(offset));
    }
    if (nodes.isEmpty()) {
      addSqlTextNode(nodes, text);
    }
    return List.copyOf(nodes);
  }

  private void addSqlTextNode(List<MapperNode> nodes, String text) {
    if (!text.isBlank()) {
      nodes.add(new TextNode(TextType.SQL, text.strip()));
    }
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

  /** Mapper XML parser の失敗を表す例外です。 */
  public static final class ParserException extends RuntimeException {
    private final ErrorCode code;

    /**
     * message 付き parser exception を作成します。
     *
     * @param message error message
     */
    public ParserException(String message) {
      this(message, null);
    }

    /**
     * message と cause 付き parser exception を作成します。
     *
     * @param message error message
     * @param cause 原因例外
     */
    public ParserException(String message, Throwable cause) {
      super(message, cause);
      this.code = ErrorCode.PARSER_ERROR;
    }

    /**
     * parser error code を返します。
     *
     * @return {@link ErrorCode#PARSER_ERROR}
     */
    public ErrorCode code() {
      return code;
    }
  }
}
