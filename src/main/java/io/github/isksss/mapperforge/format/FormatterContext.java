package io.github.isksss.mapperforge.format;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;

/**
 * formatter rule 実行中に共有する context です。
 *
 * <p>Rule pipeline 内で設定や source を差し替えられるように可変 object として扱います。
 */
public final class FormatterContext {
  private FormatterConfig config;
  private SourceFile source;

  /**
   * formatter context を作成します。
   *
   * @param config formatter 設定
   * @param source 処理対象 source
   */
  public FormatterContext(FormatterConfig config, SourceFile source) {
    this.config = config;
    this.source = source;
  }

  /**
   * 現在の formatter 設定を返します。
   *
   * @return formatter 設定
   */
  public FormatterConfig config() {
    return config;
  }

  /**
   * formatter 設定を差し替えます。
   *
   * @param config 新しい formatter 設定
   */
  public void setConfig(FormatterConfig config) {
    this.config = config;
  }

  /**
   * 現在の処理対象 source を返します。
   *
   * @return 処理対象 source
   */
  public SourceFile source() {
    return source;
  }

  /**
   * 処理対象 source を差し替えます。
   *
   * @param source 新しい処理対象 source
   */
  public void setSource(SourceFile source) {
    this.source = source;
  }
}
