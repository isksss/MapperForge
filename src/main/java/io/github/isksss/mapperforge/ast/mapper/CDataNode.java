package io.github.isksss.mapperforge.ast.mapper;

import io.github.isksss.mapperforge.ast.sql.SqlNode;
import java.util.Optional;

/**
 * CDATA セクションを表す Mapper AST ノードです。
 *
 * @param raw CDATA 内の元テキスト
 * @param parsed SQL として解析できた場合の AST
 */
public record CDataNode(String raw, Optional<SqlNode> parsed) implements MapperNode {}
