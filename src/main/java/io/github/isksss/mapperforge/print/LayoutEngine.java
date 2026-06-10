package io.github.isksss.mapperforge.print;

import io.github.isksss.mapperforge.config.FormatterConfig;

/** Doc Tree を {@link FormatterConfig} に従って文字列へ描画します。 */
public final class LayoutEngine {
  private LayoutEngine() {}

  /**
   * Doc Tree を文字列へ描画します。
   *
   * @param doc 描画する Doc Tree
   * @param config indent 幅、最大行長、改行コードを含む formatter 設定
   * @return 描画後の文字列
   */
  public static String render(Doc doc, FormatterConfig config) {
    Renderer renderer =
        new Renderer(config.lineEnding(), Math.max(0, config.indentSize()), config.maxLineLength());
    renderer.render(doc, 0, false);
    return renderer.output();
  }

  private static final class Renderer {
    private static final int UNFIT = Integer.MAX_VALUE;

    private final String lineEnding;
    private final int indentSize;
    private final int maxLineLength;
    private final StringBuilder output = new StringBuilder();
    private int column;

    private Renderer(String lineEnding, int indentSize, int maxLineLength) {
      this.lineEnding = lineEnding;
      this.indentSize = indentSize;
      this.maxLineLength = maxLineLength;
    }

    private String output() {
      return output.toString();
    }

    private void render(Doc doc, int indent, boolean flat) {
      switch (doc) {
        case TextDoc textDoc -> append(textDoc.text());
        case LineDoc ignored -> renderLine(indent, flat, " ");
        case SoftLineDoc ignored -> renderLine(indent, flat, "");
        case HardLineDoc ignored -> newline(indent);
        case IndentDoc indentDoc -> render(indentDoc.contents(), indent + indentSize, flat);
        case GroupDoc groupDoc ->
            render(groupDoc.contents(), indent, flat || fits(groupDoc.contents()));
        case ConcatDoc concatDoc -> {
          for (Doc child : concatDoc.contents()) {
            render(child, indent, flat);
          }
        }
      }
    }

    private void renderLine(int indent, boolean flat, String flatText) {
      if (flat) {
        append(flatText);
        return;
      }
      newline(indent);
    }

    private void newline(int indent) {
      output.append(lineEnding);
      output.append(" ".repeat(Math.max(0, indent)));
      column = indent;
    }

    private void append(String text) {
      output.append(text);
      column += text.length();
    }

    private boolean fits(Doc doc) {
      if (maxLineLength <= 0) {
        return false;
      }
      int width = flatWidth(doc);
      return width != UNFIT && column + width <= maxLineLength;
    }

    private int flatWidth(Doc doc) {
      return switch (doc) {
        case TextDoc textDoc -> textDoc.text().length();
        case LineDoc ignored -> 1;
        case SoftLineDoc ignored -> 0;
        case HardLineDoc ignored -> UNFIT;
        case IndentDoc indentDoc -> flatWidth(indentDoc.contents());
        case GroupDoc groupDoc -> flatWidth(groupDoc.contents());
        case ConcatDoc concatDoc -> {
          int width = 0;
          for (Doc child : concatDoc.contents()) {
            int childWidth = flatWidth(child);
            if (childWidth == UNFIT || width > UNFIT - childWidth) {
              yield UNFIT;
            }
            width += childWidth;
          }
          yield width;
        }
      };
    }
  }
}
