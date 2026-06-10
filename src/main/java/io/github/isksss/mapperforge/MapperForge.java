package io.github.isksss.mapperforge;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.diff.AstDiff;
import io.github.isksss.mapperforge.diff.UnifiedDiff;
import io.github.isksss.mapperforge.format.MapperXmlFormatter;
import io.github.isksss.mapperforge.source.SourceFile;
import io.github.isksss.mapperforge.validation.ValidationResult;
import io.github.isksss.mapperforge.validation.Validator;

/** MyBatis Mapper XML の整形、検証、差分生成を提供する公開 API です。 */
public final class MapperForge {
  private final MapperXmlFormatter formatter;
  private final Validator validator;
  private final AstDiff astDiff;
  private final UnifiedDiff diff;

  /** 標準の formatter、validator、diff 実装で MapperForge を作成します。 */
  public MapperForge() {
    this(new MapperXmlFormatter(), new Validator(), new AstDiff(), new UnifiedDiff());
  }

  MapperForge(
      MapperXmlFormatter formatter, Validator validator, AstDiff astDiff, UnifiedDiff diff) {
    this.formatter = formatter;
    this.validator = validator;
    this.astDiff = astDiff;
    this.diff = diff;
  }

  /**
   * Mapper XML を指定設定で整形します。
   *
   * @param source 整形対象のソース
   * @param config 整形設定
   * @return 整形済み XML
   */
  public String format(SourceFile source, FormatterConfig config) {
    return formatter.format(source, config);
  }

  /**
   * Mapper XML が現在の設定で整形済みかを判定します。
   *
   * @param source 判定対象のソース
   * @param config 整形設定
   * @return 整形結果が入力と一致する場合は {@code true}
   */
  public boolean isFormatted(SourceFile source, FormatterConfig config) {
    return source.content().equals(format(source, config));
  }

  /**
   * 整形前後の Mapper XML に意味的な変更がないか検証します。
   *
   * @param before 整形前のソース
   * @param after 整形後のソース
   * @param config 検証に使う整形設定
   * @return 検証結果
   */
  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    return validator.validate(before, after, config);
  }

  /**
   * MapperForge の AST 差分と unified diff を生成します。
   *
   * @param before 差分元のソース
   * @param after 差分先のソース
   * @param config 差分生成時の検証・解析設定
   * @return 差分文字列。差分がない場合は空文字列
   */
  public String diff(SourceFile before, SourceFile after, FormatterConfig config) {
    String ast = astDiff.create(before, after);
    String unified = diff.create(before, after);
    if (ast.isEmpty()) {
      return unified;
    }
    if (unified.isEmpty()) {
      return ast;
    }
    return ast + "\n" + unified;
  }
}
