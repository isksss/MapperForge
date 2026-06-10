package io.github.isksss.mapperforge.format;

import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import io.github.isksss.mapperforge.print.LayoutEngine;
import io.github.isksss.mapperforge.print.SqlAstPrinter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SqlFormatter {
  private static final List<String> CLAUSE_KEYWORDS =
      List.of(
          "WITH",
          "SELECT",
          "INSERT INTO",
          "UPDATE",
          "DELETE FROM",
          "FROM",
          "LEFT JOIN",
          "RIGHT JOIN",
          "INNER JOIN",
          "ON",
          "WHERE",
          "GROUP BY",
          "ORDER BY",
          "HAVING",
          "VALUES",
          "SET",
          "RETURNING",
          "LIMIT",
          "OFFSET",
          "UNION",
          "INTERSECT",
          "EXCEPT");
  private static final List<String> INLINE_KEYWORDS =
      List.of("AND", "OR", "IN", "IS", "NULL", "NOT", "LIKE");

  public String format(String sql, FormatterConfig config) {
    String compact = sql.strip().replaceAll("\\s+", " ");
    if (compact.isBlank()) {
      return "";
    }
    String upper = uppercaseKeywords(compact);
    if (config.sqlFormatStyle() == SqlFormatStyle.SINGLE_LINE) {
      return upper;
    }
    String astFormatted = formatWithAstPrinter(compact, config);
    if (!astFormatted.isBlank()) {
      return astFormatted;
    }
    return formatWithRegex(upper, config);
  }

  public String formatLegacy(String sql, FormatterConfig config) {
    String compact = sql.strip().replaceAll("\\s+", " ");
    if (compact.isBlank()) {
      return "";
    }
    String upper = uppercaseKeywords(compact);
    if (config.sqlFormatStyle() == SqlFormatStyle.SINGLE_LINE) {
      return upper;
    }
    return formatWithRegex(upper, config);
  }

  private String formatWithAstPrinter(String sql, FormatterConfig config) {
    var statement = new SqlStatementParser(sql).parse();
    if (statement instanceof UnknownStatement) {
      return "";
    }
    return LayoutEngine.render(new SqlAstPrinter().print(statement), config).strip();
  }

  private String formatWithRegex(String upper, FormatterConfig config) {
    String formatted = upper;
    for (String keyword : CLAUSE_KEYWORDS) {
      formatted = formatted.replaceAll(clausePattern(keyword), "\n" + keyword);
    }
    formatted = formatted.replaceAll("(?i)^" + Pattern.quote("SELECT") + "\\s+", "SELECT\n    ");
    formatted = formatted.replaceAll(",\\s*", ",\n    ");
    if (config.sqlFormatStyle() == SqlFormatStyle.COMPACT) {
      formatted = formatted.replaceAll("SELECT\\R\\s+", "SELECT ");
    }
    return formatted.strip();
  }

  private String uppercaseKeywords(String sql) {
    String result = sql;
    for (String keyword : CLAUSE_KEYWORDS) {
      result =
          result.replaceAll(
              "(?i)\\b" + Pattern.quote(keyword).replace(" ", "\\E\\s+\\Q") + "\\b",
              keyword.toUpperCase(Locale.ROOT));
    }
    for (String keyword : INLINE_KEYWORDS) {
      result = result.replaceAll("(?i)\\b" + keyword + "\\b", keyword);
    }
    return result;
  }

  private String clausePattern(String keyword) {
    return switch (keyword) {
      case "FROM" -> "(?i)(?<!DELETE)\\s+" + Pattern.quote(keyword) + "\\b";
      default -> "(?i)\\s+" + Pattern.quote(keyword) + "\\b";
    };
  }
}
