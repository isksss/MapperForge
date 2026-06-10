package io.github.isksss.mapperforge.parse.ognl;

import io.github.isksss.mapperforge.ast.ognl.OgnlBinaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCallExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlLiteralExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlNameExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnknownExpression;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** OGNL expression を recoverable に AST へ変換する Pratt parser です。 */
public final class OgnlExpressionParser {
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
          Map.entry("instanceof", 30));

  private final String expression;
  private final List<Token> tokens;
  private int current;

  /**
   * parser を初期化します。
   *
   * @param expression parse 対象の OGNL expression
   */
  public OgnlExpressionParser(String expression) {
    this.expression = expression.strip();
    this.tokens = new OgnlTokenizer(this.expression).tokenize();
  }

  /**
   * OGNL expression を parse します。
   *
   * @return parse できた AST。未対応または不正な式は {@link OgnlUnknownExpression}
   */
  public OgnlExpression parse() {
    try {
      OgnlExpression parsed = parseExpression(0);
      if (!isAtEnd()) {
        return new OgnlUnknownExpression(expression);
      }
      return parsed;
    } catch (RuntimeException e) {
      return new OgnlUnknownExpression(expression);
    }
  }

  private OgnlExpression parseExpression(int minPrecedence) {
    OgnlExpression left = parsePrefix();
    while (!isAtEnd()) {
      String operator = peekOperator();
      int precedence = PRECEDENCE.getOrDefault(operator, -1);
      if (precedence < minPrecedence) {
        break;
      }
      advance();
      OgnlExpression right = parseExpression(precedence + 1);
      left = new OgnlBinaryExpression(left, operator, right);
    }
    return left;
  }

  private OgnlExpression parsePrefix() {
    if (matchText("not") || matchText("!")) {
      return new OgnlUnaryExpression(previous().text(), parseExpression(40));
    }
    if (matchSymbol("(")) {
      OgnlExpression nested = parseExpression(0);
      consumeSymbol(")");
      return nested;
    }
    if (matchSymbol("{")) {
      return new OgnlCollectionExpression(parseExpressionListUntil("}"));
    }
    if (match(TokenType.STRING)) {
      String raw = previous().text();
      return new OgnlLiteralExpression(unquote(raw), raw);
    }
    if (match(TokenType.NUMBER)) {
      return new OgnlLiteralExpression(previous().text(), previous().text());
    }
    if (match(TokenType.IDENTIFIER)) {
      String name = previous().text();
      if (matchSymbol("(")) {
        return new OgnlCallExpression(name, parseExpressionListUntil(")"));
      }
      if (isLiteralName(name)) {
        return new OgnlLiteralExpression(name, name);
      }
      return new OgnlNameExpression(name);
    }
    return new OgnlUnknownExpression(expression);
  }

  private List<OgnlExpression> parseExpressionListUntil(String endSymbol) {
    List<OgnlExpression> expressions = new ArrayList<>();
    if (matchSymbol(endSymbol)) {
      return expressions;
    }
    do {
      expressions.add(parseExpression(0));
    } while (matchSymbol(","));
    consumeSymbol(endSymbol);
    return List.copyOf(expressions);
  }

  private String peekOperator() {
    if (check(TokenType.SYMBOL) || check(TokenType.IDENTIFIER)) {
      return normalizedOperator(peek().text());
    }
    return "";
  }

  private String normalizedOperator(String text) {
    String lower = text.toLowerCase(Locale.ROOT);
    return PRECEDENCE.containsKey(lower) ? lower : text;
  }

  private boolean isLiteralName(String value) {
    String lower = value.toLowerCase(Locale.ROOT);
    return "null".equals(lower) || "true".equals(lower) || "false".equals(lower);
  }

  private String unquote(String raw) {
    if (raw.length() < 2) {
      return raw;
    }
    return raw.substring(1, raw.length() - 1);
  }

  private boolean match(TokenType type) {
    if (!check(type)) {
      return false;
    }
    advance();
    return true;
  }

  private boolean matchText(String text) {
    if (!text.equalsIgnoreCase(peek().text())) {
      return false;
    }
    advance();
    return true;
  }

  private boolean matchSymbol(String symbol) {
    if (!checkSymbol(symbol)) {
      return false;
    }
    advance();
    return true;
  }

  private void consumeSymbol(String symbol) {
    if (!matchSymbol(symbol)) {
      throw new IllegalArgumentException("Expected symbol " + symbol);
    }
  }

  private boolean check(TokenType type) {
    return peek().type() == type;
  }

  private boolean checkSymbol(String symbol) {
    return check(TokenType.SYMBOL) && symbol.equals(peek().text());
  }

  private Token advance() {
    if (!isAtEnd()) {
      current++;
    }
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type() == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
}
