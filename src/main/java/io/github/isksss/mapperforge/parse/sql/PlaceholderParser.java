package io.github.isksss.mapperforge.parse.sql;

import io.github.isksss.mapperforge.ast.sql.PlaceholderExpression;
import io.github.isksss.mapperforge.ast.sql.PlaceholderType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlaceholderParser {
  public PlaceholderExpression parse(String raw) {
    PlaceholderType type = raw.startsWith("#{") ? PlaceholderType.HASH : PlaceholderType.DOLLAR;
    String body = raw.substring(2, raw.length() - 1).strip();
    List<String> parts = splitBody(body);
    String expression = parts.isEmpty() ? "" : parts.getFirst().strip();
    Map<String, String> options = new LinkedHashMap<>();
    for (int i = 1; i < parts.size(); i++) {
      String part = parts.get(i).strip();
      int separator = part.indexOf('=');
      if (separator > 0) {
        String name = part.substring(0, separator).strip();
        String value = unquote(part.substring(separator + 1).strip());
        options.put(name, value);
      }
    }
    return new PlaceholderExpression(type, expression, Map.copyOf(options));
  }

  private List<String> splitBody(String body) {
    java.util.ArrayList<String> parts = new java.util.ArrayList<>();
    StringBuilder current = new StringBuilder();
    char quote = 0;
    for (int i = 0; i < body.length(); i++) {
      char value = body.charAt(i);
      if (quote != 0) {
        current.append(value);
        if (value == quote) {
          quote = 0;
        }
      } else if (value == '\'' || value == '"') {
        quote = value;
        current.append(value);
      } else if (value == ',') {
        parts.add(current.toString());
        current.setLength(0);
      } else {
        current.append(value);
      }
    }
    parts.add(current.toString());
    return List.copyOf(parts);
  }

  private String unquote(String value) {
    if (value.length() >= 2
        && ((value.startsWith("'") && value.endsWith("'"))
            || (value.startsWith("\"") && value.endsWith("\"")))) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }
}
