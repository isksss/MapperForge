package io.github.isksss.mapperforge.token;

/** SQL / OGNL / Mapper XML 由来の字句解析で使う token 種別です。 */
public enum TokenType {
  /** 識別子、列名、テーブル名、引用識別子を表します。 */
  IDENTIFIER,
  /** 予約語または parser が予約語として扱う単語を表します。 */
  KEYWORD,
  /** SQL 文字列 literal を表します。 */
  STRING,
  /** 数値 literal を表します。 */
  NUMBER,
  /** 演算子、区切り文字、括弧などの記号を表します。 */
  SYMBOL,
  /** MyBatis の {@code #{...}} または {@code ${...}} placeholder を表します。 */
  PLACEHOLDER,
  /** SQL コメントを表します。 */
  COMMENT,
  /** 入力の終端を表します。 */
  EOF,
  /** tokenizer が分類できない token を表します。 */
  UNKNOWN
}
