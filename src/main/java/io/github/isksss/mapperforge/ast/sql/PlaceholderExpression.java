package io.github.isksss.mapperforge.ast.sql;

import java.util.Map;

public record PlaceholderExpression(
    PlaceholderType type, String expression, Map<String, String> options) implements Expression {}
