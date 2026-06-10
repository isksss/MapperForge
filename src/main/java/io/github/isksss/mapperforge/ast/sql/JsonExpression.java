package io.github.isksss.mapperforge.ast.sql;

public record JsonExpression(Expression expression, String operator, Expression path)
    implements Expression {}
