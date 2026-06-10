package io.github.isksss.mapperforge.source;

/**
 * MapperForge が処理する入力ソースを表します。
 *
 * @param fileName 診断や差分表示に使うファイル名
 * @param content ファイル内容
 */
public record SourceFile(String fileName, String content) {}
