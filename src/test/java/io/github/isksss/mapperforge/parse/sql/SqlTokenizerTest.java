package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.token.TokenType;
import java.util.List;
import org.junit.jupiter.api.Test;

final class SqlTokenizerTest {
  @Test
  void tokenizesKeywordsPlaceholdersAndRanges() {
    List<String> tokens =
        new SqlTokenizer("select id from users where id = #{id} and table_name = ${table}")
            .tokenize().stream()
                .filter(token -> token.type() != TokenType.EOF)
                .map(
                    token ->
                        token.type() + ":" + token.text() + "@" + token.range().start().column())
                .toList();

    assertEquals(
        List.of(
            "KEYWORD:SELECT@1",
            "IDENTIFIER:id@8",
            "KEYWORD:FROM@11",
            "IDENTIFIER:users@16",
            "KEYWORD:WHERE@22",
            "IDENTIFIER:id@28",
            "SYMBOL:=@31",
            "PLACEHOLDER:#{id}@33",
            "KEYWORD:AND@39",
            "IDENTIFIER:table_name@43",
            "SYMBOL:=@54",
            "PLACEHOLDER:${table}@56"),
        tokens);
  }

  @Test
  void tokenizesStringWithoutUppercasingItsContent() {
    var tokens = new SqlTokenizer("select 'from and where'").tokenize();

    assertEquals("SELECT", tokens.get(0).text());
    assertEquals("'from and where'", tokens.get(1).text());
  }
}
