package io.github.isksss.mapperforge.format;

import com.ctc.wstx.stax.WstxInputFactory;
import io.github.isksss.mapperforge.config.AttributeLayout;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.TagWrapStyle;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class MapperXmlFormatter {
  private static final Set<String> SQL_TEXT_TAGS =
      Set.of(
          "select",
          "insert",
          "update",
          "delete",
          "sql",
          "if",
          "when",
          "otherwise",
          "foreach",
          "trim",
          "where",
          "set");
  private static final Pattern DOCTYPE_PATTERN = Pattern.compile("<!DOCTYPE\\s+[^>]+>");

  private final OgnlFormatter ognlFormatter = new OgnlFormatter();
  private final SqlFormatter sqlFormatter = new SqlFormatter();

  public String format(SourceFile source, FormatterConfig config) {
    XMLInputFactory factory = new WstxInputFactory();
    try {
      XMLStreamReader reader =
          factory.createXMLStreamReader(source.fileName(), new StringReader(source.content()));
      StringBuilder out = new StringBuilder(source.content().length() + 128);
      extractDoctype(source.content()).ifPresent(out::append);
      ArrayDeque<String> stack = new ArrayDeque<>();
      boolean rootSeen = false;
      boolean lastWasStart = false;

      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
          case XMLStreamConstants.START_ELEMENT -> {
            if (!rootSeen) {
              if (!out.isEmpty()) {
                newline(out, stack.size(), config);
              }
              rootSeen = true;
            } else {
              newline(out, stack.size(), config);
            }
            appendStartElement(out, reader, stack.size(), config);
            stack.push(reader.getLocalName());
            lastWasStart = true;
          }
          case XMLStreamConstants.CHARACTERS, XMLStreamConstants.SPACE -> {
            String text = reader.getText();
            if (!text.isBlank()) {
              appendText(out, text, stack.peek(), stack.size(), config);
              lastWasStart = false;
            } else if (config.preserveWhitespace()) {
              out.append(text);
            }
          }
          case XMLStreamConstants.CDATA -> {
            appendCdata(out, reader.getText(), stack.size(), config);
            lastWasStart = false;
          }
          case XMLStreamConstants.COMMENT -> {
            newline(out, stack.size(), config);
            out.append("<!--").append(reader.getText()).append("-->");
            lastWasStart = false;
          }
          case XMLStreamConstants.DTD -> {
            lastWasStart = false;
          }
          case XMLStreamConstants.END_ELEMENT -> {
            String name = stack.pop();
            if (!lastWasStart) {
              newline(out, stack.size(), config);
            }
            out.append("</").append(name).append(">");
            lastWasStart = false;
          }
          default -> {}
        }
      }
      return trimTrailingBlankLines(out.toString()) + config.lineEnding();
    } catch (XMLStreamException e) {
      throw new FormatterException("Failed to parse mapper XML: " + source.fileName(), e);
    }
  }

  private void appendStartElement(
      StringBuilder out, XMLStreamReader reader, int depth, FormatterConfig config) {
    String tagName = reader.getLocalName();
    out.append("<").append(tagName);
    List<XmlAttribute> attributes = attributes(reader, tagName, config);
    boolean onePerLine =
        config.attributeLayout() == AttributeLayout.ONE_PER_LINE
            || config.tagWrapStyle() == TagWrapStyle.ALWAYS
            || (config.tagWrapStyle() == TagWrapStyle.AUTO
                && estimatedStartLength(tagName, attributes) > config.maxLineLength());
    for (XmlAttribute attribute : attributes) {
      if (onePerLine) {
        newline(out, depth + 1, config);
      } else {
        out.append(" ");
      }
      out.append(attribute.name())
          .append("=\"")
          .append(escapeAttribute(attribute.value()))
          .append("\"");
    }
    out.append(">");
  }

  private List<XmlAttribute> attributes(
      XMLStreamReader reader, String tagName, FormatterConfig config) {
    List<XmlAttribute> attributes = new ArrayList<>();
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String name = reader.getAttributeLocalName(i);
      String value = reader.getAttributeValue(i);
      if ("test".equals(name)) {
        value = ognlFormatter.format(value);
      }
      attributes.add(new XmlAttribute(name, value));
    }
    List<String> order = config.attributeOrder().get(tagName);
    if (order != null && !order.isEmpty()) {
      attributes.sort(
          Comparator.comparingInt(
              attribute -> {
                int index = order.indexOf(attribute.name());
                return index >= 0 ? index : Integer.MAX_VALUE;
              }));
    }
    return attributes;
  }

  private int estimatedStartLength(String tagName, List<XmlAttribute> attributes) {
    int length = tagName.length() + 2;
    for (XmlAttribute attribute : attributes) {
      length += attribute.name().length() + attribute.value().length() + 4;
    }
    return length;
  }

  private void appendText(
      StringBuilder out, String text, String parentTag, int depth, FormatterConfig config) {
    String formatted =
        SQL_TEXT_TAGS.contains(parentTag) ? sqlFormatter.format(text, config) : text.strip();
    if (formatted.isBlank()) {
      return;
    }
    for (String line : formatted.split("\\R")) {
      if (line.isBlank()) {
        continue;
      }
      newline(out, depth, config);
      out.append(line.stripTrailing());
    }
  }

  private void appendCdata(StringBuilder out, String raw, int depth, FormatterConfig config) {
    newline(out, depth, config);
    String text = config.formatSqlInsideCdata() ? sqlFormatter.format(raw, config) : raw.strip();
    out.append("<![CDATA[").append(text).append("]]>");
  }

  private java.util.Optional<String> extractDoctype(String content) {
    var matcher = DOCTYPE_PATTERN.matcher(content);
    if (matcher.find()) {
      return java.util.Optional.of(matcher.group().strip());
    }
    return java.util.Optional.empty();
  }

  private void newline(StringBuilder out, int depth, FormatterConfig config) {
    if (!out.isEmpty()) {
      out.append(config.lineEnding());
    }
    out.append(" ".repeat(depth * config.indentSize()));
  }

  private String trimTrailingBlankLines(String value) {
    return value.stripTrailing();
  }

  private String escapeAttribute(String value) {
    return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
  }

  private record XmlAttribute(String name, String value) {}

  public static final class FormatterException extends RuntimeException {
    public FormatterException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
