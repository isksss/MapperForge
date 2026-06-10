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
        new MapperForge().diff(before, after, FormatterConfig.defaults()));
  }

  @Test
  void returnsEmptyStringWhenContentIsEqual() {
    SourceFile source = new SourceFile("UserMapper.xml", "same\n");

    assertEquals("", new MapperForge().diff(source, source, FormatterConfig.defaults()));
  }
}
