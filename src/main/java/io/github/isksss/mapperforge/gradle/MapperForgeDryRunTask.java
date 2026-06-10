package io.github.isksss.mapperforge.gradle;

import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Dry-run prints diffs to standard output.")
public abstract class MapperForgeDryRunTask extends MapperForgeTask {
  public MapperForgeDryRunTask() {
    getMode().convention(Mode.DRY_RUN);
    getOutputs().upToDateWhen(task -> false);
  }
}
