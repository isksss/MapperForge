package io.github.isksss.mapperforge.gradle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.work.DisableCachingByDefault;
import org.junit.jupiter.api.Test;

final class MapperForgeTaskCacheAnnotationTest {
  @Test
  void checkTaskIsCacheable() {
    assertNotNull(MapperForgeCheckTask.class.getAnnotation(CacheableTask.class));
  }

  @Test
  void formatAndDryRunTasksRemainNonCacheable() {
    assertNotNull(MapperForgeFormatTask.class.getAnnotation(DisableCachingByDefault.class));
    assertNotNull(MapperForgeDryRunTask.class.getAnnotation(DisableCachingByDefault.class));
  }

  @Test
  void formatterConfigurationParticipatesInTaskInputs() throws NoSuchMethodException {
    for (String methodName :
        List.of(
            "getMode",
            "getDialect",
            "getFormatterVersion",
            "getInclude",
            "getExclude",
            "getIndentSize",
            "getMaxLineLength",
            "getLineEnding",
            "getSqlFormatStyle",
            "getSqlPrinter",
            "getTagWrapStyle",
            "getAttributeLayout",
            "getPreserveWhitespace",
            "getPreserveCdata",
            "getFormatSqlInsideCdata",
            "getStrict",
            "getAttributeOrder")) {
      assertNotNull(MapperForgeTask.class.getMethod(methodName).getAnnotation(Input.class));
    }
  }

  @Test
  void sourceFilesAndStateFileParticipateInTaskTracking() throws NoSuchMethodException {
    assertNotNull(
        MapperForgeTask.class.getMethod("getSourceFiles").getAnnotation(InputFiles.class));
    assertNotNull(MapperForgeTask.class.getMethod("getStateFile").getAnnotation(OutputFile.class));
  }
}
