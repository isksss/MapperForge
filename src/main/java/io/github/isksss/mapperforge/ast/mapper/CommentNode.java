package io.github.isksss.mapperforge.ast.mapper;

public record CommentNode(CommentType type, String content) implements MapperNode {}
