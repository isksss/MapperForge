package io.github.isksss.mapperforge.ast.mapper;

public sealed interface MapperNode permits ElementNode, TextNode, CommentNode, CDataNode {}
