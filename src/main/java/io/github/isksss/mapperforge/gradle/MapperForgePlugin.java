package io.github.isksss.mapperforge.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class MapperForgePlugin implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    MapperForgeExtension extension =
        project.getExtensions().create("mapperForge", MapperForgeExtension.class);

    project
        .getTasks()
        .register(
            "mapperForgeFormat",
            MapperForgeTask.class,
            task -> {
              task.setGroup("formatting");
              task.setDescription("Formats MyBatis Mapper XML files.");
              task.getMode().set(MapperForgeTask.Mode.FORMAT);
              task.configureFrom(project, extension);
            });

    project
        .getTasks()
        .register(
            "mapperForgeCheck",
            MapperForgeTask.class,
            task -> {
              task.setGroup("verification");
              task.setDescription("Checks MyBatis Mapper XML formatting.");
              task.getMode().set(MapperForgeTask.Mode.CHECK);
              task.configureFrom(project, extension);
            });

    project
        .getTasks()
        .register(
            "mapperForgeDryRun",
            MapperForgeTask.class,
            task -> {
              task.setGroup("verification");
              task.setDescription("Prints MyBatis Mapper XML files that would be formatted.");
              task.getMode().set(MapperForgeTask.Mode.DRY_RUN);
              task.configureFrom(project, extension);
            });
  }
}
