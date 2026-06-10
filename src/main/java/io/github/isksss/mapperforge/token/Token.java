package io.github.isksss.mapperforge.token;

import io.github.isksss.mapperforge.source.Range;

public record Token(TokenType type, String text, Range range) {}
