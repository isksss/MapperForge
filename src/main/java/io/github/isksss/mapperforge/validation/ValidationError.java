package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.error.ErrorCode;
import io.github.isksss.mapperforge.source.Range;

public record ValidationError(ErrorCode code, ErrorType type, String message, Range location) {
  public ValidationError(ErrorType type, String message, Range location) {
    this(ErrorCode.VALIDATION_ERROR, type, message, location);
  }
}
