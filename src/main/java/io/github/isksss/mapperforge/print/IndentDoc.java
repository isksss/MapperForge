package io.github.isksss.mapperforge.print;

/**
 * 子 node を現在の indent 幅だけ深く描画する node です。
 *
 * @param contents indent 対象の子 node
 */
public record IndentDoc(Doc contents) implements Doc {}
