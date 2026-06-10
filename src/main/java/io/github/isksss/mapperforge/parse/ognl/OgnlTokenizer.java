package io.github.isksss.mapperforge.parse.ognl;

import io.github.isksss.mapperforge.source.Position;
import io.github.isksss.mapperforge.source.Range;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.util.ArrayList;
import java.util.List;

final class OgnlTokenizer {
  private final String expression;
  private int index;
  private int line = 1;
  private int column = 1;

  OgnlTokenizer(String expression) {
    this.expression = expression;
  }

  List<Token> tokenize() {
    List<Token> tokens = new ArrayList<>();
    while (!isAtEnd()) {
      char current = peek();
      if (Character.isWhitespace(current)) {
        advance();
      } else if (current == '\'' || current == '"') {
        tokens.add(string());
      } else if (startsWithAny("&&", "||", "==", "!=", ">=", "<=")) {
        tokens.add(symbol(2));
      } else if (isIdentifierStart(current)) {
        tokens.add(identifier());
      } else if (Character.isDigit(current)) {
        tokens.add(number());
      } else {
        tokens.add(symbol(1));
      }
    }
    Position position = position();
    tokens.add(new Token(TokenType.EOF, "", new Range(position, position)));
    return List.copyOf(tokens);
  }

  private Token identifier() {
    Position start = position();
    int startIndex = index;
    while (!isAtEnd() && isIdentifierPart(peek())) {
      advance();
    }
    return new Token(
        TokenType.IDENTIFIER,
        expression.substring(startIndex, index),
        new Range(start, position()));
  }

  private Token number() {
    Position start = position();
    int startIndex = index;
    while (!isAtEnd() && (Character.isDigit(peek()) || peek() == '.')) {
      advance();
    }
    return new Token(
        TokenType.NUMBER, expression.substring(startIndex, index), new Range(start, position()));
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
        TokenType.STRING, expression.substring(startIndex, index), new Range(start, position()));
  }

  private Token symbol(int length) {
    Position start = position();
    int startIndex = index;
    for (int i = 0; i < length; i++) {
      advance();
    }
    return new Token(
        TokenType.SYMBOL, expression.substring(startIndex, index), new Range(start, position()));
  }

  private boolean isIdentifierStart(char value) {
    return Character.isLetter(value) || value == '_' || value == '$';
  }

  private boolean isIdentifierPart(char value) {
    return Character.isLetterOrDigit(value) || value == '_' || value == '$' || value == '.';
  }

  private boolean startsWithAny(String... candidates) {
    for (String candidate : candidates) {
      if (expression.startsWith(candidate, index)) {
        return true;
      }
    }
    return false;
  }

  private char peek() {
    return expression.charAt(index);
  }

  private char advance() {
    char current = expression.charAt(index++);
    if (current == '\n') {
      line++;
      column = 1;
    } else {
      column++;
    }
    return current;
  }

  private boolean isAtEnd() {
    return index >= expression.length();
  }

  private Position position() {
    return new Position(index, line, column);
  }
}
