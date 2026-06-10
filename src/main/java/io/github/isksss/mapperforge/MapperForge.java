package io.github.isksss.mapperforge;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.format.MapperXmlFormatter;
import io.github.isksss.mapperforge.source.SourceFile;
import io.github.isksss.mapperforge.validation.ValidationResult;
import io.github.isksss.mapperforge.validation.Validator;

public final class MapperForge {
  private final MapperXmlFormatter formatter;
  private final Validator validator;

  public MapperForge() {
    this(new MapperXmlFormatter(), new Validator());
  }

  MapperForge(MapperXmlFormatter formatter, Validator validator) {
    this.formatter = formatter;
    this.validator = validator;
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
}
