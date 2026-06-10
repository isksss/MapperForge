package io.github.isksss.mapperforge.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import io.github.isksss.mapperforge.config.SqlPrinter;
import io.github.isksss.mapperforge.error.ErrorCode;
import io.github.isksss.mapperforge.source.SourceFile;
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
        formatter.format(sql, config(SqlFormatStyle.MULTI_LINE, SqlPrinter.AST)));
  }

  @Test
  void keepsSingleLineStyleOneLine() {
    assertEquals(
        "SELECT id FROM users WHERE active = 1",
        formatter.format(
            "select id from users where active = 1",
            config(SqlFormatStyle.SINGLE_LINE, SqlPrinter.AST)));
  }

  @Test
  void blankSqlFormatsToEmptyString() {
    assertEquals("", formatter.format(" \n\t ", config(SqlFormatStyle.MULTI_LINE, SqlPrinter.AST)));
    assertEquals(
        "", formatter.formatLegacy(" \n\t ", config(SqlFormatStyle.MULTI_LINE, SqlPrinter.AST)));
  }

  @Test
  void astPrinterFallsBackToLegacyFormatterForUnsupportedSql() {
    assertEquals(
        """
        merge into users using source
        ON users.id = source.id""",
        formatter.format(
            "merge into users using source on users.id = source.id",
            config(SqlFormatStyle.MULTI_LINE, SqlPrinter.AST)));
  }

  @Test
  void formatLegacyDoesNotUseAstPrinter() {
    assertEquals(
        """
        SELECT
            id
        FROM users""",
        formatter.formatLegacy(
            "select id from users", config(SqlFormatStyle.MULTI_LINE, SqlPrinter.AST)));
  }

  @Test
  void preservesSqlCommentsWithoutFormattingTheirContent() {
    String sql =
        """
        select id, name -- keep from text
        from users
        where active = 1 /* keep order by text */
        """;

    assertEquals(
        """
        SELECT
            id,
            name -- keep from text
        FROM users
        WHERE active = 1 /* keep order by text */""",
        formatter.format(sql, config(SqlFormatStyle.MULTI_LINE, SqlPrinter.LEGACY)));
  }

  @Test
  void convertsCdataToEscapedTextWhenCdataIsNotPreserved() {
    String before =
        "<mapper namespace=\"sample\"><select id=\"find\"><![CDATA[select id from users where age < #{age} and flags & #{mask}]]></select></mapper>";

    assertEquals(
        """
        <mapper namespace="sample">
            <select id="find">
                SELECT
                    id
                FROM users
                WHERE age &lt; #{age} AND flags &amp; #{mask}
            </select>
        </mapper>
        """,
        new MapperForge()
            .format(new SourceFile("UserMapper.xml", before), cdataNotPreservedConfig()));
  }

  @Test
  void formatterExceptionCarriesFormatErrorCode() {
    MapperXmlFormatter.FormatterException error =
        assertThrows(
            MapperXmlFormatter.FormatterException.class,
            () ->
                new MapperForge()
                    .format(new SourceFile("broken.xml", "<mapper>"), FormatterConfig.defaults()));

    assertEquals(ErrorCode.FORMAT_ERROR, error.code());
  }

  private static FormatterConfig config(SqlFormatStyle style, SqlPrinter printer) {
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
        printer,
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        Map.of());
  }

  private static FormatterConfig cdataNotPreservedConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        defaults.sqlPrinter(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        false,
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        defaults.attributeOrder());
  }
}
