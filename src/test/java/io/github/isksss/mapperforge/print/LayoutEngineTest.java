package io.github.isksss.mapperforge.print;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.config.FormatterConfig;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class LayoutEngineTest {
  @Test
  void rendersHardLinesWithConfiguredLineEnding() {
    Doc doc = Docs.concat(Docs.text("SELECT"), Docs.hardLine(), Docs.text("1"));

    assertEquals("SELECT\r\n1", LayoutEngine.render(doc, config(4, 100, "\r\n")));
  }

  @Test
  void indentsAfterHardLinesInsideIndentDoc() {
    Doc doc =
        Docs.concat(
            Docs.text("WHERE"), Docs.indent(Docs.concat(Docs.hardLine(), Docs.text("id = #{id}"))));

    assertEquals("WHERE\n  id = #{id}", LayoutEngine.render(doc, config(2, 100, "\n")));
  }

  @Test
  void flattensGroupWhenItFitsMaxLineLength() {
    Doc doc =
        Docs.group(
            Docs.concat(
                Docs.text("SELECT"),
                Docs.line(),
                Docs.text("id, name"),
                Docs.softLine(),
                Docs.text("FROM users")));

    assertEquals("SELECT id, nameFROM users", LayoutEngine.render(doc, config(4, 100, "\n")));
  }

  @Test
  void breaksGroupWhenItExceedsMaxLineLength() {
    Doc doc =
        Docs.group(
            Docs.concat(
                Docs.text("SELECT"),
                Docs.line(),
                Docs.text("id, name"),
                Docs.softLine(),
                Docs.text("FROM users")));

    assertEquals(
        """
        SELECT
        id, name
        FROM users""",
        LayoutEngine.render(doc, config(4, 12, "\n")));
  }

  private static FormatterConfig config(int indentSize, int maxLineLength, String lineEnding) {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        List.of(),
        List.of(),
        indentSize,
        maxLineLength,
        lineEnding,
        defaults.sqlFormatStyle(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        Map.of());
  }
}
