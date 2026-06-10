package io.github.isksss.mapperforge.config;

import java.util.List;
import java.util.Map;

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
}
