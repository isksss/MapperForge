package io.github.isksss.mapperforge.format;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;

public final class FormatterContext {
  private FormatterConfig config;
  private SourceFile source;

  public FormatterContext(FormatterConfig config, SourceFile source) {
    this.config = config;
    this.source = source;
  }

  public FormatterConfig config() {
    return config;
  }

  public void setConfig(FormatterConfig config) {
    this.config = config;
  }

  public SourceFile source() {
    return source;
  }

  public void setSource(SourceFile source) {
    this.source = source;
  }
}
