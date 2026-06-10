package io.github.isksss.mapperforge.print;

import java.util.List;

/**
 * 複数の document node を順に描画する node です。
 *
 * @param contents 描画順に並べた子 node
 */
public record ConcatDoc(List<Doc> contents) implements Doc {
  /** 子 node list を防御コピーします。 */
  public ConcatDoc {
    contents = List.copyOf(contents);
  }
}
