package io.github.isksss.mapperforge.ast.mapper;

public sealed interface MapperNode permits GenericElementNode, TextNode, CommentNode, CDataNode {}
