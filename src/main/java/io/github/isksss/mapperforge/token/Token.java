package io.github.isksss.mapperforge.token;

import io.github.isksss.mapperforge.source.Range;

/**
 * 字句解析結果の 1 トークンを表します。
 *
 * @param type トークン種別
 * @param text 元ソース上のトークン文字列
 * @param range 元ソース上の範囲
 */
public record Token(TokenType type, String text, Range range) {}
