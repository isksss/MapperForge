package io.github.isksss.mapperforge.gradle;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.AttributeLayout;
import io.github.isksss.mapperforge.config.Dialect;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import io.github.isksss.mapperforge.config.TagWrapStyle;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "The format mode writes source files in place.")
public abstract class MapperForgeTask extends DefaultTask {
  public enum Mode {
    FORMAT,
    CHECK,
    DRY_RUN
  }

  private final ConfigurableFileCollection sourceFiles = getProject().files();

  @Input
  public abstract Property<Mode> getMode();

  @Input
  public abstract Property<String> getDialect();

  @Input
  public abstract Property<String> getFormatterVersion();

  @Input
  public abstract ListProperty<String> getInclude();

  @Input
  public abstract ListProperty<String> getExclude();

  @Input
  public abstract Property<Integer> getIndentSize();

  @Input
  public abstract Property<Integer> getMaxLineLength();

  @Input
  public abstract Property<String> getLineEnding();

  @Input
  public abstract Property<String> getSqlFormatStyle();

  @Input
  public abstract Property<String> getTagWrapStyle();

  @Input
  public abstract Property<String> getAttributeLayout();

  @Input
  public abstract Property<Boolean> getPreserveWhitespace();

  @Input
  public abstract Property<Boolean> getPreserveCdata();

  @Input
  public abstract Property<Boolean> getFormatSqlInsideCdata();

  @Input
  public abstract Property<Boolean> getStrict();

  @Input
  public abstract MapProperty<String, List<String>> getAttributeOrder();

  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public ConfigurableFileCollection getSourceFiles() {
    return sourceFiles;
  }

  @OutputFiles
  protected ConfigurableFileCollection getOutputFiles() {
    return sourceFiles;
  }

  @Internal
  protected MapperForge getMapperForge() {
    return new MapperForge();
  }

  public void configureFrom(Project project, MapperForgeExtension extension) {
    getDialect().set(extension.getDialect());
    getFormatterVersion().set(extension.getFormatterVersion());
    getInclude().set(extension.getInclude());
    getExclude().set(extension.getExclude());
    getIndentSize().set(extension.getIndentSize());
    getMaxLineLength().set(extension.getMaxLineLength());
    getLineEnding().set(extension.getLineEnding());
    getSqlFormatStyle().set(extension.getSqlFormatStyle());
    getTagWrapStyle().set(extension.getTagWrapStyle());
    getAttributeLayout().set(extension.getAttributeLayout());
    getPreserveWhitespace().set(extension.getPreserveWhitespace());
    getPreserveCdata().set(extension.getPreserveCdata());
    getFormatSqlInsideCdata().set(extension.getFormatSqlInsideCdata());
    getStrict().set(extension.getStrict());
    getAttributeOrder().set(extension.getAttributeOrder());

    sourceFiles.setFrom(
        project.provider(
            () ->
                project.fileTree(
                    project.getProjectDir(),
                    spec -> {
                      for (String include : getInclude().get()) {
                        spec.include(include);
                      }
                      for (String exclude : getExclude().get()) {
                        spec.exclude(exclude);
                      }
                    })));
  }

  @TaskAction
  public void run() {
    FormatterConfig config = config();
    boolean failed = false;
    for (var file : sourceFiles.getFiles()) {
      if (!file.isFile() || !isMapperXml(file.toPath())) {
        continue;
      }
      try {
        String before = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        SourceFile source = new SourceFile(file.getName(), before);
        String after = getMapperForge().format(source, config);
        if (!before.equals(after)) {
          switch (getMode().get()) {
            case FORMAT -> Files.writeString(file.toPath(), after, StandardCharsets.UTF_8);
            case CHECK -> {
              failed = true;
              getLogger().error("MapperForge formatting required: {}", file);
            }
            case DRY_RUN -> getLogger().lifecycle("MapperForge would format: {}", file);
          }
        }
      } catch (IOException e) {
        throw new GradleException("Failed to process " + file, e);
      }
    }
    if (failed) {
      throw new GradleException("MapperForge check failed.");
    }
  }

  private FormatterConfig config() {
    return new FormatterConfig(
        Dialect.valueOf(getDialect().get()),
        getFormatterVersion().get(),
        getInclude().get(),
        getExclude().get(),
        getIndentSize().get(),
        getMaxLineLength().get(),
        getLineEnding().get(),
        SqlFormatStyle.valueOf(getSqlFormatStyle().get()),
        TagWrapStyle.valueOf(getTagWrapStyle().get()),
        AttributeLayout.valueOf(getAttributeLayout().get()),
        getPreserveWhitespace().get(),
        getPreserveCdata().get(),
        getFormatSqlInsideCdata().get(),
        getStrict().get(),
        Map.copyOf(getAttributeOrder().get()));
  }

  private boolean isMapperXml(java.nio.file.Path path) {
    try {
      String prefix = Files.readString(path, StandardCharsets.UTF_8);
      return prefix.contains("<mapper ");
    } catch (IOException e) {
      throw new GradleException("Failed to read " + path, e);
    }
  }
}
