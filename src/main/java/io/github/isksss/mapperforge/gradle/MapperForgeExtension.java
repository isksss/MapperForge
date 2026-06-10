package io.github.isksss.mapperforge.gradle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

public abstract class MapperForgeExtension {
  public abstract Property<String> getDialect();

  public abstract Property<String> getFormatterVersion();

  public abstract ListProperty<String> getInclude();

  public abstract ListProperty<String> getExclude();

  public abstract Property<Integer> getIndentSize();

  public abstract Property<Integer> getMaxLineLength();

  public abstract Property<String> getLineEnding();

  public abstract Property<String> getSqlFormatStyle();

  public abstract Property<String> getTagWrapStyle();

  public abstract Property<String> getAttributeLayout();

  public abstract Property<Boolean> getPreserveWhitespace();

  public abstract Property<Boolean> getPreserveCdata();

  public abstract Property<Boolean> getFormatSqlInsideCdata();

  public abstract Property<Boolean> getStrict();

  public abstract MapProperty<String, List<String>> getAttributeOrder();

  public MapperForgeExtension(ObjectFactory objects) {
    getDialect().convention("POSTGRESQL");
    getFormatterVersion().convention("1.0.0");
    getInclude().convention(List.of("src/main/resources/**/*.xml"));
    getExclude().convention(List.of());
    getIndentSize().convention(4);
    getMaxLineLength().convention(100);
    getLineEnding().convention("\n");
    getSqlFormatStyle().convention("MULTI_LINE");
    getTagWrapStyle().convention("AUTO");
    getAttributeLayout().convention("COMPACT");
    getPreserveWhitespace().convention(false);
    getPreserveCdata().convention(true);
    getFormatSqlInsideCdata().convention(false);
    getStrict().convention(true);
    getAttributeOrder().convention(new LinkedHashMap<>());
  }

  public void attributeOrder(String tagName, List<String> order) {
    Map<String, List<String>> current = new LinkedHashMap<>(getAttributeOrder().get());
    current.put(tagName, List.copyOf(order));
    getAttributeOrder().set(current);
  }
}
