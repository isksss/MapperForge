package io.github.isksss.mapperforge.diff;

import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.parse.MapperXmlParser;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Mapper XML を AST descriptor として比較する差分生成器です。
 *
 * <p>整形だけの差分は空文字を返し、属性、SQL、テキスト、CDATA、コメントの構造的な差分だけを `# AST Diff` 形式で返します。
 */
public final class AstDiff {
  private final MapperXmlParser parser = new MapperXmlParser();

  /** AST 差分生成器を作成します。 */
  public AstDiff() {}

  /**
   * 2 つの Mapper XML の AST descriptor 差分を生成します。
   *
   * @param before 比較元
   * @param after 比較先
   * @return 構造差分。構造差分がない場合は空文字
   */
  public String create(SourceFile before, SourceFile after) {
    List<String> beforeDescriptors = descriptors(parser.parse(before));
    List<String> afterDescriptors = descriptors(parser.parse(after));
    if (beforeDescriptors.equals(afterDescriptors)) {
      return "";
    }

    StringBuilder out = new StringBuilder("# AST Diff\n");
    for (Edit edit : edits(beforeDescriptors, afterDescriptors)) {
      if (edit.marker() != ' ') {
        out.append(edit.marker()).append(' ').append(edit.line()).append('\n');
      }
    }
    return out.toString();
  }

  private List<String> descriptors(MapperNode node) {
    List<String> descriptors = new ArrayList<>();
    collect(node, "/" + tagName(node) + "[0]", descriptors);
    return List.copyOf(descriptors);
  }

  private void collect(MapperNode node, String path, List<String> descriptors) {
    switch (node) {
      case ElementNode element -> {
        element.attributes().stream()
            .sorted(Comparator.comparing(AttributeNode::name))
            .map(attribute -> path + " @" + attribute.name() + "=" + attribute.value())
            .forEach(descriptors::add);
        List<MapperNode> children = element.children();
        for (int index = 0; index < children.size(); index++) {
          MapperNode child = children.get(index);
          if (child instanceof ElementNode childElement) {
            collect(child, path + "/" + childElement.tagName() + "[" + index + "]", descriptors);
          } else {
            collect(child, path, descriptors);
          }
        }
      }
      case TextNode text when text.type() == TextType.SQL -> {
        String normalized = normalizeSql(text.value());
        if (!normalized.isEmpty()) {
          descriptors.add(path + " sql: " + normalized);
        }
      }
      case TextNode text when text.type() == TextType.PLAIN_TEXT -> {
        String normalized = normalizeText(text.value());
        if (!normalized.isEmpty()) {
          descriptors.add(path + " text: " + normalized);
        }
      }
      case CDataNode cdata -> descriptors.add(path + " cdata: " + normalizeSql(cdata.raw()));
      case CommentNode comment ->
          descriptors.add(
              path + " comment(" + comment.type() + "): " + normalizeText(comment.content()));
      default -> {}
    }
  }

  private String tagName(MapperNode node) {
    if (node instanceof ElementNode element) {
      return element.tagName();
    }
    return "node";
  }

  private String normalizeSql(String raw) {
    return raw.strip().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
  }

  private String normalizeText(String raw) {
    return raw.strip().replaceAll("\\s+", " ");
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
