package io.github.isksss.mapperforge.print;

/**
 * 子 node を flat に描画できるか判定する group node です。
 *
 * @param contents group 化する子 node
 */
public record GroupDoc(Doc contents) implements Doc {}
