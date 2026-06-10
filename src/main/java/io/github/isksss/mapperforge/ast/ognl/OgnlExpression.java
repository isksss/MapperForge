package io.github.isksss.mapperforge.ast.ognl;

public sealed interface OgnlExpression
    permits OgnlBinaryExpression,
        OgnlCallExpression,
        OgnlCollectionExpression,
        OgnlLiteralExpression,
        OgnlNameExpression,
        OgnlUnaryExpression,
        OgnlUnknownExpression {}
