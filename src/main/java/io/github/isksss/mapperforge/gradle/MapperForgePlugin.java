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
            MapperForgeFormatTask.class,
            task -> {
              task.setGroup("formatting");
              task.setDescription("Formats MyBatis Mapper XML files.");
              task.configureFrom(project, extension);
            });

    project
        .getTasks()
        .register(
            "mapperForgeCheck",
            MapperForgeCheckTask.class,
            task -> {
              task.setGroup("verification");
              task.setDescription("Checks MyBatis Mapper XML formatting.");
              task.configureFrom(project, extension);
            });

    project
        .getTasks()
        .register(
            "mapperForgeDryRun",
            MapperForgeDryRunTask.class,
            task -> {
              task.setGroup("verification");
              task.setDescription("Prints MyBatis Mapper XML files that would be formatted.");
              task.configureFrom(project, extension);
            });
  }
}
