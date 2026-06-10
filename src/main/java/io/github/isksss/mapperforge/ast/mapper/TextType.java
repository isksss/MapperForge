package io.github.isksss.mapperforge.ast.mapper;

/** Mapper AST のテキスト種別です。 */
public enum TextType {
  /** SQL として扱うテキストです。 */
  SQL,
  /** 通常の XML テキストです。 */
  PLAIN_TEXT,
  /** 空白のみのテキストです。 */
  WHITESPACE
}
