package io.github.isksss.mapperforge.ast.mapper;

/**
 * XML テキストを表す Mapper AST ノードです。
 *
 * @param type テキスト種別
 * @param value テキスト値
 */
public record TextNode(TextType type, String value) implements MapperNode {}
