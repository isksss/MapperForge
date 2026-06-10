package io.github.isksss.mapperforge.ast.ognl;

public record OgnlBinaryExpression(OgnlExpression left, String operator, OgnlExpression right)
    implements OgnlExpression {}
