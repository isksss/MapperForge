package io.github.isksss.mapperforge.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.CommentType;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class MapperAstLeafNodeTest {
  @Test
  void attributeNodePreservesNameAndValueExactly() {
    AttributeNode attribute = new AttributeNode("test", "id != null && name != 'A>B'");

    assertEquals("test", attribute.name());
    assertEquals("id != null && name != 'A>B'", attribute.value());
  }

  @Test
  void textNodePreservesTypeAndValueExactly() {
    TextNode sql = new TextNode(TextType.SQL, "select * from users where name < #{name}");
    TextNode whitespace = new TextNode(TextType.WHITESPACE, "\n    ");

    assertEquals(TextType.SQL, sql.type());
    assertEquals("select * from users where name < #{name}", sql.value());
    assertEquals(TextType.WHITESPACE, whitespace.type());
    assertEquals("\n    ", whitespace.value());
  }

  @Test
  void commentNodePreservesXmlAndSqlCommentsExactly() {
    CommentNode xml = new CommentNode(CommentType.XML, " keep <where> notes & spacing ");
    CommentNode sql = new CommentNode(CommentType.SQL, "-- keep optimizer hint");

    assertEquals(CommentType.XML, xml.type());
    assertEquals(" keep <where> notes & spacing ", xml.content());
    assertEquals(CommentType.SQL, sql.type());
    assertEquals("-- keep optimizer hint", sql.content());
  }

  @Test
  void cdataNodePreservesEscapingSensitiveCharactersExactly() {
    String raw = "name < #{name} and code > #{code} and enabled & archived";
    CDataNode cdata = new CDataNode(raw, Optional.empty());

    assertEquals(raw, cdata.raw());
    assertTrue(cdata.parsed().isEmpty());
  }

  @Test
  void cdataNodePreservesParsedSqlNodeReference() {
    UnknownStatement parsed = new UnknownStatement("select <invalid>");
    CDataNode cdata = new CDataNode("select <invalid>", Optional.of(parsed));

    assertEquals("select <invalid>", cdata.raw());
    assertSame(parsed, cdata.parsed().orElseThrow());
  }
}
