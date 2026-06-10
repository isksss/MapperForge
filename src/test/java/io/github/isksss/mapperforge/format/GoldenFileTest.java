package io.github.isksss.mapperforge.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlPrinter;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

final class GoldenFileTest {
  private static final Set<String> CUSTOM_CONFIG_GOLDEN_FILES =
      Set.of("attribute-order", "ast-sql-printer");
  private final MapperForge mapperForge = new MapperForge();

  @TestFactory
  Stream<DynamicTest> defaultGoldenFiles() throws IOException, URISyntaxException {
    return goldenDirectories()
        .filter(Files::isDirectory)
        .filter(path -> !CUSTOM_CONFIG_GOLDEN_FILES.contains(path.getFileName().toString()))
        .map(
            path ->
                DynamicTest.dynamicTest(
                    path.getFileName().toString(),
                    () -> assertGolden(path.getFileName().toString(), FormatterConfig.defaults())));
  }

  @Test
  void preservesLosslessNodes() throws IOException {
    FormatterConfig config = FormatterConfig.defaults();
    String before = resource("golden/lossless/before.xml");
    String formatted = mapperForge.format(new SourceFile("lossless.xml", before), config);

    assertEquals(resource("golden/lossless/after.xml"), formatted);
    assertTrue(
        mapperForge
            .validate(
                new SourceFile("lossless-before.xml", before),
                new SourceFile("lossless-after.xml", formatted),
                config)
            .success());
  }

  @Test
  void appliesConfiguredAttributeOrder() throws IOException {
    assertGolden("attribute-order", configFor("attribute-order"));
  }

  @Test
  void appliesAstSqlPrinterWhenConfigured() throws IOException {
    assertGolden("ast-sql-printer", configFor("ast-sql-printer"));
  }

  @TestFactory
  Stream<DynamicTest> goldenFilesAreIdempotent() throws IOException, URISyntaxException {
    return goldenDirectories()
        .filter(Files::isDirectory)
        .map(
            path -> {
              String name = path.getFileName().toString();
              return DynamicTest.dynamicTest(
                  name,
                  () -> {
                    FormatterConfig config = configFor(name);
                    String after = resource("golden/" + name + "/after.xml");
                    assertEquals(
                        after, mapperForge.format(new SourceFile(name + ".xml", after), config));
                  });
            });
  }

  private FormatterConfig configFor(String goldenName) {
    if ("attribute-order".equals(goldenName)) {
      return attributeOrderConfig();
    }
    if ("ast-sql-printer".equals(goldenName)) {
      return astSqlPrinterConfig();
    }
    return FormatterConfig.defaults();
  }

  private FormatterConfig attributeOrderConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        defaults.sqlPrinter(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        Map.of("result", List.of("property", "column", "javaType", "jdbcType")));
  }

  private FormatterConfig astSqlPrinterConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        SqlPrinter.AST,
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        defaults.attributeOrder());
  }

  @Test
  void formatIsIdempotent() throws IOException {
    FormatterConfig config = FormatterConfig.defaults();
    String after = resource("golden/dynamic-sql/after.xml");

    assertEquals(after, mapperForge.format(new SourceFile("after.xml", after), config));
  }

  private void assertGolden(String name, FormatterConfig config) throws IOException {
    String before = resource("golden/" + name + "/before.xml");
    String after = resource("golden/" + name + "/after.xml");

    assertEquals(after, mapperForge.format(new SourceFile(name + ".xml", before), config));
  }

  private Stream<Path> goldenDirectories() throws IOException, URISyntaxException {
    Path goldenRoot = Path.of(getClass().getClassLoader().getResource("golden").toURI());
    return Files.list(goldenRoot);
  }

  private String resource(String name) throws IOException {
    try (var stream = getClass().getClassLoader().getResourceAsStream(name)) {
      if (stream == null) {
        throw new IOException("Missing test resource: " + name);
      }
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
