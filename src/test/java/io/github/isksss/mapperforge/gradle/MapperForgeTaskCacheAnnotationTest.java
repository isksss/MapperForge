package io.github.isksss.mapperforge.gradle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.gradle.api.tasks.CacheableTask;
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
}
