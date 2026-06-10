package io.github.isksss.mapperforge.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class GoldenFileTest {
  private final MapperForge mapperForge = new MapperForge();

  @Test
  void formatsDynamicSql() throws IOException {
    assertGolden("dynamic-sql", FormatterConfig.defaults());
  }

  @Test
  void preservesLosslessNodes() throws IOException {
    FormatterConfig config = FormatterConfig.defaults();
    String before = resource("golden/lossless/before.xml");
    String formatted = mapperForge.format(new SourceFile("lossless.xml", before), config);

    assertEquals(resource("golden/lossless/after.xml"), formatted);
    assertTrue(
        mapperForge
            .validate(
                new SourceFile("lossless-before.xml", before),
                new SourceFile("lossless-after.xml", formatted),
                config)
            .success());
  }

  @Test
  void appliesConfiguredAttributeOrder() throws IOException {
    FormatterConfig config =
        new FormatterConfig(
            FormatterConfig.defaults().dialect(),
            FormatterConfig.defaults().formatterVersion(),
            FormatterConfig.defaults().include(),
            FormatterConfig.defaults().exclude(),
            FormatterConfig.defaults().indentSize(),
            FormatterConfig.defaults().maxLineLength(),
            FormatterConfig.defaults().lineEnding(),
            FormatterConfig.defaults().sqlFormatStyle(),
            FormatterConfig.defaults().tagWrapStyle(),
            FormatterConfig.defaults().attributeLayout(),
            FormatterConfig.defaults().preserveWhitespace(),
            FormatterConfig.defaults().preserveCdata(),
            FormatterConfig.defaults().formatSqlInsideCdata(),
            FormatterConfig.defaults().strict(),
            Map.of("result", List.of("property", "column", "javaType", "jdbcType")));

    assertGolden("attribute-order", config);
  }

  @Test
  void formatIsIdempotent() throws IOException {
    FormatterConfig config = FormatterConfig.defaults();
    String after = resource("golden/dynamic-sql/after.xml");

    assertEquals(after, mapperForge.format(new SourceFile("after.xml", after), config));
  }

  private void assertGolden(String name, FormatterConfig config) throws IOException {
    String before = resource("golden/" + name + "/before.xml");
    String after = resource("golden/" + name + "/after.xml");

    assertEquals(after, mapperForge.format(new SourceFile(name + ".xml", before), config));
  }

  private String resource(String name) throws IOException {
    try (var stream = getClass().getClassLoader().getResourceAsStream(name)) {
      if (stream == null) {
        throw new IOException("Missing test resource: " + name);
      }
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
