package io.github.isksss.mapperforge.error;

/** MapperForge 全体で使う上位エラー分類です。 */
public enum ErrorCode {
  /** XML や Mapper AST の解析に失敗したことを表します。 */
  PARSER_ERROR,
  /** SQL や OGNL などの字句解析に失敗したことを表します。 */
  TOKENIZER_ERROR,
  /** OGNL の解析または整形に失敗したことを表します。 */
  OGNL_ERROR,
  /** Mapper XML の整形に失敗したことを表します。 */
  FORMAT_ERROR,
  /** 整形前後の意味検証に失敗したことを表します。 */
  VALIDATION_ERROR,
  /** Gradle DSL や mapperforge.yml の設定が不正であることを表します。 */
  CONFIG_ERROR,
  /** ファイルや外部リソースの入出力に失敗したことを表します。 */
  IO_ERROR,
  /** 想定外の内部エラーを表します。 */
  INTERNAL_ERROR
}
