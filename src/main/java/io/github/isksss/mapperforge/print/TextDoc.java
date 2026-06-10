package io.github.isksss.mapperforge.print;

/**
 * そのまま出力する text node です。
 *
 * @param text 出力する文字列
 */
public record TextDoc(String text) implements Doc {
  /** {@code null} text を空文字へ正規化します。 */
  public TextDoc {
    text = text == null ? "" : text;
  }
}
