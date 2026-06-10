package io.github.isksss.mapperforge.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MapperForgeLoggers {
  public static final Logger PARSER =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.parser");
  public static final Logger FORMATTER =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.formatter");
  public static final Logger VALIDATOR =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.validator");
  public static final Logger GRADLE =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.gradle");

  private MapperForgeLoggers() {}
}
