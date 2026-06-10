package io.github.isksss.mapperforge;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;
import org.junit.jupiter.api.Test;

final class MapperForgeTest {
  private final MapperForge mapperForge = new MapperForge();

  @Test
  void publicFacadeFormatsChecksValidatesAndDiffsMapperXml() {
    FormatterConfig config = FormatterConfig.defaults();
    SourceFile before =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>");

    String formatted = mapperForge.format(before, config);
    SourceFile after = new SourceFile(before.fileName(), formatted);

    assertFalse(mapperForge.isFormatted(before, config));
    assertTrue(mapperForge.isFormatted(after, config));
    assertTrue(mapperForge.validate(before, after, config).success());
    assertTrue(mapperForge.diff(before, after, config).contains("--- UserMapper.xml"));
  }
}
