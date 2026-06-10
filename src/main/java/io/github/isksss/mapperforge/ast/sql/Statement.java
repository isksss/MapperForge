package io.github.isksss.mapperforge.ast.sql;

public sealed interface Statement extends SqlNode
    permits SelectStatement,
        InsertStatement,
        UpdateStatement,
        DeleteStatement,
        WithStatement,
        SetOperationStatement,
        UnknownStatement {}
