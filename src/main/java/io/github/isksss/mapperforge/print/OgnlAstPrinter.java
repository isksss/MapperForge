package io.github.isksss.mapperforge.print;

import io.github.isksss.mapperforge.ast.ognl.OgnlBinaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCallExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlLiteralExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlNameExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnknownExpression;
import java.util.Locale;
import java.util.Map;

/**
 * OGNL AST を MapperForge の標準 OGNL 文字列表現へ変換する printer です。
 *
 * <p>operator の前後には安定した空白を入れ、必要な場合だけ precedence 保護用の括弧を出力します。
 */
public final class OgnlAstPrinter {
  private static final Map<String, Integer> PRECEDENCE =
      Map.ofEntries(
          Map.entry("||", 10),
          Map.entry("or", 10),
          Map.entry("&&", 20),
          Map.entry("and", 20),
          Map.entry("==", 30),
          Map.entry("!=", 30),
          Map.entry(">", 30),
          Map.entry(">=", 30),
          Map.entry("<", 30),
          Map.entry("<=", 30),
          Map.entry("in", 30),
          Map.entry("instanceof", 30),
          Map.entry("not", 40),
          Map.entry("!", 40),
          Map.entry("primary", 50));

  /** OGNL AST printer を作成します。 */
  public OgnlAstPrinter() {}

  /**
   * OGNL expression AST を文字列へ変換します。
   *
   * @param expression 出力対象 OGNL expression
   * @return OGNL 文字列表現
   */
  public String print(OgnlExpression expression) {
    return print(expression, 0);
  }

  private String print(OgnlExpression expression, int parentPrecedence) {
    return switch (expression) {
      case OgnlBinaryExpression binary -> printBinary(binary, parentPrecedence);
      case OgnlUnaryExpression unary -> printUnary(unary, parentPrecedence);
      case OgnlCallExpression call ->
          call.name()
              + "("
              + String.join(", ", call.arguments().stream().map(this::print).toList())
              + ")";
      case OgnlCollectionExpression collection ->
          "{" + String.join(",", collection.values().stream().map(this::print).toList()) + "}";
      case OgnlLiteralExpression literal -> literal.raw();
      case OgnlNameExpression name -> name.name();
      case OgnlUnknownExpression unknown -> unknown.raw();
    };
  }

  private String printBinary(OgnlBinaryExpression binary, int parentPrecedence) {
    int precedence = precedence(binary.operator());
    String rendered =
        print(binary.left(), precedence)
            + " "
            + binary.operator()
            + " "
            + print(binary.right(), precedence + 1);
    return parenthesize(rendered, precedence, parentPrecedence);
  }

  private String printUnary(OgnlUnaryExpression unary, int parentPrecedence) {
    int precedence = precedence(unary.operator());
    String rendered = unary.operator() + " " + print(unary.expression(), precedence);
    return parenthesize(rendered, precedence, parentPrecedence);
  }

  private String parenthesize(String rendered, int precedence, int parentPrecedence) {
    if (precedence < parentPrecedence) {
      return "(" + rendered + ")";
    }
    return rendered;
  }

  private int precedence(String operator) {
    return PRECEDENCE.getOrDefault(operator.toLowerCase(Locale.ROOT), PRECEDENCE.get("primary"));
  }
}
