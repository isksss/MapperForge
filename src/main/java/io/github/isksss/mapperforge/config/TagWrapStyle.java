package io.github.isksss.mapperforge.config;

/** XML tag の折り返し方針です。 */
public enum TagWrapStyle {
  /** 入力に近い tag 配置を維持します。 */
  KEEP,
  /** 最大行長に応じて自動で折り返します。 */
  AUTO,
  /** attribute を常に複数行へ折り返します。 */
  ALWAYS
}
