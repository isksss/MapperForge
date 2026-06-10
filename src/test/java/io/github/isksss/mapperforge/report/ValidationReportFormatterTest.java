package io.github.isksss.mapperforge.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.error.ErrorCode;
import io.github.isksss.mapperforge.source.Position;
import io.github.isksss.mapperforge.source.Range;
import io.github.isksss.mapperforge.validation.ErrorType;
import io.github.isksss.mapperforge.validation.ValidationError;
import io.github.isksss.mapperforge.validation.ValidationResult;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

final class ValidationReportFormatterTest {
  private final ValidationReportFormatter formatter = new ValidationReportFormatter();

  @Test
  void formatsSuccessfulValidation() {
    assertEquals(
        "MapperForge validation passed: src/main/resources/UserMapper.xml",
        formatter.format(Path.of("src/main/resources/UserMapper.xml"), ValidationResult.ok()));
  }

  @Test
  void formatsFailedValidationWithoutErrors() {
    assertEquals(
        "MapperForge validation failed: src/main/resources/UserMapper.xml [UNKNOWN]",
        formatter.format(
            Path.of("src/main/resources/UserMapper.xml"), new ValidationResult(false, List.of())));
  }

  @Test
  void formatsValidationErrorsWithLocation() {
    ValidationResult result =
        new ValidationResult(
            false,
            List.of(
                new ValidationError(
                    ErrorCode.VALIDATION_ERROR,
                    ErrorType.STATEMENT,
                    "Formatted SQL statement sequence changed",
                    new Range(new Position(10, 2, 5), new Position(16, 2, 11))),
                new ValidationError(
                    ErrorCode.OGNL_ERROR,
                    ErrorType.EXPRESSION,
                    "Formatted OGNL expression sequence changed",
                    null)));

    assertEquals(
        """
        MapperForge validation failed: src/main/resources/UserMapper.xml
        - VALIDATION_ERROR/STATEMENT at 2:5-2:11: Formatted SQL statement sequence changed
        - OGNL_ERROR/EXPRESSION: Formatted OGNL expression sequence changed\
        """,
        formatter.format(Path.of("src/main/resources/UserMapper.xml"), result));
  }
}
