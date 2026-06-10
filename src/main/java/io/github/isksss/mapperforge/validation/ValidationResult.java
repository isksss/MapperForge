package io.github.isksss.mapperforge.validation;

import java.util.List;

/**
 * Validation 全体の結果を表します。
 *
 * @param success validation が成功した場合は {@code true}
 * @param errors 検出した validation error。生成時に不変コピーされます
 */
public record ValidationResult(boolean success, List<ValidationError> errors) {
  /** Validation error のリストを不変コピーして生成します。 */
  public ValidationResult {
    errors = List.copyOf(errors);
  }

  /**
   * 成功結果を返します。
   *
   * @return error を持たない成功結果
   */
  public static ValidationResult ok() {
    return new ValidationResult(true, List.of());
  }
}
