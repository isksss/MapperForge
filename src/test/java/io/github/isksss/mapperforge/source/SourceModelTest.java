package io.github.isksss.mapperforge.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.token.Token;
import io.github.isksss.mapperforge.token.TokenType;
import org.junit.jupiter.api.Test;

final class SourceModelTest {
  @Test
  void sourcePositionRangeAndTokenAreRecords() {
    assertTrue(SourceFile.class.isRecord());
    assertTrue(Position.class.isRecord());
    assertTrue(Range.class.isRecord());
    assertTrue(Token.class.isRecord());
  }

  @Test
  void sourceRangeUsesZeroBasedOffsetAndOneBasedLineColumn() {
    Position start = new Position(7, 2, 3);
    Position end = new Position(13, 2, 9);
    Range range = new Range(start, end);
    Token token = new Token(TokenType.IDENTIFIER, "user_id", range);

    assertEquals(7, token.range().start().offset());
    assertEquals(2, token.range().start().line());
    assertEquals(3, token.range().start().column());
    assertEquals(13, token.range().end().offset());
    assertEquals(2, token.range().end().line());
    assertEquals(9, token.range().end().column());
  }
}
