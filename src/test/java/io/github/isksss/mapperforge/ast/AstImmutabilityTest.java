package io.github.isksss.mapperforge.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

final class AstImmutabilityTest {
  private static final Path AST_SOURCE_ROOT =
      Path.of("src/main/java/io/github/isksss/mapperforge/ast");
  private static final Pattern TOP_LEVEL_IMMUTABLE_DECLARATION =
      Pattern.compile("public\\s+(record|sealed\\s+interface|enum)\\s+\\w+");
  private static final Pattern SETTER_METHOD =
      Pattern.compile(
          "\\b(?:public|protected|private)?\\s*(?:final\\s+)?void\\s+set[A-Z]\\w*\\s*\\(");

  @Test
  void astSourcesUseImmutableTopLevelDeclarations() throws IOException {
    try (var paths = Files.walk(AST_SOURCE_ROOT)) {
      for (Path source : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
        String content = Files.readString(source);
        assertTrue(
            TOP_LEVEL_IMMUTABLE_DECLARATION.matcher(content).find(),
            source + " must use a record, sealed interface, or enum top-level declaration");
      }
    }
  }

  @Test
  void astSourcesDoNotDeclareSetters() throws IOException {
    try (var paths = Files.walk(AST_SOURCE_ROOT)) {
      for (Path source : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
        String content = Files.readString(source);
        assertFalse(SETTER_METHOD.matcher(content).find(), source + " must not declare setters");
      }
    }
  }
}
