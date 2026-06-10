package io.github.isksss.mapperforge.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

final class MapperForgeExtensionTest {
  @Test
  void attributeOrderHelperAddsAndOverwritesConfiguredOrder() {
    Project project = ProjectBuilder.builder().build();
    project.getPluginManager().apply(MapperForgePlugin.class);
    MapperForgeExtension extension = project.getExtensions().getByType(MapperForgeExtension.class);

    extension.attributeOrder("result", List.of("property", "column"));
    extension.attributeOrder("result", List.of("property", "column", "javaType"));

    assertEquals(
        Map.of("result", List.of("property", "column", "javaType")),
        extension.getAttributeOrder().get());
  }
}
