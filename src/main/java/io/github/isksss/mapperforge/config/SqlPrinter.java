package io.github.isksss.mapperforge.config;

/** SQL printer の実装選択です。 */
public enum SqlPrinter {
  /** v1 golden file と互換の文字列ベース SQL formatter です。 */
  LEGACY,
  /** SQL AST から Doc Tree を経由して出力する printer です。 */
  AST
}
