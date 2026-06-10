package io.github.isksss.mapperforge.ast.sql;

public sealed interface Expression extends SqlNode
    permits ArrayExpression,
        BetweenExpression,
        BinaryExpression,
        CaseExpression,
        CastExpression,
        ColumnExpression,
        ExistsExpression,
        FunctionExpression,
        InExpression,
        JsonExpression,
        LiteralExpression,
        ParameterExpression,
        PlaceholderExpression,
        RowExpression,
        SubQueryExpression,
        UnaryExpression,
        UnknownExpression,
        WindowExpression {}
