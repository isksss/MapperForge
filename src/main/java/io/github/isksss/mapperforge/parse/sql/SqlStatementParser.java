package io.github.isksss.mapperforge.parse.sql;

import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.Expression;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.SelectStatement;
import io.github.isksss.mapperforge.ast.sql.SetOperationStatement;
import io.github.isksss.mapperforge.ast.sql.Statement;
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.ast.sql.UpdateStatement;
import io.github.isksss.mapperforge.ast.sql.WithStatement;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SqlStatementParser {
  private static final Set<String> SET_OPERATORS = Set.of("UNION", "INTERSECT", "EXCEPT");

  private final String sql;
  private final List<Token> tokens;

  public SqlStatementParser(String sql) {
    this.sql = sql.strip();
    this.tokens = new SqlTokenizer(this.sql).tokenize();
  }

  public Statement parse() {
    Token first = firstSignificantToken();
    if (first.type() != TokenType.KEYWORD) {
      return new UnknownStatement(sql);
    }
    String setOperator = setOperator();
    if (setOperator != null) {
      return new SetOperationStatement(sql, setOperator);
    }
    return switch (first.text()) {
      case "SELECT" -> parseSelect();
      case "INSERT" -> new InsertStatement(sql);
      case "UPDATE" -> new UpdateStatement(sql);
      case "DELETE" -> new DeleteStatement(sql);
      case "WITH" -> new WithStatement(sql);
      default -> new UnknownStatement(sql);
    };
  }

  private Token firstSignificantToken() {
    return tokens.stream()
        .filter(token -> token.type() != TokenType.EOF)
        .findFirst()
        .orElse(tokens.getLast());
  }

  private String setOperator() {
    return tokens.stream()
        .filter(token -> token.type() == TokenType.KEYWORD)
        .map(Token::text)
        .filter(SET_OPERATORS::contains)
        .findFirst()
        .orElse(null);
  }

  private SelectStatement parseSelect() {
    int fromIndex = topLevelKeyword("FROM", 1);
    if (fromIndex < 0) {
      return new SelectStatement(sql);
    }
    int whereIndex = topLevelKeyword("WHERE", fromIndex + 1);
    List<Expression> selectItems = parseExpressionList(tokensBetween(1, fromIndex));
    String from =
        whereIndex >= 0
            ? tokensBetween(fromIndex + 1, whereIndex).strip()
            : tokensBetween(fromIndex + 1, eofIndex()).strip();
    Expression where =
        whereIndex >= 0
            ? new SqlExpressionParser(tokensBetween(whereIndex + 1, eofIndex())).parse()
            : new UnknownExpression("");
    return new SelectStatement(sql, selectItems, from, where);
  }

  private List<Expression> parseExpressionList(String raw) {
    List<Expression> expressions = new ArrayList<>();
    int depth = 0;
    int start = 0;
    for (int i = 0; i < raw.length(); i++) {
      char value = raw.charAt(i);
      if (value == '(' || value == '[') {
        depth++;
      } else if (value == ')' || value == ']') {
        depth--;
      } else if (value == ',' && depth == 0) {
        expressions.add(new SqlExpressionParser(raw.substring(start, i)).parse());
        start = i + 1;
      }
    }
    String tail = raw.substring(start).strip();
    if (!tail.isEmpty()) {
      expressions.add(new SqlExpressionParser(tail).parse());
    }
    return List.copyOf(expressions);
  }

  private int topLevelKeyword(String keyword, int start) {
    int depth = 0;
    for (int i = start; i < eofIndex(); i++) {
      Token token = tokens.get(i);
      if (token.type() == TokenType.SYMBOL) {
        if ("(".equals(token.text()) || "[".equals(token.text())) {
          depth++;
        } else if (")".equals(token.text()) || "]".equals(token.text())) {
          depth--;
        }
      } else if (depth == 0 && token.type() == TokenType.KEYWORD && keyword.equals(token.text())) {
        return i;
      }
    }
    return -1;
  }

  private String tokensBetween(int startInclusive, int endExclusive) {
    if (startInclusive >= endExclusive) {
      return "";
    }
    int startOffset = tokens.get(startInclusive).range().start().offset();
    int endOffset = tokens.get(endExclusive - 1).range().end().offset();
    return sql.substring(startOffset, endOffset);
  }

  private int eofIndex() {
    return tokens.size() - 1;
  }
}
