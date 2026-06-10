package io.github.isksss.mapperforge.config;

import java.util.List;
import java.util.Map;

/**
 * MapperForge formatter の全設定を保持する immutable config です。
 *
 * @param dialect 対象 SQL dialect
 * @param formatterVersion formatter の SemVer バージョン
 * @param include 処理対象 file の include glob
 * @param exclude 処理対象から除外する exclude glob
 * @param indentSize インデント幅
 * @param maxLineLength 最大行長
 * @param lineEnding 出力改行コード
 * @param sqlFormatStyle SQL formatter の出力スタイル
 * @param sqlPrinter SQL printer 実装
 * @param tagWrapStyle XML tag の折り返し方針
 * @param attributeLayout XML attribute の配置方針
 * @param preserveWhitespace 空白 node を意味検証対象として保持するか
 * @param preserveCdata CDATA wrapper を保持するか
 * @param formatSqlInsideCdata CDATA 内 SQL を整形するか
 * @param strict 検証失敗時に task を失敗させるか
 * @param attributeOrder tag ごとの attribute 順序設定
 */
public record FormatterConfig(
    Dialect dialect,
    String formatterVersion,
    List<String> include,
    List<String> exclude,
    int indentSize,
    int maxLineLength,
    String lineEnding,
    SqlFormatStyle sqlFormatStyle,
    SqlPrinter sqlPrinter,
    TagWrapStyle tagWrapStyle,
    AttributeLayout attributeLayout,
    boolean preserveWhitespace,
    boolean preserveCdata,
    boolean formatSqlInsideCdata,
    boolean strict,
    Map<String, List<String>> attributeOrder) {
  /** 設定値を検証し、表記揺れを正規化します。 */
  public FormatterConfig {
    if (!isSemVer(formatterVersion)) {
      throw new IllegalArgumentException("formatterVersion must be SemVer: " + formatterVersion);
    }
    if (indentSize < 0) {
      throw new IllegalArgumentException("indentSize must be zero or greater: " + indentSize);
    }
    if (maxLineLength <= 0) {
      throw new IllegalArgumentException(
          "maxLineLength must be greater than zero: " + maxLineLength);
    }
    lineEnding = normalizeLineEnding(lineEnding);
  }

  /**
   * PLAN v1 の default formatter 設定を返します。
   *
   * @return default formatter 設定
   */
  public static FormatterConfig defaults() {
    return new FormatterConfig(
        Dialect.POSTGRESQL,
        "1.0.0",
        List.of("src/main/resources/**/*.xml"),
        List.of(),
        4,
        100,
        "\n",
        SqlFormatStyle.MULTI_LINE,
        SqlPrinter.LEGACY,
        TagWrapStyle.AUTO,
        AttributeLayout.COMPACT,
        false,
        true,
        false,
        true,
        Map.of());
  }

  private static boolean isSemVer(String value) {
    if (value == null) {
      return false;
    }
    return value.matches(
        "(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)"
            + "(-[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?"
            + "(\\+[0-9A-Za-z-]+(\\.[0-9A-Za-z-]+)*)?");
  }

  private static String normalizeLineEnding(String value) {
    return switch (value) {
      case "LF", "\\n" -> "\n";
      case "CRLF", "\\r\\n" -> "\r\n";
      case "\n", "\r\n" -> value;
      default -> throw new IllegalArgumentException("lineEnding must be LF or CRLF: " + value);
    };
  }
}
