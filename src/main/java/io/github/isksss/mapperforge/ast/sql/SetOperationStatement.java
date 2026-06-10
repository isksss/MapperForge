package io.github.isksss.mapperforge.ast.sql;

public record SetOperationStatement(String raw, String operator) implements Statement {}
