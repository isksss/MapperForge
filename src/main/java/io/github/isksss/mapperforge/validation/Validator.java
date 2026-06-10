package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {
  private static final Pattern PLACEHOLDER = Pattern.compile("[$#]\\{[^}]+}");
  private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
  private static final Pattern CDATA = Pattern.compile("<!\\[CDATA\\[.*?]]>", Pattern.DOTALL);

  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    List<ValidationError> errors = new ArrayList<>();
    compare(
        "placeholder",
        PLACEHOLDER,
        before.content(),
        after.content(),
        ErrorType.PLACEHOLDER,
        errors);
    compare("comment", COMMENT, before.content(), after.content(), ErrorType.COMMENT, errors);
    if (config.preserveCdata()) {
      compare("CDATA", CDATA, before.content(), after.content(), ErrorType.CDATA, errors);
    }
    return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
  }

  private void compare(
      String name,
      Pattern pattern,
      String before,
      String after,
      ErrorType type,
      List<ValidationError> errors) {
    List<String> beforeValues = find(pattern, before);
    List<String> afterValues = find(pattern, after);
    if (!beforeValues.equals(afterValues)) {
      errors.add(new ValidationError(type, "Formatted XML changed " + name + " sequence", null));
    }
  }

  private List<String> find(Pattern pattern, String value) {
    Matcher matcher = pattern.matcher(value);
    List<String> values = new ArrayList<>();
    while (matcher.find()) {
      values.add(matcher.group());
    }
    return values;
  }
}
