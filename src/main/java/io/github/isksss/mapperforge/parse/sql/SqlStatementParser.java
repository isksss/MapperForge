package io.github.isksss.mapperforge.parse.sql;

import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.SelectStatement;
import io.github.isksss.mapperforge.ast.sql.SetOperationStatement;
import io.github.isksss.mapperforge.ast.sql.Statement;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.ast.sql.UpdateStatement;
import io.github.isksss.mapperforge.ast.sql.WithStatement;
import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
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
      case "SELECT" -> new SelectStatement(sql);
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
}
