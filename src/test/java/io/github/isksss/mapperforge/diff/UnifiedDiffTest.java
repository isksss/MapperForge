package io.github.isksss.mapperforge.diff;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;
import org.junit.jupiter.api.Test;

final class UnifiedDiffTest {
  @Test
  void createsUnifiedDiffForChangedContent() {
    SourceFile before = new SourceFile("UserMapper.xml", "one\ntwo\nthree\n");
    SourceFile after = new SourceFile("UserMapper.xml", "one\nTWO\nthree\n");

    assertEquals(
        """
        --- UserMapper.xml
        +++ UserMapper.xml
        @@ -1,3 +1,3 @@
         one
        -two
        +TWO
         three
        """,
        new UnifiedDiff().create(before, after));
  }

  @Test
  void returnsEmptyStringWhenContentIsEqual() {
    SourceFile source = new SourceFile("UserMapper.xml", "same\n");

    assertEquals("", new UnifiedDiff().create(source, source));
  }

  @Test
  void mapperForgeDiffPrefixesAstDiffWhenStructureChanges() {
    SourceFile before =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users</select></mapper>");
    SourceFile after =
        new SourceFile(
            "UserMapper.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select name from users</select></mapper>");

    assertEquals(
        """
        # AST Diff
        - /mapper[0]/select[0] sql: SELECT ID FROM USERS
        + /mapper[0]/select[0] sql: SELECT NAME FROM USERS

        --- UserMapper.xml
        +++ UserMapper.xml
        @@ -1,1 +1,1 @@
        -<mapper namespace="sample"><select id="find">select id from users</select></mapper>
        +<mapper namespace="sample"><select id="find">select name from users</select></mapper>
        """,
        new MapperForge().diff(before, after, FormatterConfig.defaults()));
  }
}
