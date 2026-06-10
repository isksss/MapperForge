package io.github.isksss.mapperforge.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class ConfigLoaderTest {
  @TempDir Path tempDir;

  @Test
  void loadsMapperForgeYamlOverDefaults() throws IOException {
    Path config = tempDir.resolve("mapperforge.yml");
    Files.writeString(
        config,
        """
        dialect: MYSQL
        include:
          - custom/**/*.xml
        indentSize: 2
        sqlFormatStyle: SINGLE_LINE
        attributeOrder:
          result:
            - property
            - column
        """,
        StandardCharsets.UTF_8);

    FormatterConfig loaded = new ConfigLoader().load(config);

    assertEquals(Dialect.MYSQL, loaded.dialect());
    assertEquals(List.of("custom/**/*.xml"), loaded.include());
    assertEquals(2, loaded.indentSize());
    assertEquals(SqlFormatStyle.SINGLE_LINE, loaded.sqlFormatStyle());
    assertEquals(List.of("property", "column"), loaded.attributeOrder().get("result"));
  }

  @Test
  void returnsDefaultsWhenYamlDoesNotExist() {
    FormatterConfig loaded = new ConfigLoader().load(tempDir.resolve("missing.yml"));

    assertEquals(FormatterConfig.defaults(), loaded);
  }
}
