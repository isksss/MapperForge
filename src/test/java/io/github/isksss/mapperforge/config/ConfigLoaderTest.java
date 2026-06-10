package io.github.isksss.mapperforge.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.isksss.mapperforge.config.ConfigLoader.ConfigException;
import io.github.isksss.mapperforge.error.ErrorCode;
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
        sqlPrinter: AST
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
    assertEquals(SqlPrinter.AST, loaded.sqlPrinter());
    assertEquals(List.of("property", "column"), loaded.attributeOrder().get("result"));
  }

  @Test
  void returnsDefaultsWhenYamlDoesNotExist() {
    FormatterConfig loaded = new ConfigLoader().load(tempDir.resolve("missing.yml"));

    assertEquals(FormatterConfig.defaults(), loaded);
  }

  @Test
  void rejectsInvalidFormatterVersion() throws IOException {
    Path config = tempDir.resolve("mapperforge.yml");
    Files.writeString(config, "formatterVersion: latest\n", StandardCharsets.UTF_8);

    ConfigException error =
        assertThrows(ConfigException.class, () -> new ConfigLoader().load(config));

    assertEquals(ErrorCode.CONFIG_ERROR, error.code());
    assertEquals("formatterVersion must be SemVer: latest", error.getMessage());
  }

  @Test
  void rejectsInvalidIndentSize() throws IOException {
    Path config = tempDir.resolve("mapperforge.yml");
    Files.writeString(config, "indentSize: -1\n", StandardCharsets.UTF_8);

    ConfigException error =
        assertThrows(ConfigException.class, () -> new ConfigLoader().load(config));

    assertEquals(ErrorCode.CONFIG_ERROR, error.code());
    assertEquals("indentSize must be zero or greater: -1", error.getMessage());
  }

  @Test
  void rejectsInvalidMaxLineLength() throws IOException {
    Path config = tempDir.resolve("mapperforge.yml");
    Files.writeString(config, "maxLineLength: 0\n", StandardCharsets.UTF_8);

    ConfigException error =
        assertThrows(ConfigException.class, () -> new ConfigLoader().load(config));

    assertEquals(ErrorCode.CONFIG_ERROR, error.code());
    assertEquals("maxLineLength must be greater than zero: 0", error.getMessage());
  }

  @Test
  void rejectsInvalidLineEnding() throws IOException {
    Path config = tempDir.resolve("mapperforge.yml");
    Files.writeString(config, "lineEnding: CR\n", StandardCharsets.UTF_8);

    ConfigException error =
        assertThrows(ConfigException.class, () -> new ConfigLoader().load(config));

    assertEquals(ErrorCode.CONFIG_ERROR, error.code());
    assertEquals("lineEnding must be LF or CRLF: CR", error.getMessage());
  }

  @Test
  void normalizesLineEndingAliases() {
    FormatterConfig crlf =
        new FormatterConfig(
            FormatterConfig.defaults().dialect(),
            FormatterConfig.defaults().formatterVersion(),
            FormatterConfig.defaults().include(),
            FormatterConfig.defaults().exclude(),
            FormatterConfig.defaults().indentSize(),
            FormatterConfig.defaults().maxLineLength(),
            "CRLF",
            FormatterConfig.defaults().sqlFormatStyle(),
            FormatterConfig.defaults().sqlPrinter(),
            FormatterConfig.defaults().tagWrapStyle(),
            FormatterConfig.defaults().attributeLayout(),
            FormatterConfig.defaults().preserveWhitespace(),
            FormatterConfig.defaults().preserveCdata(),
            FormatterConfig.defaults().formatSqlInsideCdata(),
            FormatterConfig.defaults().strict(),
            FormatterConfig.defaults().attributeOrder());

    assertEquals("\r\n", crlf.lineEnding());
  }
}
