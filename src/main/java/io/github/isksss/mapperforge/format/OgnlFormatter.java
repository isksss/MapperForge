package io.github.isksss.mapperforge.format;

import java.util.Set;

public final class OgnlFormatter {
  private static final Set<String> WORD_OPERATORS = Set.of("and", "or", "in", "instanceof");
  private static final Set<String> PREFIX_WORD_OPERATORS = Set.of("not");

  public String format(String expression) {
    return join(tokenize(expression));
  }

  private java.util.List<String> tokenize(String expression) {
    java.util.List<String> tokens = new java.util.ArrayList<>();
    int index = 0;
    while (index < expression.length()) {
      char current = expression.charAt(index);
      if (Character.isWhitespace(current)) {
        index++;
      } else if (current == '\'' || current == '"') {
        int end = stringEnd(expression, index);
        tokens.add(expression.substring(index, end));
        index = end;
      } else if (startsWithAny(expression, index, "&&", "||", "==", "!=", ">=", "<=")) {
        tokens.add(expression.substring(index, index + 2));
        index += 2;
      } else if (current == '>' || current == '<') {
        tokens.add(String.valueOf(current));
        index++;
      } else {
        int end = bareTokenEnd(expression, index);
        tokens.add(expression.substring(index, end));
        index = end;
      }
    }
    return tokens;
  }

  private String join(java.util.List<String> tokens) {
    StringBuilder out = new StringBuilder();
    for (String token : tokens) {
      if (out.isEmpty()) {
        out.append(token);
      } else if (needsSpaceBefore(token) || needsSpaceAfter(previousToken(out))) {
        out.append(' ').append(token);
      } else {
        out.append(token);
      }
    }
    return out.toString();
  }

  private boolean needsSpaceBefore(String token) {
    return isSymbolOperator(token) || WORD_OPERATORS.contains(token);
  }

  private boolean needsSpaceAfter(String token) {
    return isSymbolOperator(token)
        || WORD_OPERATORS.contains(token)
        || PREFIX_WORD_OPERATORS.contains(token);
  }

  private boolean isSymbolOperator(String token) {
    return switch (token) {
      case "&&", "||", "==", "!=", ">=", "<=", ">", "<" -> true;
      default -> false;
    };
  }

  private String previousToken(StringBuilder out) {
    int end = out.length();
    int start = end - 1;
    while (start >= 0 && out.charAt(start) != ' ') {
      start--;
    }
    return out.substring(start + 1, end);
  }

  private int stringEnd(String expression, int start) {
    char quote = expression.charAt(start);
    int index = start + 1;
    while (index < expression.length()) {
      char current = expression.charAt(index);
      if (current == '\\') {
        index += 2;
      } else if (current == quote) {
        return index + 1;
      } else {
        index++;
      }
    }
    return expression.length();
  }

  private int bareTokenEnd(String expression, int start) {
    int index = start;
    while (index < expression.length()) {
      char current = expression.charAt(index);
      if (Character.isWhitespace(current)
          || current == '\''
          || current == '"'
          || current == '>'
          || current == '<'
          || startsWithAny(expression, index, "&&", "||", "==", "!=", ">=", "<=")) {
        break;
      }
      index++;
    }
    return index;
  }

  private boolean startsWithAny(String expression, int index, String... candidates) {
    for (String candidate : candidates) {
      if (expression.startsWith(candidate, index)) {
        return true;
      }
    }
    return false;
  }
}
