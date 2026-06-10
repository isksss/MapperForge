package io.github.isksss.mapperforge.config;

/** SQL formatter の出力スタイルです。 */
public enum SqlFormatStyle {
  /** SQL を可能な限り 1 行で出力します。 */
  SINGLE_LINE,
  /** select list などを適度に詰めて出力します。 */
  COMPACT,
  /** clause と select list を複数行で出力します。 */
  MULTI_LINE
}
