package io.github.isksss.mapperforge.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class FormatterConfigTest {
  @Test
  void defaultsMatchPlanAndDocumentation() {
    FormatterConfig defaults = FormatterConfig.defaults();

    assertEquals(Dialect.POSTGRESQL, defaults.dialect());
    assertEquals("1.0.0", defaults.formatterVersion());
    assertEquals(List.of("src/main/resources/**/*.xml"), defaults.include());
    assertEquals(List.of(), defaults.exclude());
    assertEquals(4, defaults.indentSize());
    assertEquals(100, defaults.maxLineLength());
    assertEquals("\n", defaults.lineEnding());
    assertEquals(SqlFormatStyle.MULTI_LINE, defaults.sqlFormatStyle());
    assertEquals(SqlPrinter.LEGACY, defaults.sqlPrinter());
    assertEquals(TagWrapStyle.AUTO, defaults.tagWrapStyle());
    assertEquals(AttributeLayout.COMPACT, defaults.attributeLayout());
    assertFalse(defaults.preserveWhitespace());
    assertTrue(defaults.preserveCdata());
    assertFalse(defaults.formatSqlInsideCdata());
    assertTrue(defaults.strict());
    assertEquals(Map.of(), defaults.attributeOrder());
  }
}
