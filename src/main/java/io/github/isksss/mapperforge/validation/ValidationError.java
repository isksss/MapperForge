package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.error.ErrorCode;
import io.github.isksss.mapperforge.source.Range;

/**
 * Validation で検出した 1 件の問題を表します。
 *
 * @param code 上位の error code
 * @param type validation 差分の分類
 * @param message 人間向けの説明
 * @param location 問題に対応する source range。特定できない場合は {@code null}
 */
public record ValidationError(ErrorCode code, ErrorType type, String message, Range location) {
  /**
   * 互換用に {@link ErrorCode#VALIDATION_ERROR} を使って validation error を生成します。
   *
   * @param type validation 差分の分類
   * @param message 人間向けの説明
   * @param location 問題に対応する source range。特定できない場合は {@code null}
   */
  public ValidationError(ErrorType type, String message, Range location) {
    this(ErrorCode.VALIDATION_ERROR, type, message, location);
  }
}
