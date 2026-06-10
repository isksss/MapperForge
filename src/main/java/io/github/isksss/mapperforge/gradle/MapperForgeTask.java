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
import io.github.isksss.mapperforge.report.ValidationReportFormatter;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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

/** MapperForge の Gradle task 共通基底クラスです。 */
@DisableCachingByDefault(because = "Mode-specific subclasses define cache behavior.")
public abstract class MapperForgeTask extends DefaultTask {
  /** Gradle task の動作モードです。 */
  public enum Mode {
    /** 対象 file を整形して書き戻します。 */
    FORMAT,
    /** 対象 file が整形済みか検査します。 */
    CHECK,
    /** 対象 file を変更せず diff を表示します。 */
    DRY_RUN
  }

  private final ConfigurableFileCollection sourceFiles = getProject().files();
  private final MapperXmlParser parser = new MapperXmlParser();
  private final ValidationReportFormatter validationReportFormatter =
      new ValidationReportFormatter();

  /** Gradle が task instance を生成するための constructor です。 */
  public MapperForgeTask() {}

  /**
   * task の動作モードを返します。
   *
   * @return task の動作モード
   */
  @Input
  public abstract Property<Mode> getMode();

  /**
   * 対象 SQL dialect を返します。
   *
   * @return 対象 SQL dialect
   */
  @Input
  public abstract Property<String> getDialect();

  /**
   * formatter の SemVer バージョンを返します。
   *
   * @return formatter の SemVer バージョン
   */
  @Input
  public abstract Property<String> getFormatterVersion();

  /**
   * 処理対象 file の include glob を返します。
   *
   * @return include glob の一覧
   */
  @Input
  public abstract ListProperty<String> getInclude();

  /**
   * 処理対象から除外する exclude glob を返します。
   *
   * @return exclude glob の一覧
   */
  @Input
  public abstract ListProperty<String> getExclude();

  /**
   * インデント幅を返します。
   *
   * @return インデント幅
   */
  @Input
  public abstract Property<Integer> getIndentSize();

  /**
   * 最大行長を返します。
   *
   * @return 最大行長
   */
  @Input
  public abstract Property<Integer> getMaxLineLength();

  /**
   * 出力改行コードを返します。
   *
   * @return 出力改行コード
   */
  @Input
  public abstract Property<String> getLineEnding();

  /**
   * SQL formatter の出力スタイルを返します。
   *
   * @return SQL formatter の出力スタイル
   */
  @Input
  public abstract Property<String> getSqlFormatStyle();

  /**
   * SQL printer 実装を返します。
   *
   * @return SQL printer 実装
   */
  @Input
  public abstract Property<String> getSqlPrinter();

  /**
   * XML tag の折り返し方針を返します。
   *
   * @return XML tag の折り返し方針
   */
  @Input
  public abstract Property<String> getTagWrapStyle();

  /**
   * XML attribute の配置方針を返します。
   *
   * @return XML attribute の配置方針
   */
  @Input
  public abstract Property<String> getAttributeLayout();

  /**
   * 空白 node を意味検証対象として保持するかを返します。
   *
   * @return 空白 node を保持する場合は {@code true}
   */
  @Input
  public abstract Property<Boolean> getPreserveWhitespace();

  /**
   * CDATA wrapper を保持するかを返します。
   *
   * @return CDATA wrapper を保持する場合は {@code true}
   */
  @Input
  public abstract Property<Boolean> getPreserveCdata();

  /**
   * CDATA 内 SQL を整形するかを返します。
   *
   * @return CDATA 内 SQL を整形する場合は {@code true}
   */
  @Input
  public abstract Property<Boolean> getFormatSqlInsideCdata();

  /**
   * 検証失敗時に task を失敗させるかを返します。
   *
   * @return 検証失敗時に task を失敗させる場合は {@code true}
   */
  @Input
  public abstract Property<Boolean> getStrict();

  /**
   * tag ごとの attribute 順序設定を返します。
   *
   * @return tag 名から attribute 順序への mapping
   */
  @Input
  public abstract MapProperty<String, List<String>> getAttributeOrder();

  /**
   * task が処理する source file collection を返します。
   *
   * @return source file collection
   */
  @InputFiles
  @PathSensitive(PathSensitivity.RELATIVE)
  public ConfigurableFileCollection getSourceFiles() {
    return sourceFiles;
  }

  /**
   * Gradle task tracking 用の state file を返します。
   *
   * @return task state file
   */
  @OutputFile
  public abstract RegularFileProperty getStateFile();

  /**
   * task 実行時に使う MapperForge facade を返します。
   *
   * @return MapperForge facade
   */
  @Internal
  protected MapperForge getMapperForge() {
    return new MapperForge();
  }

  /**
   * Gradle extension と YAML から task property を設定します。
   *
   * @param project 対象 Gradle project
   * @param extension MapperForge Gradle extension
   */
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

  /** task を実行します。 */
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
            String message = validationReportFormatter.format(file.toPath(), validation);
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
        enumValue("dialect", Dialect.class, getDialect().get()),
        getFormatterVersion().get(),
        getInclude().get(),
        getExclude().get(),
        getIndentSize().get(),
        getMaxLineLength().get(),
        getLineEnding().get(),
        enumValue("sqlFormatStyle", SqlFormatStyle.class, getSqlFormatStyle().get()),
        enumValue("sqlPrinter", SqlPrinter.class, getSqlPrinter().get()),
        enumValue("tagWrapStyle", TagWrapStyle.class, getTagWrapStyle().get()),
        enumValue("attributeLayout", AttributeLayout.class, getAttributeLayout().get()),
        getPreserveWhitespace().get(),
        getPreserveCdata().get(),
        getFormatSqlInsideCdata().get(),
        getStrict().get(),
        Map.copyOf(getAttributeOrder().get()));
  }

  private <E extends Enum<E>> E enumValue(String key, Class<E> type, String raw) {
    try {
      return Enum.valueOf(type, raw);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          key + " must be one of " + allowedValues(type) + ": " + raw, e);
    }
  }

  private <E extends Enum<E>> List<String> allowedValues(Class<E> type) {
    return Arrays.stream(type.getEnumConstants()).map(Enum::name).toList();
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

  private void writeStateFile(FormatterConfig config) throws IOException {
    Path stateFile = getStateFile().get().getAsFile().toPath();
    Files.createDirectories(stateFile.getParent());
    Files.writeString(
        stateFile,
        "mode=" + getMode().get() + "\nformatterVersion=" + config.formatterVersion() + "\n",
        StandardCharsets.UTF_8);
  }
}
