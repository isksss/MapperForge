package io.github.isksss.mapperforge.ast.mapper;

import io.github.isksss.mapperforge.ast.sql.SqlNode;
import java.util.Optional;

public record CDataNode(String raw, Optional<SqlNode> parsed) implements MapperNode {}
