package io.github.isksss.mapperforge;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.diff.UnifiedDiff;
import io.github.isksss.mapperforge.format.MapperXmlFormatter;
import io.github.isksss.mapperforge.source.SourceFile;
import io.github.isksss.mapperforge.validation.ValidationResult;
import io.github.isksss.mapperforge.validation.Validator;

public final class MapperForge {
  private final MapperXmlFormatter formatter;
  private final Validator validator;
  private final UnifiedDiff diff;

  public MapperForge() {
    this(new MapperXmlFormatter(), new Validator(), new UnifiedDiff());
  }

  MapperForge(MapperXmlFormatter formatter, Validator validator, UnifiedDiff diff) {
    this.formatter = formatter;
    this.validator = validator;
    this.diff = diff;
  }

  public String format(SourceFile source, FormatterConfig config) {
    return formatter.format(source, config);
  }

  public boolean isFormatted(SourceFile source, FormatterConfig config) {
    return source.content().equals(format(source, config));
  }

  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    return validator.validate(before, after, config);
  }

  public String diff(SourceFile before, SourceFile after, FormatterConfig config) {
    return diff.create(before, after);
  }
}
