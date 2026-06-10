package io.github.isksss.mapperforge.rule;

import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.format.FormatterContext;
import java.util.List;

/** Mapper AST に固定順序の整形ルールを適用するパイプラインです。 */
public final class FormatterRulePipeline {
  private final List<FormatterRule> rules;

  /**
   * 指定されたルール列でパイプラインを作成します。
   *
   * @param rules 適用順序どおりに並んだ整形ルール
   */
  public FormatterRulePipeline(List<FormatterRule> rules) {
    this.rules = List.copyOf(rules);
  }

  /**
   * v1 仕様の固定ルールパイプラインを作成します。
   *
   * @return Normalize、AttributeOrder、OGNL、SQL、Wrap の順で適用するパイプライン
   */
  public static FormatterRulePipeline v1() {
    return new FormatterRulePipeline(
        List.of(
            new NormalizeRule(),
            new AttributeOrderRule(),
            new OgnlFormatRule(),
            new SqlFormatRule(),
            new WrapRule()));
  }

  /**
   * パイプライン内のルールを順番に適用します。
   *
   * @param node 対象の Mapper AST ノード
   * @param context 整形設定と入力ソースを持つコンテキスト
   * @return すべてのルールを適用した Mapper AST ノード
   */
  public MapperNode apply(MapperNode node, FormatterContext context) {
    MapperNode current = node;
    for (FormatterRule rule : rules) {
      current = rule.apply(current, context);
    }
    return current;
  }
}
