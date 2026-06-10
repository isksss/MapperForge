package io.github.isksss.mapperforge.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.error.ErrorCode;
import java.util.Arrays;
import java.util.List;
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

  @Test
  void errorCodesMatchPlanClassificationOrder() {
    assertEquals(
        List.of(
            "PARSER_ERROR",
            "TOKENIZER_ERROR",
            "OGNL_ERROR",
            "FORMAT_ERROR",
            "VALIDATION_ERROR",
            "CONFIG_ERROR",
            "IO_ERROR",
            "INTERNAL_ERROR"),
        Arrays.stream(ErrorCode.values()).map(Enum::name).toList());
  }

  @Test
  void errorTypesCoverValidationDifferenceKinds() {
    assertEquals(
        List.of(
            "STATEMENT",
            "EXPRESSION",
            "COMMENT",
            "CDATA",
            "WHITESPACE",
            "PLACEHOLDER",
            "GENERIC_ELEMENT",
            "XML"),
        Arrays.stream(ErrorType.values()).map(Enum::name).toList());
  }
}
