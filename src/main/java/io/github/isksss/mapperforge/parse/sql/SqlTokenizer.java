package io.github.isksss.mapperforge.parse.sql;

import io.github.isksss.mapperforge.source.Position;
import io.github.isksss.mapperforge.source.Range;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class SqlTokenizer {
  private static final Set<String> KEYWORDS =
      Set.of(
          "SELECT",
          "INSERT",
          "INTO",
          "UPDATE",
          "DELETE",
          "FROM",
          "WITH",
          "WHERE",
          "AND",
          "OR",
          "UNION",
          "INTERSECT",
          "EXCEPT",
          "VALUES",
          "SET",
          "AS",
          "CASE",
          "WHEN",
          "THEN",
          "ELSE",
          "END",
          "EXISTS",
          "IN",
          "BETWEEN",
          "CAST",
          "NOT",
          "ARRAY",
          "ROW",
          "OVER",
          "WINDOW",
          "PARTITION",
          "GROUP",
          "HAVING",
          "ORDER",
          "BY",
          "LIMIT",
          "OFFSET",
          "ASC",
          "DESC",
          "JOIN",
          "LEFT",
          "RIGHT",
          "FULL",
          "INNER",
          "OUTER",
          "CROSS",
          "ON",
          "USING",
          "RETURNING",
          "FETCH",
          "FIRST",
          "NEXT",
          "ROWS",
          "ONLY",
          "JSON");

  private final String sql;
  private int index;
  private int line = 1;
  private int column = 1;

  public SqlTokenizer(String sql) {
    this.sql = sql;
  }

  public List<Token> tokenize() {
    List<Token> tokens = new ArrayList<>();
    while (!isAtEnd()) {
      char current = peek();
      if (Character.isWhitespace(current)) {
        advance();
      } else if (current == '\'') {
        tokens.add(string());
      } else if (current == '"' || current == '`') {
        tokens.add(quotedIdentifier());
      } else if (startsWith("#{") || startsWith("${")) {
        tokens.add(placeholder());
      } else if (isIdentifierStart(current)) {
        tokens.add(word());
      } else if (Character.isDigit(current)) {
        tokens.add(number());
      } else {
        tokens.add(symbol());
      }
    }
    Position position = position();
    tokens.add(new Token(TokenType.EOF, "", new Range(position, position)));
    return List.copyOf(tokens);
  }

  private Token word() {
    Position start = position();
    int startIndex = index;
    while (!isAtEnd() && isIdentifierPart(peek())) {
      advance();
    }
    while (consumeIdentifierPartChain()) {
      // Consume dotted quoted/unquoted identifier parts as one logical name.
    }
    String raw = sql.substring(startIndex, index);
    String upper = raw.toUpperCase(Locale.ROOT);
    TokenType type = KEYWORDS.contains(upper) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
    String text = type == TokenType.KEYWORD ? upper : raw;
    return new Token(type, text, new Range(start, position()));
  }

  private Token quotedIdentifier() {
    Position start = position();
    int startIndex = index;
    quotedIdentifierPart();
    while (consumeIdentifierPartChain()) {
      // Consume dotted quoted/unquoted identifier parts as one logical name.
    }
    return new Token(
        TokenType.IDENTIFIER, sql.substring(startIndex, index), new Range(start, position()));
  }

  private Token number() {
    Position start = position();
    int startIndex = index;
    while (!isAtEnd() && (Character.isDigit(peek()) || peek() == '.')) {
      advance();
    }
    return new Token(
        TokenType.NUMBER, sql.substring(startIndex, index), new Range(start, position()));
  }

  private Token string() {
    Position start = position();
    int startIndex = index;
    char quote = advance();
    while (!isAtEnd()) {
      char current = advance();
      if (current == '\\' && !isAtEnd()) {
        advance();
      } else if (current == quote) {
        break;
      }
    }
    return new Token(
        TokenType.STRING, sql.substring(startIndex, index), new Range(start, position()));
  }

  private void quotedIdentifierPart() {
    char quote = advance();
    while (!isAtEnd()) {
      char current = advance();
      if (current == quote) {
        if (!isAtEnd() && peek() == quote) {
          advance();
        } else {
          break;
        }
      }
    }
  }

  private Token placeholder() {
    Position start = position();
    int startIndex = index;
    advance();
    advance();
    while (!isAtEnd() && peek() != '}') {
      advance();
    }
    if (!isAtEnd()) {
      advance();
    }
    return new Token(
        TokenType.PLACEHOLDER, sql.substring(startIndex, index), new Range(start, position()));
  }

  private Token symbol() {
    Position start = position();
    char current = advance();
    return new Token(TokenType.SYMBOL, String.valueOf(current), new Range(start, position()));
  }

  private boolean isIdentifierStart(char value) {
    return Character.isLetter(value) || value == '_';
  }

  private boolean isIdentifierPart(char value) {
    return Character.isLetterOrDigit(value) || value == '_' || value == '.';
  }

  private boolean consumeIdentifierPartChain() {
    if (isAtEnd() || peek() != '.' || index + 1 >= sql.length()) {
      return false;
    }
    char next = sql.charAt(index + 1);
    if (next == '"' || next == '`') {
      advance();
      quotedIdentifierPart();
      return true;
    }
    if (isIdentifierStart(next)) {
      advance();
      while (!isAtEnd() && isIdentifierPart(peek())) {
        advance();
      }
      return true;
    }
    return false;
  }

  private boolean startsWith(String value) {
    return sql.startsWith(value, index);
  }

  private char peek() {
    return sql.charAt(index);
  }

  private char advance() {
    char current = sql.charAt(index++);
    if (current == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
    return current;
  }

  private boolean isAtEnd() {
    return index >= sql.length();
  }

  private Position position() {
    return new Position(index, line, column);
  }
}
