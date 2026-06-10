package io.github.isksss.mapperforge.validation;

/** ValidationError の詳細な差分分類です。 */
public enum ErrorType {
  /** SQL statement の意味構造差分を表します。 */
  STATEMENT,
  /** OGNL などの expression 差分を表します。 */
  EXPRESSION,
  /** XML コメントまたは SQL コメントの差分を表します。 */
  COMMENT,
  /** CDATA セクションの差分を表します。 */
  CDATA,
  /** preserveWhitespace=true 時の空白差分を表します。 */
  WHITESPACE,
  /** MyBatis placeholder の差分を表します。 */
  PLACEHOLDER,
  /** MyBatis 専用ノード以外の XML 要素差分を表します。 */
  GENERIC_ELEMENT,
  /** XML 構造全般の差分を表します。 */
  XML
}
