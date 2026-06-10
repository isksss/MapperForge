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
    int groupIndex = topLevelKeywordPair("GROUP", "BY", fromIndex + 1);
    int havingIndex = topLevelKeyword("HAVING", fromIndex + 1);
    int orderIndex = topLevelKeywordPair("ORDER", "BY", fromIndex + 1);
    int limitIndex = topLevelKeyword("LIMIT", fromIndex + 1);
    int offsetIndex = topLevelKeyword("OFFSET", fromIndex + 1);
    int windowIndex = topLevelKeyword("WINDOW", fromIndex + 1);
    int fetchIndex = topLevelKeyword("FETCH", fromIndex + 1);
    List<Expression> selectItems = parseExpressionList(tokensBetween(1, fromIndex));
    int fromEnd = nextClauseIndex(fromIndex);
    int firstJoinIndex = firstJoinIndex(fromIndex + 1, fromEnd);
    String from =
        firstJoinIndex >= 0
            ? tokensBetween(fromIndex + 1, firstJoinIndex).strip()
            : tokensBetween(fromIndex + 1, fromEnd).strip();
    List<SelectStatement.JoinItem> joins =
        firstJoinIndex >= 0 ? parseJoins(firstJoinIndex, fromEnd) : List.of();
    Expression where =
        whereIndex >= 0
            ? new SqlExpressionParser(tokensBetween(whereIndex + 1, nextClauseIndex(whereIndex)))
                .parse()
            : new UnknownExpression("");
    List<Expression> groupBy =
        groupIndex >= 0
            ? parseExpressionList(tokensBetween(groupIndex + 2, nextClauseIndex(groupIndex)))
            : List.of();
    Expression having =
        havingIndex >= 0
            ? new SqlExpressionParser(tokensBetween(havingIndex + 1, nextClauseIndex(havingIndex)))
                .parse()
            : new UnknownExpression("");
    List<SelectStatement.OrderByItem> orderBy =
        orderIndex >= 0
            ? parseOrderBy(tokensBetween(orderIndex + 2, nextClauseIndex(orderIndex)))
            : List.of();
    Expression limit =
        limitIndex >= 0
            ? new SqlExpressionParser(tokensBetween(limitIndex + 1, nextClauseIndex(limitIndex)))
                .parse()
            : new UnknownExpression("");
    Expression offset =
        offsetIndex >= 0
            ? new SqlExpressionParser(tokensBetween(offsetIndex + 1, nextClauseIndex(offsetIndex)))
                .parse()
            : new UnknownExpression("");
    List<SelectStatement.WindowItem> windows =
        windowIndex >= 0
            ? parseWindows(tokensBetween(windowIndex + 1, nextClauseIndex(windowIndex)))
            : List.of();
    SelectStatement.FetchClause fetch =
        fetchIndex >= 0
            ? parseFetch(tokensBetween(fetchIndex, nextClauseIndex(fetchIndex)))
            : new SelectStatement.FetchClause("", new UnknownExpression(""));
    return new SelectStatement(
        sql,
        selectItems,
        from,
        where,
        groupBy,
        having,
        orderBy,
        limit,
        offset,
        joins,
        windows,
        fetch);
  }

  private InsertStatement parseInsert() {
    int intoIndex = topLevelKeyword("INTO", 1);
    int valuesIndex = topLevelKeyword("VALUES", intoIndex + 1);
    int selectIndex = topLevelKeyword("SELECT", intoIndex + 1);
    int returningIndex = topLevelKeyword("RETURNING", intoIndex + 1);
    int sourceIndex = valuesIndex >= 0 ? valuesIndex : selectIndex;
    if (intoIndex < 0 || sourceIndex < 0 || intoIndex + 1 >= sourceIndex) {
      return new InsertStatement(sql);
    }
    String table = tokens.get(intoIndex + 1).text();
    List<String> columns = List.of();
    if (intoIndex + 2 < sourceIndex && "(".equals(tokens.get(intoIndex + 2).text())) {
      columns = parseNameList(tokensBetween(intoIndex + 3, sourceIndex - 1));
    }
    List<Expression> values = List.of();
    List<List<Expression>> valueRows = List.of();
    Statement selectSource = new UnknownStatement("");
    int statementEnd = returningIndex >= 0 ? returningIndex : eofIndex();
    if (valuesIndex >= 0) {
      valueRows = parseValueRows(valuesIndex + 1, statementEnd);
      values = valueRows.isEmpty() ? List.of() : valueRows.getFirst();
    } else if (selectIndex >= 0) {
      selectSource = new SqlStatementParser(tokensBetween(selectIndex, statementEnd)).parse();
    }
    List<Expression> returning =
        returningIndex >= 0
            ? parseExpressionList(tokensBetween(returningIndex + 1, eofIndex()))
            : List.of();
    return new InsertStatement(sql, table, columns, values, valueRows, selectSource, returning);
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
    int usingIndex = topLevelKeyword("USING", fromIndex + 1);
    int returningIndex = topLevelKeyword("RETURNING", fromIndex + 1);
    String table = tokensBetween(fromIndex + 1, nextDeleteClauseIndex(fromIndex)).strip();
    String using =
        usingIndex >= 0
            ? tokensBetween(usingIndex + 1, nextDeleteClauseIndex(usingIndex)).strip()
            : "";
    Expression where =
        whereIndex >= 0
            ? new SqlExpressionParser(
                    tokensBetween(whereIndex + 1, nextDeleteClauseIndex(whereIndex)))
                .parse()
            : new UnknownExpression("");
    List<Expression> returning =
        returningIndex >= 0
            ? parseExpressionList(tokensBetween(returningIndex + 1, eofIndex()))
            : List.of();
    return new DeleteStatement(sql, table, using, where, returning);
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

  private List<List<Expression>> parseValueRows(int start, int end) {
    List<List<Expression>> rows = new ArrayList<>();
    int index = start;
    while (index < end) {
      if (!"(".equals(tokens.get(index).text())) {
        index++;
        continue;
      }
      int close = matchingParenIndex(index, end);
      if (close < 0) {
        break;
      }
      rows.add(parseExpressionList(tokensBetween(index + 1, close)));
      index = close + 1;
    }
    return List.copyOf(rows);
  }

  private int matchingParenIndex(int openIndex, int end) {
    int depth = 0;
    for (int i = openIndex; i < end; i++) {
      if (tokens.get(i).type() == TokenType.SYMBOL && "(".equals(tokens.get(i).text())) {
        depth++;
      } else if (tokens.get(i).type() == TokenType.SYMBOL && ")".equals(tokens.get(i).text())) {
        depth--;
        if (depth == 0) {
          return i;
        }
      }
    }
    return -1;
  }

  private List<String> parseNameList(String raw) {
    return parseCommaSeparated(raw).stream()
        .map(String::strip)
        .filter(value -> !value.isEmpty())
        .toList();
  }

  private List<SelectStatement.OrderByItem> parseOrderBy(String raw) {
    return parseCommaSeparated(raw).stream()
        .map(String::strip)
        .filter(value -> !value.isEmpty())
        .map(this::parseOrderByItem)
        .toList();
  }

  private SelectStatement.OrderByItem parseOrderByItem(String raw) {
    String upper = raw.toUpperCase(java.util.Locale.ROOT);
    if (upper.endsWith(" DESC")) {
      return new SelectStatement.OrderByItem(
          new SqlExpressionParser(raw.substring(0, raw.length() - 5).strip()).parse(), "DESC");
    }
    if (upper.endsWith(" ASC")) {
      return new SelectStatement.OrderByItem(
          new SqlExpressionParser(raw.substring(0, raw.length() - 4).strip()).parse(), "ASC");
    }
    return new SelectStatement.OrderByItem(new SqlExpressionParser(raw).parse(), "");
  }

  private List<SelectStatement.JoinItem> parseJoins(int start, int end) {
    List<SelectStatement.JoinItem> joins = new ArrayList<>();
    int currentJoin = start;
    while (currentJoin >= 0 && currentJoin < end) {
      int nextJoin = nextJoinIndex(currentJoin + 1, end);
      joins.add(parseJoin(currentJoin, nextJoin >= 0 ? nextJoin : end));
      currentJoin = nextJoin;
    }
    return List.copyOf(joins);
  }

  private List<SelectStatement.WindowItem> parseWindows(String raw) {
    return parseCommaSeparated(raw).stream()
        .map(String::strip)
        .filter(value -> !value.isEmpty())
        .map(this::parseWindow)
        .toList();
  }

  private SelectStatement.WindowItem parseWindow(String raw) {
    int asIndex = raw.toUpperCase(java.util.Locale.ROOT).indexOf(" AS ");
    if (asIndex < 0) {
      return new SelectStatement.WindowItem(raw, "");
    }
    String name = raw.substring(0, asIndex).strip();
    String spec = raw.substring(asIndex + 4).strip();
    if (spec.startsWith("(") && spec.endsWith(")")) {
      spec = spec.substring(1, spec.length() - 1).strip();
    }
    return new SelectStatement.WindowItem(name, spec);
  }

  private SelectStatement.FetchClause parseFetch(String raw) {
    List<Token> fetchTokens = new SqlTokenizer(raw).tokenize();
    Expression count = new UnknownExpression("");
    for (int i = 0; i < fetchTokens.size(); i++) {
      Token token = fetchTokens.get(i);
      if (token.type() == TokenType.NUMBER || token.type() == TokenType.PLACEHOLDER) {
        count = new SqlExpressionParser(token.text()).parse();
        break;
      }
    }
    return new SelectStatement.FetchClause(raw.strip().toUpperCase(java.util.Locale.ROOT), count);
  }

  private SelectStatement.JoinItem parseJoin(int start, int end) {
    int joinKeyword = joinKeywordIndex(start, end);
    int onIndex = topLevelKeyword("ON", joinKeyword + 1);
    if (joinKeyword < 0 || onIndex < 0 || onIndex >= end) {
      return new SelectStatement.JoinItem(
          tokensBetween(start, Math.min(start + 1, end)), "", new UnknownExpression(""));
    }
    String kind = tokensBetween(start, joinKeyword + 1).strip().toUpperCase(java.util.Locale.ROOT);
    String table = tokensBetween(joinKeyword + 1, onIndex).strip();
    Expression on = new SqlExpressionParser(tokensBetween(onIndex + 1, end)).parse();
    return new SelectStatement.JoinItem(kind, table, on);
  }

  private int firstJoinIndex(int start, int end) {
    return nextJoinIndex(start, end);
  }

  private int nextJoinIndex(int start, int end) {
    for (int i = start; i < end; i++) {
      if (isJoinStart(i, end)) {
        return i;
      }
    }
    return -1;
  }

  private boolean isJoinStart(int index, int end) {
    if (tokens.get(index).type() != TokenType.KEYWORD) {
      return false;
    }
    if ("JOIN".equals(tokens.get(index).text())) {
      return index == 0
          || tokens.get(index - 1).type() != TokenType.KEYWORD
          || !Set.of("LEFT", "RIGHT", "FULL", "INNER", "CROSS", "OUTER")
              .contains(tokens.get(index - 1).text());
    }
    return Set.of("LEFT", "RIGHT", "FULL", "INNER", "CROSS").contains(tokens.get(index).text())
        && index + 1 < end
        && tokens.get(index + 1).type() == TokenType.KEYWORD
        && ("JOIN".equals(tokens.get(index + 1).text())
            || ("OUTER".equals(tokens.get(index + 1).text())
                && index + 2 < end
                && tokens.get(index + 2).type() == TokenType.KEYWORD
                && "JOIN".equals(tokens.get(index + 2).text())));
  }

  private int joinKeywordIndex(int start, int end) {
    for (int i = start; i < end; i++) {
      if (tokens.get(i).type() == TokenType.KEYWORD && "JOIN".equals(tokens.get(i).text())) {
        return i;
      }
    }
    return -1;
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

  private int topLevelKeywordPair(String first, String second, int start) {
    int firstIndex = topLevelKeyword(first, start);
    while (firstIndex >= 0 && firstIndex + 1 < eofIndex()) {
      if (tokens.get(firstIndex + 1).type() == TokenType.KEYWORD
          && second.equals(tokens.get(firstIndex + 1).text())) {
        return firstIndex;
      }
      firstIndex = topLevelKeyword(first, firstIndex + 1);
    }
    return -1;
  }

  private int nextClauseIndex(int currentClauseIndex) {
    return List.of(
            topLevelKeyword("WHERE", currentClauseIndex + 1),
            topLevelKeywordPair("GROUP", "BY", currentClauseIndex + 1),
            topLevelKeyword("HAVING", currentClauseIndex + 1),
            topLevelKeywordPair("ORDER", "BY", currentClauseIndex + 1),
            topLevelKeyword("LIMIT", currentClauseIndex + 1),
            topLevelKeyword("OFFSET", currentClauseIndex + 1),
            topLevelKeyword("WINDOW", currentClauseIndex + 1),
            topLevelKeyword("FETCH", currentClauseIndex + 1),
            eofIndex())
        .stream()
        .filter(index -> index > currentClauseIndex)
        .min(Integer::compareTo)
        .orElse(eofIndex());
  }

  private int nextDeleteClauseIndex(int currentClauseIndex) {
    return List.of(
            topLevelKeyword("USING", currentClauseIndex + 1),
            topLevelKeyword("WHERE", currentClauseIndex + 1),
            topLevelKeyword("RETURNING", currentClauseIndex + 1),
            eofIndex())
        .stream()
        .filter(index -> index > currentClauseIndex)
        .min(Integer::compareTo)
        .orElse(eofIndex());
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
