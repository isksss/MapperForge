package io.github.isksss.mapperforge.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.source.SourceFile;
import org.junit.jupiter.api.Test;

final class AstDiffTest {
  private final AstDiff diff = new AstDiff();

  @Test
  void returnsEmptyStringForFormattingOnlyChanges() {
    SourceFile before =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users where active = 1</select></mapper>");
    SourceFile after =
        new SourceFile(
            "UserMapper.xml",
            """
            <mapper namespace="sample">
                <select id="find">
                    SELECT id
                    FROM users
                    WHERE active = 1
                </select>
            </mapper>
            """);

    assertEquals("", diff.create(before, after));
  }

  @Test
  void reportsSemanticAstDescriptorChanges() {
    SourceFile before =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users where active = 1</select></mapper>");
    SourceFile after =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select name from users where active = 1</select></mapper>");

    assertEquals(
        """
        # AST Diff
        - /mapper[0]/select[0] sql: SELECT ID FROM USERS WHERE ACTIVE = 1
        + /mapper[0]/select[0] sql: SELECT NAME FROM USERS WHERE ACTIVE = 1
        """,
        diff.create(before, after));
  }

  @Test
  void reportsAttributeChanges() {
    SourceFile before =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\" resultType=\"User\">select id from users</select></mapper>");
    SourceFile after =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\" resultType=\"Account\">select id from users</select></mapper>");

    assertEquals(
        """
        # AST Diff
        - /mapper[0]/select[0] @resultType=User
        + /mapper[0]/select[0] @resultType=Account
        """,
        diff.create(before, after));
  }
}
