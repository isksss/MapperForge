package io.github.isksss.mapperforge.print;

import java.util.Arrays;
import java.util.List;

public final class Docs {
  private static final LineDoc LINE = new LineDoc();
  private static final SoftLineDoc SOFT_LINE = new SoftLineDoc();
  private static final HardLineDoc HARD_LINE = new HardLineDoc();

  private Docs() {}

  public static TextDoc text(String text) {
    return new TextDoc(text);
  }

  public static LineDoc line() {
    return LINE;
  }

  public static SoftLineDoc softLine() {
    return SOFT_LINE;
  }

  public static HardLineDoc hardLine() {
    return HARD_LINE;
  }

  public static IndentDoc indent(Doc contents) {
    return new IndentDoc(contents);
  }

  public static GroupDoc group(Doc contents) {
    return new GroupDoc(contents);
  }

  public static ConcatDoc concat(Doc... contents) {
    return concat(Arrays.asList(contents));
  }

  public static ConcatDoc concat(List<Doc> contents) {
    return new ConcatDoc(contents);
  }
}
