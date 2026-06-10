package io.github.isksss.mapperforge.print;

import java.util.List;

public record ConcatDoc(List<Doc> contents) implements Doc {
  public ConcatDoc {
    contents = List.copyOf(contents);
  }
}
