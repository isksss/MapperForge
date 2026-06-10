package io.github.isksss.mapperforge.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class MapperForgePluginFunctionalTest {
  @TempDir Path projectDir;

  @Test
  void formatTaskFormatsMapperXml() throws IOException {
    writeProject();
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":mapperForgeFormat").getOutcome());
    assertTrue(Files.readString(mapper, StandardCharsets.UTF_8).contains("SELECT"));
  }

  @Test
  void checkTaskFailsWhenFormattingIsRequired() throws IOException {
    writeProject();
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeCheck")
            .buildAndFail();

    assertEquals(TaskOutcome.FAILED, result.task(":mapperForgeCheck").getOutcome());
    assertTrue(result.getOutput().contains("MapperForge check failed"));
  }

  @Test
  void checkTaskIsUpToDateAfterSuccessfulRun() throws IOException {
    writeProject();
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        """
        <mapper namespace="sample.UserMapper">
            <select id="find">
                SELECT
                    id
                FROM users
            </select>
        </mapper>
        """,
        StandardCharsets.UTF_8);

    var runner =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeCheck", "--build-cache");

    var first = runner.build();
    var second = runner.build();

    assertTrue(
        List.of(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
            .contains(first.task(":mapperForgeCheck").getOutcome()));
    assertEquals(TaskOutcome.UP_TO_DATE, second.task(":mapperForgeCheck").getOutcome());
  }

  @Test
  void dryRunPrintsUnifiedDiff() throws IOException {
    writeProject();
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeDryRun")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":mapperForgeDryRun").getOutcome());
    assertTrue(result.getOutput().contains("--- src/main/resources/sample/UserMapper.xml"));
    assertTrue(result.getOutput().contains("+++ src/main/resources/sample/UserMapper.xml"));
    assertTrue(result.getOutput().contains("+        SELECT"));
  }

  @Test
  void ignoresXmlWhenMapperAppearsOnlyInComment() throws IOException {
    writeProject();
    Path xml = projectDir.resolve("src/main/resources/sample/NotMapper.xml");
    Files.createDirectories(xml.getParent());
    Files.writeString(
        xml,
        "<!-- <mapper namespace=\"sample.UserMapper\"> --><root><select>select id from users</select></root>",
        StandardCharsets.UTF_8);

    GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withPluginClasspath()
        .withArguments("mapperForgeFormat")
        .build();

    assertEquals(
        "<!-- <mapper namespace=\"sample.UserMapper\"> --><root><select>select id from users</select></root>",
        Files.readString(xml, StandardCharsets.UTF_8));
  }

  @Test
  void yamlConfigProvidesDefaultsAndGradleDslOverridesIt() throws IOException {
    Files.writeString(
        projectDir.resolve("mapperforge.yml"),
        """
        include:
          - custom/**/*.xml
        indentSize: 2
        """,
        StandardCharsets.UTF_8);
    Files.writeString(
        projectDir.resolve("settings.gradle.kts"), "rootProject.name = \"fixture\"\n");
    Files.writeString(
        projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("io.github.isksss.mapperforge")
        }

        mapperForge {
            indentSize = 6
        }
        """,
        StandardCharsets.UTF_8);
    Path mapper = projectDir.resolve("custom/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withPluginClasspath()
        .withArguments("mapperForgeFormat")
        .build();

    String formatted = Files.readString(mapper, StandardCharsets.UTF_8);
    assertTrue(formatted.contains("\n      <select"));
  }

  @Test
  void gradleDslRejectsInvalidFormatterVersion() throws IOException {
    writeProject(
        """
        mapperForge {
            formatterVersion = "latest"
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat")
            .buildAndFail();

    assertTrue(result.getOutput().contains("formatterVersion must be SemVer: latest"));
  }

  @Test
  void gradleDslRejectsInvalidIndentSize() throws IOException {
    writeProject(
        """
        mapperForge {
            indentSize = -1
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat")
            .buildAndFail();

    assertTrue(result.getOutput().contains("indentSize must be zero or greater: -1"));
  }

  @Test
  void gradleDslNormalizesLineEndingAlias() throws IOException {
    writeProject(
        """
        mapperForge {
            lineEnding = "CRLF"
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    Files.writeString(
        mapper,
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>",
        StandardCharsets.UTF_8);

    GradleRunner.create()
        .withProjectDir(projectDir.toFile())
        .withPluginClasspath()
        .withArguments("mapperForgeFormat")
        .build();

    assertTrue(Files.readString(mapper, StandardCharsets.UTF_8).contains("\r\n"));
  }

  @Test
  void formatTaskFormatsSqlInsideCdataWhenConfigured() throws IOException {
    writeProject(
        """
        mapperForge {
            formatSqlInsideCdata = true
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    String original =
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\"><![CDATA[select id from users]]></select></mapper>";
    Files.writeString(mapper, original, StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":mapperForgeFormat").getOutcome());
    assertTrue(Files.readString(mapper, StandardCharsets.UTF_8).contains("<![CDATA[SELECT"));
  }

  @Test
  void nonStrictFormatTaskFormatsSqlInsideCdataWhenConfigured() throws IOException {
    writeProject(
        """
        mapperForge {
            formatSqlInsideCdata = true
            strict = false
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    String original =
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\"><![CDATA[select id from users]]></select></mapper>";
    Files.writeString(mapper, original, StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":mapperForgeFormat").getOutcome());
    assertTrue(Files.readString(mapper, StandardCharsets.UTF_8).contains("<![CDATA[SELECT"));
  }

  @Test
  void nonStrictFormatTaskWarnsAndKeepsOriginalWhenValidationFails() throws IOException {
    writeProject(
        """
        mapperForge {
            preserveWhitespace = true
            strict = false
        }
        """);
    Path mapper = projectDir.resolve("src/main/resources/sample/UserMapper.xml");
    Files.createDirectories(mapper.getParent());
    String original =
        "<mapper namespace=\"sample.UserMapper\"><select id=\"find\">select id from users</select></mapper>";
    Files.writeString(mapper, original, StandardCharsets.UTF_8);

    var result =
        GradleRunner.create()
            .withProjectDir(projectDir.toFile())
            .withPluginClasspath()
            .withArguments("mapperForgeFormat", "--warning-mode=all")
            .build();

    assertEquals(TaskOutcome.SUCCESS, result.task(":mapperForgeFormat").getOutcome());
    assertTrue(result.getOutput().contains("MapperForge validation failed"));
    assertEquals(original, Files.readString(mapper, StandardCharsets.UTF_8));
  }

  private void writeProject() throws IOException {
    writeProject("");
  }

  private void writeProject(String mapperForgeConfig) throws IOException {
    Files.writeString(
        projectDir.resolve("settings.gradle.kts"),
        "rootProject.name = \"fixture\"\n",
        StandardCharsets.UTF_8);
    Files.writeString(
        projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("io.github.isksss.mapperforge")
        }
        %s
        """
            .formatted(mapperForgeConfig),
        StandardCharsets.UTF_8);
  }
}
