package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.parse.MapperXmlParser;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {
  private static final Pattern PLACEHOLDER = Pattern.compile("[$#]\\{[^}]+}");
  private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
  private static final Pattern CDATA = Pattern.compile("<!\\[CDATA\\[.*?]]>", Pattern.DOTALL);
  private final MapperXmlParser parser = new MapperXmlParser();

  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    List<ValidationError> errors = new ArrayList<>();
    compareAst(before, after, errors);
    compare(
        "placeholder",
        PLACEHOLDER,
        before.content(),
        after.content(),
        ErrorType.PLACEHOLDER,
        errors);
    return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
  }

  private void compareAst(SourceFile before, SourceFile after, List<ValidationError> errors) {
    var beforeAst = parser.parse(before);
    var afterAst = parser.parse(after);
    if (!beforeAst.equals(afterAst)) {
      errors.add(
          new ValidationError(
              classifyAstChange(before.content(), after.content()),
              "Formatted XML changed AST",
              null));
    }
  }

  private ErrorType classifyAstChange(String before, String after) {
    if (!find(COMMENT, before).equals(find(COMMENT, after))) {
      return ErrorType.COMMENT;
    }
    if (!find(CDATA, before).equals(find(CDATA, after))) {
      return ErrorType.CDATA;
    }
    return ErrorType.GENERIC_ELEMENT;
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
