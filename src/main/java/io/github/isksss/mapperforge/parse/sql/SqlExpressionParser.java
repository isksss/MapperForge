package io.github.isksss.mapperforge.parse.sql;

import io.github.isksss.mapperforge.ast.sql.ArrayExpression;
import io.github.isksss.mapperforge.ast.sql.BetweenExpression;
import io.github.isksss.mapperforge.ast.sql.BinaryExpression;
import io.github.isksss.mapperforge.ast.sql.CaseExpression;
import io.github.isksss.mapperforge.ast.sql.CastExpression;
import io.github.isksss.mapperforge.ast.sql.ColumnExpression;
import io.github.isksss.mapperforge.ast.sql.ExistsExpression;
import io.github.isksss.mapperforge.ast.sql.Expression;
import io.github.isksss.mapperforge.ast.sql.FunctionExpression;
import io.github.isksss.mapperforge.ast.sql.InExpression;
import io.github.isksss.mapperforge.ast.sql.LiteralExpression;
import io.github.isksss.mapperforge.ast.sql.RowExpression;
import io.github.isksss.mapperforge.ast.sql.UnaryExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SqlExpressionParser {
  private static final Map<String, Integer> PRECEDENCE =
      Map.ofEntries(
          Map.entry("OR", 10),
          Map.entry("AND", 20),
          Map.entry("=", 30),
          Map.entry("!=", 30),
          Map.entry("<>", 30),
          Map.entry(">", 30),
          Map.entry(">=", 30),
          Map.entry("<", 30),
          Map.entry("<=", 30),
          Map.entry("+", 40),
          Map.entry("-", 40),
          Map.entry("*", 50),
          Map.entry("/", 50),
          Map.entry("%", 50));

  private final String sql;
  private final List<Token> tokens;
  private int current;

  public SqlExpressionParser(String sql) {
    this.sql = sql.strip();
    this.tokens = new SqlTokenizer(this.sql).tokenize();
  }

  public Expression parse() {
    try {
      return parseExpression(0);
    } catch (RuntimeException e) {
      return new UnknownExpression(sql);
    }
  }

  private Expression parseExpression(int minPrecedence) {
    Expression left = parsePrefix();
    while (!isAtEnd()) {
      if (matchKeyword("BETWEEN")) {
        Expression lower = parseExpression(PRECEDENCE.get("AND") + 1);
        if (!matchKeyword("AND")) {
          return new UnknownExpression(sql);
        }
        Expression upper = parseExpression(0);
        left = new BetweenExpression(left, lower, upper);
        continue;
      }
      if (matchKeyword("IN")) {
        left = new InExpression(left, parseParenthesizedExpressionList());
        continue;
      }
      String operator = peekOperator();
      int precedence = PRECEDENCE.getOrDefault(operator, -1);
      if (precedence < minPrecedence) {
        break;
      }
      advanceOperator(operator);
      Expression right = parseExpression(precedence + 1);
      left = new BinaryExpression(left, operator, right);
    }
    return left;
  }

  private Expression parsePrefix() {
    if (matchKeyword("NOT") || matchSymbol("-") || matchSymbol("+")) {
      return new UnaryExpression(previous().text().toUpperCase(Locale.ROOT), parseExpression(60));
    }
    if (matchKeyword("CASE")) {
      return parseCase();
    }
    if (matchKeyword("EXISTS")) {
      return new ExistsExpression(parseParenthesizedRaw());
    }
    if (matchKeyword("CAST")) {
      return parseCast();
    }
    if (matchKeyword("ARRAY")) {
      return new ArrayExpression(parseBracketedExpressionList());
    }
    if (matchKeyword("ROW")) {
      return new RowExpression(parseParenthesizedExpressionList());
    }
    if (matchSymbol("(")) {
      Expression expression = parseExpression(0);
      consumeSymbol(")");
      return expression;
    }
    if (match(TokenType.PLACEHOLDER)) {
      return new PlaceholderParser().parse(previous().text());
    }
    if (match(TokenType.STRING)) {
      return new LiteralExpression(unquote(previous().text()));
    }
    if (match(TokenType.NUMBER)) {
      return new LiteralExpression(previous().text());
    }
    if (match(TokenType.IDENTIFIER) || match(TokenType.KEYWORD)) {
      Token name = previous();
      if (matchSymbol("(")) {
        return new FunctionExpression(name.text(), parseExpressionListUntil(")"));
      }
      return new ColumnExpression(name.text());
    }
    return new UnknownExpression(sql);
  }

  private CaseExpression parseCase() {
    List<CaseExpression.WhenClause> whenClauses = new ArrayList<>();
    Expression elseExpression = null;
    while (matchKeyword("WHEN")) {
      Expression condition = parseExpression(0);
      consumeKeyword("THEN");
      Expression result = parseExpression(0);
      whenClauses.add(new CaseExpression.WhenClause(condition, result));
    }
    if (matchKeyword("ELSE")) {
      elseExpression = parseExpression(0);
    }
    consumeKeyword("END");
    return new CaseExpression(whenClauses, elseExpression);
  }

  private CastExpression parseCast() {
    consumeSymbol("(");
    Expression expression = parseExpression(0);
    consumeKeyword("AS");
    StringBuilder typeName = new StringBuilder();
    while (!checkSymbol(")") && !isAtEnd()) {
      if (!typeName.isEmpty()) {
        typeName.append(" ");
      }
      typeName.append(advance().text());
    }
    consumeSymbol(")");
    return new CastExpression(expression, typeName.toString());
  }

  private List<Expression> parseParenthesizedExpressionList() {
    consumeSymbol("(");
    return parseExpressionListUntil(")");
  }

  private List<Expression> parseBracketedExpressionList() {
    consumeSymbol("[");
    return parseExpressionListUntil("]");
  }

  private List<Expression> parseExpressionListUntil(String endSymbol) {
    List<Expression> expressions = new ArrayList<>();
    if (matchSymbol(endSymbol)) {
      return expressions;
    }
    do {
      expressions.add(parseExpression(0));
    } while (matchSymbol(","));
    consumeSymbol(endSymbol);
    return List.copyOf(expressions);
  }

  private String parseParenthesizedRaw() {
    consumeSymbol("(");
    int depth = 1;
    int startOffset = peek().range().start().offset();
    int endOffset = startOffset;
    while (!isAtEnd() && depth > 0) {
      Token token = advance();
      if (token.type() == TokenType.SYMBOL && "(".equals(token.text())) {
        depth++;
      } else if (token.type() == TokenType.SYMBOL && ")".equals(token.text())) {
        depth--;
        if (depth == 0) {
          endOffset = token.range().start().offset();
          break;
        }
      }
    }
    return sql.substring(startOffset, endOffset).strip();
  }

  private String peekOperator() {
    if (check(TokenType.KEYWORD) && PRECEDENCE.containsKey(peek().text())) {
      return peek().text();
    }
    if (!check(TokenType.SYMBOL)) {
      return "";
    }
    String first = peek().text();
    if (current + 1 < tokens.size()) {
      String second = tokens.get(current + 1).text();
      String combined = first + second;
      if (PRECEDENCE.containsKey(combined)) {
        return combined;
      }
    }
    return PRECEDENCE.containsKey(first) ? first : "";
  }

  private void advanceOperator(String operator) {
    advance();
    if (operator.length() == 2) {
      advance();
    }
  }

  private boolean match(TokenType type) {
    if (!check(type)) {
      return false;
    }
    advance();
    return true;
  }

  private boolean matchKeyword(String keyword) {
    if (!check(TokenType.KEYWORD) || !keyword.equals(peek().text())) {
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

  private void consumeKeyword(String keyword) {
    if (!matchKeyword(keyword)) {
      throw new IllegalArgumentException("Expected keyword: " + keyword);
    }
  }

  private void consumeSymbol(String symbol) {
    if (!matchSymbol(symbol)) {
      throw new IllegalArgumentException("Expected symbol: " + symbol);
    }
  }

  private boolean check(TokenType type) {
    return !isAtEnd() && peek().type() == type;
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

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private boolean isAtEnd() {
    return peek().type() == TokenType.EOF;
  }

  private String unquote(String value) {
    if (value.length() >= 2
        && (value.startsWith("'") && value.endsWith("'")
            || value.startsWith("\"") && value.endsWith("\""))) {
      return value.substring(1, value.length() - 1);
    }
    return value;
  }
}
