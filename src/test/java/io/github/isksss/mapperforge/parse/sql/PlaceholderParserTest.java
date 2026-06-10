package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.isksss.mapperforge.ast.sql.PlaceholderExpression;
import io.github.isksss.mapperforge.ast.sql.PlaceholderType;
import java.util.LinkedHashMap;
import java.util.Map;
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

  @Test
  void placeholderExpressionDefensivelyCopiesOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    options.put("jdbcType", "BIGINT");

    PlaceholderExpression placeholder =
        new PlaceholderExpression(PlaceholderType.HASH, "id", options);
    options.put("javaType", "long");

    assertEquals(Map.of("jdbcType", "BIGINT"), placeholder.options());
    assertThrows(
        UnsupportedOperationException.class,
        () -> placeholder.options().put("typeHandler", "UserIdHandler"));
  }
}
