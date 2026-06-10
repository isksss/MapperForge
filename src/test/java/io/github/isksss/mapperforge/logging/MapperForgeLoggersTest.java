package io.github.isksss.mapperforge.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class MapperForgeLoggersTest {
  @Test
  void exposesStableSlf4jCategories() {
    assertEquals("io.github.isksss.mapperforge.parser", MapperForgeLoggers.PARSER.getName());
    assertEquals("io.github.isksss.mapperforge.formatter", MapperForgeLoggers.FORMATTER.getName());
    assertEquals("io.github.isksss.mapperforge.validator", MapperForgeLoggers.VALIDATOR.getName());
    assertEquals("io.github.isksss.mapperforge.gradle", MapperForgeLoggers.GRADLE.getName());
  }
}
