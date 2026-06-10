package io.github.isksss.mapperforge.report;

import io.github.isksss.mapperforge.source.Range;
import io.github.isksss.mapperforge.validation.ValidationError;
import io.github.isksss.mapperforge.validation.ValidationResult;
import java.nio.file.Path;
import java.util.stream.Collectors;

/** ValidationResult を人間が読める report 文字列へ変換します。 */
public final class ValidationReportFormatter {
  /**
   * 対象 file の validation result を report 文字列へ変換します。
   *
   * @param file validation 対象 file
   * @param validation validation result
   * @return report 文字列
   */
  public String format(Path file, ValidationResult validation) {
    if (validation.success()) {
      return "MapperForge validation passed: " + file;
    }
    if (validation.errors().isEmpty()) {
      return "MapperForge validation failed: " + file + " [UNKNOWN]";
    }
    return validation.errors().stream()
        .map(this::formatError)
        .collect(Collectors.joining("\n", "MapperForge validation failed: " + file + "\n", ""));
  }

  private String formatError(ValidationError error) {
    String location = formatLocation(error.location());
    if (location.isEmpty()) {
      return "- " + error.code() + "/" + error.type() + ": " + error.message();
    }
    return "- " + error.code() + "/" + error.type() + " at " + location + ": " + error.message();
  }

  private String formatLocation(Range location) {
    if (location == null) {
      return "";
    }
    return location.start().line()
        + ":"
        + location.start().column()
        + "-"
        + location.end().line()
        + ":"
        + location.end().column();
  }
}
