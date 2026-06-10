package io.github.isksss.mapperforge.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

  private void writeProject() throws IOException {
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
                """,
        StandardCharsets.UTF_8);
  }
}
