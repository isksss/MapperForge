package io.github.isksss.mapperforge.ast.sql;

public record BetweenExpression(Expression expression, Expression lower, Expression upper)
    implements Expression {}
