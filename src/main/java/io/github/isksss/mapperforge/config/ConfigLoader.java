package io.github.isksss.mapperforge.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

public final class ConfigLoader {
  public FormatterConfig load(Path path) {
    FormatterConfig defaults = FormatterConfig.defaults();
    if (!Files.isRegularFile(path)) {
      return defaults;
    }
    try {
      return merge(defaults, readYaml(path));
    } catch (IOException e) {
      throw new ConfigException("Failed to read config: " + path, e);
    }
  }

  private Map<String, Object> readYaml(Path path) throws IOException {
    String content = Files.readString(path, StandardCharsets.UTF_8);
    Load load = new Load(LoadSettings.builder().build());
    Object loaded = load.loadFromString(content);
    if (loaded == null) {
      return Map.of();
    }
    if (!(loaded instanceof Map<?, ?> map)) {
      throw new ConfigException("mapperforge.yml must contain a mapping");
    }
    Map<String, Object> values = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      values.put(String.valueOf(entry.getKey()), entry.getValue());
    }
    return values;
  }

  private FormatterConfig merge(FormatterConfig base, Map<String, Object> yaml) {
    return new FormatterConfig(
        enumValue(Dialect.class, yaml.get("dialect"), base.dialect()),
        stringValue(yaml.get("formatterVersion"), base.formatterVersion()),
        stringList(yaml.get("include"), base.include()),
        stringList(yaml.get("exclude"), base.exclude()),
        intValue(yaml.get("indentSize"), base.indentSize()),
        intValue(yaml.get("maxLineLength"), base.maxLineLength()),
        lineEnding(yaml.get("lineEnding"), base.lineEnding()),
        enumValue(SqlFormatStyle.class, yaml.get("sqlFormatStyle"), base.sqlFormatStyle()),
        enumValue(SqlPrinter.class, yaml.get("sqlPrinter"), base.sqlPrinter()),
        enumValue(TagWrapStyle.class, yaml.get("tagWrapStyle"), base.tagWrapStyle()),
        enumValue(AttributeLayout.class, yaml.get("attributeLayout"), base.attributeLayout()),
        booleanValue(yaml.get("preserveWhitespace"), base.preserveWhitespace()),
        booleanValue(yaml.get("preserveCdata"), base.preserveCdata()),
        booleanValue(yaml.get("formatSqlInsideCdata"), base.formatSqlInsideCdata()),
        booleanValue(yaml.get("strict"), base.strict()),
        attributeOrder(yaml.get("attributeOrder"), base.attributeOrder()));
  }

  private String stringValue(Object value, String fallback) {
    return value == null ? fallback : String.valueOf(value);
  }

  private int intValue(Object value, int fallback) {
    if (value == null) {
      return fallback;
    }
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(String.valueOf(value));
  }

  private boolean booleanValue(Object value, boolean fallback) {
    if (value == null) {
      return fallback;
    }
    if (value instanceof Boolean bool) {
      return bool;
    }
    return Boolean.parseBoolean(String.valueOf(value));
  }

  private String lineEnding(Object value, String fallback) {
    if (value == null) {
      return fallback;
    }
    return switch (String.valueOf(value)) {
      case "LF", "\\n" -> "\n";
      case "CRLF", "\\r\\n" -> "\r\n";
      default -> String.valueOf(value);
    };
  }

  private List<String> stringList(Object value, List<String> fallback) {
    if (value == null) {
      return fallback;
    }
    if (!(value instanceof List<?> list)) {
      throw new ConfigException("Expected list value but got: " + value);
    }
    return list.stream().map(String::valueOf).toList();
  }

  private Map<String, List<String>> attributeOrder(
      Object value, Map<String, List<String>> fallback) {
    if (value == null) {
      return fallback;
    }
    if (!(value instanceof Map<?, ?> map)) {
      throw new ConfigException("attributeOrder must be a mapping");
    }
    Map<String, List<String>> result = new LinkedHashMap<>();
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      result.put(String.valueOf(entry.getKey()), stringList(entry.getValue(), List.of()));
    }
    return Map.copyOf(result);
  }

  private <E extends Enum<E>> E enumValue(Class<E> type, Object value, E fallback) {
    if (value == null) {
      return fallback;
    }
    return Enum.valueOf(type, String.valueOf(value).toUpperCase(Locale.ROOT));
  }

  public static final class ConfigException extends RuntimeException {
    public ConfigException(String message) {
      super(message);
    }

    public ConfigException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
