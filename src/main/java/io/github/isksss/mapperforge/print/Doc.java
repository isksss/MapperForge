package io.github.isksss.mapperforge.print;

public sealed interface Doc
    permits TextDoc, LineDoc, SoftLineDoc, HardLineDoc, IndentDoc, GroupDoc, ConcatDoc {}
