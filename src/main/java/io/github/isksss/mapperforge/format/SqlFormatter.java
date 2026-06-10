package io.github.isksss.mapperforge.format;

import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import io.github.isksss.mapperforge.config.SqlPrinter;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import io.github.isksss.mapperforge.print.LayoutEngine;
import io.github.isksss.mapperforge.print.SqlAstPrinter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Mapper XML 内の SQL text を整形する formatter です。
 *
 * <p>設定に応じて AST printer または legacy regex formatter を使います。AST printer が未対応 SQL を返した場合は、legacy
 * formatter に fallback します。
 */
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

  /** SQL formatter を作成します。 */
  public SqlFormatter() {}

  /**
   * SQL text を設定に従って整形します。
   *
   * @param sql 整形対象 SQL
   * @param config formatter 設定
   * @return 整形後 SQL。空白のみの SQL は空文字
   */
  public String format(String sql, FormatterConfig config) {
    String compact = compactSql(sql);
    if (compact.isBlank()) {
      return "";
    }
    String upper = uppercaseKeywords(compact);
    if (config.sqlFormatStyle() == SqlFormatStyle.SINGLE_LINE) {
      return upper;
    }
    if (config.sqlPrinter() == SqlPrinter.AST) {
      String astFormatted = formatWithAstPrinter(compact, config);
      if (!astFormatted.isBlank()) {
        return astFormatted;
      }
    }
    return formatWithRegex(upper, config);
  }

  /**
   * legacy regex formatter だけで SQL text を整形します。
   *
   * @param sql 整形対象 SQL
   * @param config formatter 設定
   * @return 整形後 SQL。空白のみの SQL は空文字
   */
  public String formatLegacy(String sql, FormatterConfig config) {
    String compact = compactSql(sql);
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
    ProtectedSql protectedSql = protectNonCodeSegments(upper);
    String formatted = protectedSql.sql();
    for (String keyword : CLAUSE_KEYWORDS) {
      formatted = formatted.replaceAll(clausePattern(keyword), "\n" + keyword);
    }
    formatted = formatted.replaceAll("(?i)^" + Pattern.quote("SELECT") + "\\s+", "SELECT\n    ");
    formatted = formatted.replaceAll(",\\s*", ",\n    ");
    if (config.sqlFormatStyle() == SqlFormatStyle.COMPACT) {
      formatted = formatted.replaceAll("SELECT\\R\\s+", "SELECT ");
    }
    return protectedSql.restore(formatted).strip();
  }

  private String uppercaseKeywords(String sql) {
    ProtectedSql protectedSql = protectNonCodeSegments(sql);
    String result = protectedSql.sql();
    for (String keyword : CLAUSE_KEYWORDS) {
      result =
          result.replaceAll(
              "(?i)\\b" + Pattern.quote(keyword).replace(" ", "\\E\\s+\\Q") + "\\b",
              keyword.toUpperCase(Locale.ROOT));
    }
    for (String keyword : INLINE_KEYWORDS) {
      result = result.replaceAll("(?i)\\b" + keyword + "\\b", keyword);
    }
    return protectedSql.restore(result);
  }

  private String compactSql(String sql) {
    String stripped = sql.strip();
    StringBuilder compact = new StringBuilder();
    boolean pendingSpace = false;
    for (int i = 0; i < stripped.length(); i++) {
      char current = stripped.charAt(i);
      if (Character.isWhitespace(current)) {
        pendingSpace = compact.length() > 0 && compact.charAt(compact.length() - 1) != '\n';
        continue;
      }
      if (pendingSpace) {
        compact.append(' ');
        pendingSpace = false;
      }
      if (current == '\'') {
        i = appendString(stripped, i, compact);
      } else if (startsWith(stripped, i, "--")) {
        i = appendLineComment(stripped, i, compact);
        pendingSpace = false;
      } else if (startsWith(stripped, i, "/*")) {
        i = appendBlockComment(stripped, i, compact);
      } else {
        compact.append(current);
      }
    }
    return compact.toString().strip();
  }

  private ProtectedSql protectNonCodeSegments(String sql) {
    StringBuilder protectedText = new StringBuilder();
    List<String> segments = new java.util.ArrayList<>();
    for (int i = 0; i < sql.length(); i++) {
      char current = sql.charAt(i);
      if (current == '\'') {
        StringBuilder segment = new StringBuilder();
        i = appendString(sql, i, segment);
        appendPlaceholder(protectedText, segments, segment.toString());
      } else if (startsWith(sql, i, "--")) {
        StringBuilder segment = new StringBuilder();
        i = appendLineComment(sql, i, segment);
        String comment = segment.toString();
        if (comment.endsWith("\n")) {
          appendPlaceholder(protectedText, segments, comment.substring(0, comment.length() - 1));
          protectedText.append('\n');
        } else {
          appendPlaceholder(protectedText, segments, comment);
        }
      } else if (startsWith(sql, i, "/*")) {
        StringBuilder segment = new StringBuilder();
        i = appendBlockComment(sql, i, segment);
        appendPlaceholder(protectedText, segments, segment.toString());
      } else {
        protectedText.append(current);
      }
    }
    return new ProtectedSql(protectedText.toString(), List.copyOf(segments));
  }

  private void appendPlaceholder(StringBuilder builder, List<String> segments, String segment) {
    builder.append("__MAPPERFORGE_SEGMENT_").append(segments.size()).append("__");
    segments.add(segment);
  }

  private int appendString(String sql, int start, StringBuilder builder) {
    int i = start;
    builder.append(sql.charAt(i++));
    while (i < sql.length()) {
      char current = sql.charAt(i++);
      builder.append(current);
      if (current == '\\' && i < sql.length()) {
        builder.append(sql.charAt(i++));
      } else if (current == '\'') {
        break;
      }
    }
    return i - 1;
  }

  private int appendLineComment(String sql, int start, StringBuilder builder) {
    int i = start;
    while (i < sql.length() && sql.charAt(i) != '\n' && sql.charAt(i) != '\r') {
      builder.append(sql.charAt(i++));
    }
    if (i < sql.length()) {
      builder.append('\n');
      if (sql.charAt(i) == '\r' && i + 1 < sql.length() && sql.charAt(i + 1) == '\n') {
        i++;
      }
    }
    return i;
  }

  private int appendBlockComment(String sql, int start, StringBuilder builder) {
    int i = start;
    while (i < sql.length() && !startsWith(sql, i, "*/")) {
      builder.append(sql.charAt(i++));
    }
    if (i < sql.length()) {
      builder.append("*/");
      i++;
    }
    return i;
  }

  private boolean startsWith(String value, int index, String prefix) {
    return value.startsWith(prefix, index);
  }

  private String clausePattern(String keyword) {
    return switch (keyword) {
      case "FROM" -> "(?i)(?<!DELETE)\\s+" + Pattern.quote(keyword) + "\\b";
      default -> "(?i)\\s+" + Pattern.quote(keyword) + "\\b";
    };
  }

  private record ProtectedSql(String sql, List<String> segments) {
    private String restore(String value) {
      String restored = value;
      for (int i = 0; i < segments.size(); i++) {
        restored = restored.replace("__MAPPERFORGE_SEGMENT_" + i + "__", segments.get(i));
      }
      return restored;
    }
  }
}
