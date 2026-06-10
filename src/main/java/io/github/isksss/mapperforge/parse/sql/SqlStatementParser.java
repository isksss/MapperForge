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
      case "INSERT" -> parseInsert();
      case "UPDATE" -> parseUpdate();
      case "DELETE" -> parseDelete();
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

  private InsertStatement parseInsert() {
    int intoIndex = topLevelKeyword("INTO", 1);
    int valuesIndex = topLevelKeyword("VALUES", intoIndex + 1);
    if (intoIndex < 0 || valuesIndex < 0 || intoIndex + 1 >= valuesIndex) {
      return new InsertStatement(sql);
    }
    String table = tokens.get(intoIndex + 1).text();
    List<String> columns = List.of();
    if (intoIndex + 2 < valuesIndex && "(".equals(tokens.get(intoIndex + 2).text())) {
      columns = parseNameList(tokensBetween(intoIndex + 3, valuesIndex - 1));
    }
    List<Expression> values = List.of();
    if (valuesIndex + 1 < eofIndex() && "(".equals(tokens.get(valuesIndex + 1).text())) {
      values = parseExpressionList(tokensBetween(valuesIndex + 2, eofIndex() - 1));
    }
    return new InsertStatement(sql, table, columns, values);
  }

  private UpdateStatement parseUpdate() {
    int setIndex = topLevelKeyword("SET", 1);
    if (setIndex < 0 || setIndex < 2) {
      return new UpdateStatement(sql);
    }
    int whereIndex = topLevelKeyword("WHERE", setIndex + 1);
    String table = tokensBetween(1, setIndex).strip();
    String assignmentsRaw =
        whereIndex >= 0
            ? tokensBetween(setIndex + 1, whereIndex)
            : tokensBetween(setIndex + 1, eofIndex());
    Expression where =
        whereIndex >= 0
            ? new SqlExpressionParser(tokensBetween(whereIndex + 1, eofIndex())).parse()
            : new UnknownExpression("");
    return new UpdateStatement(sql, table, parseAssignments(assignmentsRaw), where);
  }

  private DeleteStatement parseDelete() {
    int fromIndex = topLevelKeyword("FROM", 1);
    if (fromIndex < 0 || fromIndex + 1 >= eofIndex()) {
      return new DeleteStatement(sql);
    }
    int whereIndex = topLevelKeyword("WHERE", fromIndex + 1);
    String table =
        whereIndex >= 0
            ? tokensBetween(fromIndex + 1, whereIndex).strip()
            : tokensBetween(fromIndex + 1, eofIndex()).strip();
    Expression where =
        whereIndex >= 0
            ? new SqlExpressionParser(tokensBetween(whereIndex + 1, eofIndex())).parse()
            : new UnknownExpression("");
    return new DeleteStatement(sql, table, where);
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

  private List<String> parseNameList(String raw) {
    return parseCommaSeparated(raw).stream()
        .map(String::strip)
        .filter(value -> !value.isEmpty())
        .toList();
  }

  private List<UpdateStatement.Assignment> parseAssignments(String raw) {
    return parseCommaSeparated(raw).stream()
        .map(String::strip)
        .filter(value -> !value.isEmpty())
        .map(this::parseAssignment)
        .toList();
  }

  private UpdateStatement.Assignment parseAssignment(String raw) {
    int equals = raw.indexOf('=');
    if (equals < 0) {
      return new UpdateStatement.Assignment(raw.strip(), new UnknownExpression(""));
    }
    return new UpdateStatement.Assignment(
        raw.substring(0, equals).strip(),
        new SqlExpressionParser(raw.substring(equals + 1).strip()).parse());
  }

  private List<String> parseCommaSeparated(String raw) {
    List<String> values = new ArrayList<>();
    int depth = 0;
    int start = 0;
    for (int i = 0; i < raw.length(); i++) {
      char value = raw.charAt(i);
      if (value == '(' || value == '[') {
        depth++;
      } else if (value == ')' || value == ']') {
        depth--;
      } else if (value == ',' && depth == 0) {
        values.add(raw.substring(start, i));
        start = i + 1;
      }
    }
    values.add(raw.substring(start));
    return List.copyOf(values);
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
