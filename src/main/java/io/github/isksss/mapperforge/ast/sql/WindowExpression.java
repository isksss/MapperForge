package io.github.isksss.mapperforge.ast.sql;

public record WindowExpression(Expression expression, String windowSpec) implements Expression {}
