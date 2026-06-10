package io.github.isksss.mapperforge.print;

import java.util.Arrays;
import java.util.List;

/** Doc Tree を組み立てるための factory です。 */
public final class Docs {
  private static final LineDoc LINE = new LineDoc();
  private static final SoftLineDoc SOFT_LINE = new SoftLineDoc();
  private static final HardLineDoc HARD_LINE = new HardLineDoc();

  private Docs() {}

  /**
   * text node を作成します。
   *
   * @param text 出力する文字列
   * @return text node
   */
  public static TextDoc text(String text) {
    return new TextDoc(text);
  }

  /**
   * flat 時は空白、break 時は改行になる line node を返します。
   *
   * @return line node
   */
  public static LineDoc line() {
    return LINE;
  }

  /**
   * flat 時は空文字、break 時は改行になる soft line node を返します。
   *
   * @return soft line node
   */
  public static SoftLineDoc softLine() {
    return SOFT_LINE;
  }

  /**
   * 常に改行する hard line node を返します。
   *
   * @return hard line node
   */
  public static HardLineDoc hardLine() {
    return HARD_LINE;
  }

  /**
   * 子 node を indent する node を作成します。
   *
   * @param contents indent 対象の子 node
   * @return indent node
   */
  public static IndentDoc indent(Doc contents) {
    return new IndentDoc(contents);
  }

  /**
   * 子 node を group 化する node を作成します。
   *
   * @param contents group 化する子 node
   * @return group node
   */
  public static GroupDoc group(Doc contents) {
    return new GroupDoc(contents);
  }

  /**
   * 複数 node を順に描画する concat node を作成します。
   *
   * @param contents 描画順に並べる子 node
   * @return concat node
   */
  public static ConcatDoc concat(Doc... contents) {
    return concat(Arrays.asList(contents));
  }

  /**
   * 複数 node を順に描画する concat node を作成します。
   *
   * @param contents 描画順に並べる子 node
   * @return concat node
   */
  public static ConcatDoc concat(List<Doc> contents) {
    return new ConcatDoc(contents);
  }
}
