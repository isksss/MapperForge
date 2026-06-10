package io.github.isksss.mapperforge.gradle;

import org.gradle.api.tasks.CacheableTask;

@CacheableTask
public abstract class MapperForgeCheckTask extends MapperForgeTask {
  public MapperForgeCheckTask() {
    getMode().convention(Mode.CHECK);
  }
}
