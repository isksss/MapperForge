package io.github.isksss.mapperforge.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** MapperForge が使う SLF4J logger category を集約します。 */
public final class MapperForgeLoggers {
  /** XML parser と SQL / OGNL parser 用 logger です。 */
  public static final Logger PARSER =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.parser");

  /** formatter と printer 用 logger です。 */
  public static final Logger FORMATTER =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.formatter");

  /** validation 用 logger です。 */
  public static final Logger VALIDATOR =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.validator");

  /** Gradle plugin / task 用 logger です。 */
  public static final Logger GRADLE =
      LoggerFactory.getLogger("io.github.isksss.mapperforge.gradle");

  private MapperForgeLoggers() {}
}
