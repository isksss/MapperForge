package io.github.isksss.mapperforge.gradle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;

/** Gradle DSL の {@code mapperForge { ... }} で使う設定拡張です。 */
public abstract class MapperForgeExtension {
  /** Gradle が extension instance を生成するための constructor です。 */
  public MapperForgeExtension() {}

  /**
   * 対象 SQL dialect を設定します。
   *
   * @return 対象 SQL dialect
   */
  public abstract Property<String> getDialect();

  /**
   * formatter の SemVer バージョンを設定します。
   *
   * @return formatter の SemVer バージョン
   */
  public abstract Property<String> getFormatterVersion();

  /**
   * 処理対象ファイルの include glob を設定します。
   *
   * @return include glob の一覧
   */
  public abstract ListProperty<String> getInclude();

  /**
   * 処理対象から除外する exclude glob を設定します。
   *
   * @return exclude glob の一覧
   */
  public abstract ListProperty<String> getExclude();

  /**
   * インデント幅を設定します。
   *
   * @return インデント幅
   */
  public abstract Property<Integer> getIndentSize();

  /**
   * 最大行長を設定します。
   *
   * @return 最大行長
   */
  public abstract Property<Integer> getMaxLineLength();

  /**
   * 出力改行コードを設定します。
   *
   * @return 出力改行コード
   */
  public abstract Property<String> getLineEnding();

  /**
   * SQL formatter の出力スタイルを設定します。
   *
   * @return SQL formatter の出力スタイル
   */
  public abstract Property<String> getSqlFormatStyle();

  /**
   * SQL printer 実装を設定します。
   *
   * @return SQL printer 実装
   */
  public abstract Property<String> getSqlPrinter();

  /**
   * XML タグの折り返し方針を設定します。
   *
   * @return XML タグの折り返し方針
   */
  public abstract Property<String> getTagWrapStyle();

  /**
   * XML 属性の配置方針を設定します。
   *
   * @return XML 属性の配置方針
   */
  public abstract Property<String> getAttributeLayout();

  /**
   * 空白ノードを意味検証対象として保持するかを設定します。
   *
   * @return 空白ノードを保持する場合は {@code true}
   */
  public abstract Property<Boolean> getPreserveWhitespace();

  /**
   * CDATA ラッパーを保持するかを設定します。
   *
   * @return CDATA ラッパーを保持する場合は {@code true}
   */
  public abstract Property<Boolean> getPreserveCdata();

  /**
   * CDATA 内 SQL を整形するかを設定します。
   *
   * @return CDATA 内 SQL を整形する場合は {@code true}
   */
  public abstract Property<Boolean> getFormatSqlInsideCdata();

  /**
   * 検証失敗時にタスクを失敗させるかを設定します。
   *
   * @return 検証失敗時にタスクを失敗させる場合は {@code true}
   */
  public abstract Property<Boolean> getStrict();

  /**
   * タグごとの属性順序設定を返します。
   *
   * @return タグ名から属性順序への mapping
   */
  public abstract MapProperty<String, List<String>> getAttributeOrder();

  /**
   * 指定タグの属性順序を追加または上書きします。
   *
   * @param tagName 対象タグ名
   * @param order 優先して並べる属性名
   */
  public void attributeOrder(String tagName, List<String> order) {
    Map<String, List<String>> current =
        new LinkedHashMap<>(getAttributeOrder().getOrElse(Map.of()));
    current.put(tagName, List.copyOf(order));
    getAttributeOrder().set(current);
  }
}
