package io.github.isksss.mapperforge.ast.sql;

public record UnaryExpression(String operator, Expression expression) implements Expression {}
