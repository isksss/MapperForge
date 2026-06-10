package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.source.Range;

public record ValidationError(ErrorType type, String message, Range location) {}
