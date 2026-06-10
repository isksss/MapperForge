package io.github.isksss.mapperforge.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.error.ErrorCode;
import org.junit.jupiter.api.Test;

final class ValidationErrorTest {
  @Test
  void defaultsToValidationErrorCodeForCompatibilityConstructor() {
    ValidationError error = new ValidationError(ErrorType.XML, "message", null);

    assertEquals(ErrorCode.VALIDATION_ERROR, error.code());
  }

  @Test
  void keepsExplicitErrorCode() {
    ValidationError error =
        new ValidationError(ErrorCode.OGNL_ERROR, ErrorType.EXPRESSION, "message", null);

    assertEquals(ErrorCode.OGNL_ERROR, error.code());
  }
}
