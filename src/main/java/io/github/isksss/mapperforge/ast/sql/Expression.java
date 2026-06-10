package io.github.isksss.mapperforge.ast.sql;

public sealed interface Expression extends SqlNode
    permits UnknownExpression, PlaceholderExpression {}
