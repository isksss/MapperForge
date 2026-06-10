package io.github.isksss.mapperforge.print;

/** LayoutEngine が描画する document model の基底型です。 */
public sealed interface Doc
    permits TextDoc, LineDoc, SoftLineDoc, HardLineDoc, IndentDoc, GroupDoc, ConcatDoc {}
