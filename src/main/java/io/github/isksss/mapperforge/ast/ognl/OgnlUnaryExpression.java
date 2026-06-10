package io.github.isksss.mapperforge.ast.ognl;

public record OgnlUnaryExpression(String operator, OgnlExpression expression)
    implements OgnlExpression {}
