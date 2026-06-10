package io.github.isksss.mapperforge.ast.sql;

public record CastExpression(Expression expression, String typeName) implements Expression {}
