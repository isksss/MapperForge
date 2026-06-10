package io.github.isksss.mapperforge.gradle;

import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Formats mapper XML files in place.")
public abstract class MapperForgeFormatTask extends MapperForgeTask {
  public MapperForgeFormatTask() {
    getMode().convention(Mode.FORMAT);
  }
}
