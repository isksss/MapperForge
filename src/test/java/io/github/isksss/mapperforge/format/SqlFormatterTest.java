package io.github.isksss.mapperforge.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class SqlFormatterTest {
  private final SqlFormatter formatter = new SqlFormatter();

  @Test
  void formatsSupportedStatementsThroughAstPrinter() {
    String sql =
        "select u.id, u.name from users u left join orders o on o.user_id = u.id "
            + "where o.total > 0 order by u.id desc limit 10";

    assertEquals(
        """
        SELECT
            u.id,
            u.name
        FROM users u
        LEFT JOIN orders o
            ON o.user_id = u.id
        WHERE o.total > 0
        ORDER BY u.id DESC
        LIMIT 10""",
        formatter.format(sql, config(SqlFormatStyle.MULTI_LINE)));
  }

  @Test
  void keepsSingleLineStyleOneLine() {
    assertEquals(
        "SELECT id FROM users WHERE active = 1",
        formatter.format(
            "select id from users where active = 1", config(SqlFormatStyle.SINGLE_LINE)));
  }

  private static FormatterConfig config(SqlFormatStyle style) {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        style,
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        Map.of());
  }
}
