package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.ast.sql.PlaceholderType;
import org.junit.jupiter.api.Test;

final class PlaceholderParserTest {
  private final PlaceholderParser parser = new PlaceholderParser();

  @Test
  void parsesHashPlaceholderWithOptions() {
    var placeholder =
        parser.parse("#{id, jdbcType=BIGINT, javaType=long, typeHandler=UserIdHandler}");

    assertEquals(PlaceholderType.HASH, placeholder.type());
    assertEquals("id", placeholder.expression());
    assertEquals("BIGINT", placeholder.options().get("jdbcType"));
    assertEquals("long", placeholder.options().get("javaType"));
    assertEquals("UserIdHandler", placeholder.options().get("typeHandler"));
  }

  @Test
  void parsesDollarPlaceholder() {
    var placeholder = parser.parse("${table}");

    assertEquals(PlaceholderType.DOLLAR, placeholder.type());
    assertEquals("table", placeholder.expression());
    assertEquals(0, placeholder.options().size());
  }

  @Test
  void keepsCommasInsideQuotedOptionValues() {
    var placeholder = parser.parse("#{name, defaultValue='A,B'}");

    assertEquals("A,B", placeholder.options().get("defaultValue"));
  }
}
