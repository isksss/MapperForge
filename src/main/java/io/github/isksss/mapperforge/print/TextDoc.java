package io.github.isksss.mapperforge.print;

public record TextDoc(String text) implements Doc {
  public TextDoc {
    text = text == null ? "" : text;
  }
}
