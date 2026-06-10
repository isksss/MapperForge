package io.github.isksss.mapperforge.source;

/**
 * ソース内の範囲を表します。
 *
 * @param start 範囲の開始位置
 * @param end 範囲の終了位置
 */
public record Range(Position start, Position end) {}
