package io.github.isksss.mapperforge.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

final class MapperForgeLoggersTest {
  @Test
  void exposesStableSlf4jCategories() {
    assertEquals("io.github.isksss.mapperforge.parser", MapperForgeLoggers.PARSER.getName());
    assertEquals("io.github.isksss.mapperforge.formatter", MapperForgeLoggers.FORMATTER.getName());
    assertEquals("io.github.isksss.mapperforge.validator", MapperForgeLoggers.VALIDATOR.getName());
    assertEquals("io.github.isksss.mapperforge.gradle", MapperForgeLoggers.GRADLE.getName());
  }

  @Test
  void parserFormatterValidatorAndGradleUseCentralLoggers() throws IOException {
    assertSourceUses("parse/MapperXmlParser.java", "MapperForgeLoggers.PARSER");
    assertSourceUses("format/MapperXmlFormatter.java", "MapperForgeLoggers.FORMATTER");
    assertSourceUses("validation/Validator.java", "MapperForgeLoggers.VALIDATOR");
    assertSourceUses("gradle/MapperForgeTask.java", "MapperForgeLoggers.GRADLE");
  }

  private void assertSourceUses(String relativePath, String expected) throws IOException {
    Path source = Path.of("src/main/java/io/github/isksss/mapperforge").resolve(relativePath);
    assertTrue(Files.readString(source).contains(expected), source + " must use " + expected);
  }
}
