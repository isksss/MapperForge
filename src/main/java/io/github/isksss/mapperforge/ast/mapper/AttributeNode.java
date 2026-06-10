package io.github.isksss.mapperforge.ast.mapper;

/**
 * XML 属性を表す Mapper AST ノードです。
 *
 * @param name 属性名
 * @param value 属性値
 */
public record AttributeNode(String name, String value) {}
