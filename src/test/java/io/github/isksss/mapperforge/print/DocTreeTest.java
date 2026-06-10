package io.github.isksss.mapperforge.print;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

final class DocTreeTest {
  @Test
  void docsFactoryCreatesAllPlanDocNodeTypes() {
    TextDoc text = assertInstanceOf(TextDoc.class, Docs.text("SELECT"));
    assertEquals("SELECT", text.text());
    assertInstanceOf(LineDoc.class, Docs.line());
    assertInstanceOf(SoftLineDoc.class, Docs.softLine());
    assertInstanceOf(HardLineDoc.class, Docs.hardLine());
    assertInstanceOf(IndentDoc.class, Docs.indent(text));
    assertInstanceOf(GroupDoc.class, Docs.group(text));
    assertInstanceOf(ConcatDoc.class, Docs.concat(text, Docs.line(), Docs.text("1")));
  }

  @Test
  void textDocNormalizesNullToEmptyString() {
    assertEquals("", Docs.text(null).text());
  }

  @Test
  void concatDocDefensivelyCopiesContents() {
    List<Doc> mutable = new ArrayList<>();
    mutable.add(Docs.text("SELECT"));

    ConcatDoc concat = Docs.concat(mutable);
    mutable.add(Docs.text("1"));

    assertEquals(1, concat.contents().size());
    assertThrows(UnsupportedOperationException.class, () -> concat.contents().add(Docs.text("2")));
  }
}
