package io.github.isksss.mapperforge.gradle;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.AttributeLayout;
import io.github.isksss.mapperforge.config.ConfigLoader;
import io.github.isksss.mapperforge.config.Dialect;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlFormatStyle;
import io.github.isksss.mapperforge.config.SqlPrinter;
import io.github.isksss.mapperforge.config.TagWrapStyle;
import io.github.isksss.mapperforge.logging.MapperForgeLoggers;
import io.github.isksss.mapperforge.parse.MapperXmlParser;
import io.github.isksss.mapperforge.source.SourceFile;
import io.github.isksss.mapperforge.validation.ValidationResult;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Mode-specific subclasses define cache behavior.")
public abstract class MapperForgeTask extends DefaultTask {
  public enum Mode {
    FORMAT,
    CHECK,
    DRY_RUN
  }

  private final ConfigurableFileCollection sourceFiles = getProject().files();
  private final MapperXmlParser parser = new MapperXmlParser();

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
  public abstract Property<String> getSqlPrinter();

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

  @OutputFile
  public abstract RegularFileProperty getStateFile();

  @Internal
  protected MapperForge getMapperForge() {
    return new MapperForge();
  }

  public void configureFrom(Project project, MapperForgeExtension extension) {
    FormatterConfig yamlConfig = new ConfigLoader().load(project.file("mapperforge.yml").toPath());

    getDialect().set(extension.getDialect().orElse(yamlConfig.dialect().name()));
    getFormatterVersion()
        .set(extension.getFormatterVersion().orElse(yamlConfig.formatterVersion()));
    getInclude().set(extension.getInclude().orElse(yamlConfig.include()));
    getExclude().set(extension.getExclude().orElse(yamlConfig.exclude()));
    getIndentSize().set(extension.getIndentSize().orElse(yamlConfig.indentSize()));
    getMaxLineLength().set(extension.getMaxLineLength().orElse(yamlConfig.maxLineLength()));
    getLineEnding().set(extension.getLineEnding().orElse(yamlConfig.lineEnding()));
    getSqlFormatStyle()
        .set(extension.getSqlFormatStyle().orElse(yamlConfig.sqlFormatStyle().name()));
    getSqlPrinter().set(extension.getSqlPrinter().orElse(yamlConfig.sqlPrinter().name()));
    getTagWrapStyle().set(extension.getTagWrapStyle().orElse(yamlConfig.tagWrapStyle().name()));
    getAttributeLayout()
        .set(extension.getAttributeLayout().orElse(yamlConfig.attributeLayout().name()));
    getPreserveWhitespace()
        .set(extension.getPreserveWhitespace().orElse(yamlConfig.preserveWhitespace()));
    getPreserveCdata().set(extension.getPreserveCdata().orElse(yamlConfig.preserveCdata()));
    getFormatSqlInsideCdata()
        .set(extension.getFormatSqlInsideCdata().orElse(yamlConfig.formatSqlInsideCdata()));
    getStrict().set(extension.getStrict().orElse(yamlConfig.strict()));
    getAttributeOrder().set(extension.getAttributeOrder().orElse(yamlConfig.attributeOrder()));
    getStateFile()
        .convention(
            project.getLayout().getBuildDirectory().file("mapperforge/" + getName() + ".state"));

    ConfigurableFileTree fileTree = project.fileTree(project.getProjectDir());
    fileTree.include(getInclude().get());
    fileTree.exclude(getExclude().get());
    sourceFiles.setFrom(fileTree.filter(file -> file.isFile() && file.getName().endsWith(".xml")));
  }

  @TaskAction
  public void run() {
    FormatterConfig config;
    try {
      config = config();
    } catch (ConfigLoader.ConfigException e) {
      throw new GradleException(e.code() + ": " + e.getMessage(), e);
    } catch (IllegalArgumentException e) {
      throw new GradleException("CONFIG_ERROR: " + e.getMessage(), e);
    }
    MapperForgeLoggers.GRADLE.info(
        "Running MapperForge task: {} mode={}", getName(), getMode().get());
    boolean failed = false;
    for (var file : sourceFiles.getFiles()) {
      if (!file.isFile() || !file.getName().endsWith(".xml") || !isMapperXml(file.toPath())) {
        continue;
      }
      try {
        String before = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        SourceFile source = new SourceFile(file.getName(), before);
        String after = getMapperForge().format(source, config);
        if (!before.equals(after)) {
          var validation =
              getMapperForge().validate(source, new SourceFile(file.getName(), after), config);
          if (!validation.success()) {
            String message = validationFailureMessage(file.toPath(), validation);
            if (config.strict()) {
              throw new GradleException(message);
            }
            getLogger().warn(message);
            continue;
          }
          switch (getMode().get()) {
            case FORMAT -> Files.writeString(file.toPath(), after, StandardCharsets.UTF_8);
            case CHECK -> {
              failed = true;
              getLogger().error("MapperForge formatting required: {}", file);
            }
            case DRY_RUN -> {
              String relativePath = relativePath(file.toPath());
              getLogger()
                  .lifecycle(
                      getMapperForge()
                          .diff(
                              new SourceFile(relativePath, before),
                              new SourceFile(relativePath, after),
                              config)
                          .stripTrailing());
            }
          }
        }
      } catch (IOException e) {
        throw new GradleException("Failed to process " + file, e);
      }
    }
    if (failed) {
      throw new GradleException("MapperForge check failed.");
    }
    try {
      writeStateFile(config);
    } catch (IOException e) {
      throw new GradleException("Failed to write MapperForge task state", e);
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
        SqlPrinter.valueOf(getSqlPrinter().get()),
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
      String content = Files.readString(path, StandardCharsets.UTF_8);
      return parser.parseMapper(new SourceFile(path.getFileName().toString(), content)).isPresent();
    } catch (MapperXmlParser.ParserException e) {
      return false;
    } catch (IOException e) {
      throw new GradleException("Failed to read " + path, e);
    }
  }

  private String relativePath(Path path) {
    return getProject().getProjectDir().toPath().relativize(path).toString();
  }

  private String validationFailureMessage(Path file, ValidationResult validation) {
    String details =
        validation.errors().stream()
            .findFirst()
            .map(error -> error.code() + "/" + error.type() + ": " + error.message())
            .orElse("UNKNOWN");
    return "MapperForge validation failed: " + file + " [" + details + "]";
  }

  private void writeStateFile(FormatterConfig config) throws IOException {
    Path stateFile = getStateFile().get().getAsFile().toPath();
    Files.createDirectories(stateFile.getParent());
    Files.writeString(
        stateFile,
        "mode=" + getMode().get() + "\nformatterVersion=" + config.formatterVersion() + "\n",
        StandardCharsets.UTF_8);
  }
}
