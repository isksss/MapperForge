package io.github.isksss.mapperforge.ast.sql;

public record BinaryExpression(Expression left, String operator, Expression right)
    implements Expression {}
