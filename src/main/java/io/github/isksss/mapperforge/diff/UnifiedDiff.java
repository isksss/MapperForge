package io.github.isksss.mapperforge.diff;

import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 2 つの source file から unified diff 形式のテキスト差分を生成します。
 *
 * <p>MapperForge の dry-run や公開 facade で、実際に変更される行を表示するために使います。
 */
public final class UnifiedDiff {
  /** Unified diff 生成器を作成します。 */
  public UnifiedDiff() {}

  /**
   * 2 つの source file の unified diff を生成します。
   *
   * @param before 比較元
   * @param after 比較先
   * @return unified diff。内容が同一の場合は空文字
   */
  public String create(SourceFile before, SourceFile after) {
    if (before.content().equals(after.content())) {
      return "";
    }

    List<String> beforeLines = lines(before.content());
    List<String> afterLines = lines(after.content());
    List<Edit> edits = edits(beforeLines, afterLines);

    StringBuilder out = new StringBuilder();
    out.append("--- ").append(before.fileName()).append('\n');
    out.append("+++ ").append(after.fileName()).append('\n');
    out.append("@@ -1,")
        .append(beforeLines.size())
        .append(" +1,")
        .append(afterLines.size())
        .append(" @@\n");
    for (Edit edit : edits) {
      out.append(edit.marker()).append(edit.line()).append('\n');
    }
    return out.toString();
  }

  private List<String> lines(String content) {
    if (content.isEmpty()) {
      return List.of();
    }
    String normalized =
        content.endsWith("\n") ? content.substring(0, content.length() - 1) : content;
    if (normalized.isEmpty()) {
      return List.of("");
    }
    return normalized.lines().toList();
  }

  private List<Edit> edits(List<String> before, List<String> after) {
    int[][] lengths = longestCommonSubsequenceLengths(before, after);
    List<Edit> result = new ArrayList<>();
    int i = 0;
    int j = 0;
    while (i < before.size() && j < after.size()) {
      if (before.get(i).equals(after.get(j))) {
        result.add(new Edit(' ', before.get(i)));
        i++;
        j++;
      } else if (lengths[i + 1][j] >= lengths[i][j + 1]) {
        result.add(new Edit('-', before.get(i)));
        i++;
      } else {
        result.add(new Edit('+', after.get(j)));
        j++;
      }
    }
    while (i < before.size()) {
      result.add(new Edit('-', before.get(i)));
      i++;
    }
    while (j < after.size()) {
      result.add(new Edit('+', after.get(j)));
      j++;
    }
    return result;
  }

  private int[][] longestCommonSubsequenceLengths(List<String> before, List<String> after) {
    int[][] lengths = new int[before.size() + 1][after.size() + 1];
    for (int i = before.size() - 1; i >= 0; i--) {
      for (int j = after.size() - 1; j >= 0; j--) {
        if (before.get(i).equals(after.get(j))) {
          lengths[i][j] = lengths[i + 1][j + 1] + 1;
        } else {
          lengths[i][j] = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
        }
      }
    }
    return lengths;
  }

  private record Edit(char marker, String line) {}
}
