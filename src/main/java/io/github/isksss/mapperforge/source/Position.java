package io.github.isksss.mapperforge.source;

/**
 * ソース内の 1 点を表します。
 *
 * @param offset 0 始まりの文字オフセット
 * @param line 1 始まりの行番号
 * @param column 1 始まりの列番号
 */
public record Position(int offset, int line, int column) {}
