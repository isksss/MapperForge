package io.github.isksss.mapperforge.format;

import java.util.regex.Pattern;

public final class OgnlFormatter {
  private static final Pattern WORD_OPERATORS = Pattern.compile("\\s+(and|or|in|instanceof)\\s+");

  public String format(String expression) {
    String result = expression.strip();
    result = result.replaceAll("\\s*(&&|\\|\\||==|!=|>=|<=|>|<)\\s*", " $1 ");
    result = result.replaceAll("\\s+(and|or|in|instanceof)\\s+", " $1 ");
    result = result.replaceAll("\\bnot\\s+", "not ");
    result = result.replaceAll("\\s+", " ");
    return WORD_OPERATORS.matcher(result).replaceAll(" $1 ");
  }
}
