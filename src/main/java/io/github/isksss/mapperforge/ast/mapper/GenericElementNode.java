package io.github.isksss.mapperforge.ast.mapper;

import java.util.List;

public record GenericElementNode(
    String tagName, List<AttributeNode> attributes, List<MapperNode> children)
    implements MapperNode {}
