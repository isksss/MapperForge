package io.github.isksss.mapperforge.validation;

import java.util.List;

public record ValidationResult(boolean success, List<ValidationError> errors) {
  public static ValidationResult ok() {
    return new ValidationResult(true, List.of());
  }
}
