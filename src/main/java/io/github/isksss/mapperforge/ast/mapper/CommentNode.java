package io.github.isksss.mapperforge.ast.mapper;

/**
 * XML コメントまたは SQL コメントを表す Mapper AST ノードです。
 *
 * @param type コメント種別
 * @param content コメント本文
 */
public record CommentNode(CommentType type, String content) implements MapperNode {}
