package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

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
import io.github.isksss.mapperforge.ast.sql.JsonExpression;
import io.github.isksss.mapperforge.ast.sql.LiteralExpression;
import io.github.isksss.mapperforge.ast.sql.PlaceholderExpression;
import io.github.isksss.mapperforge.ast.sql.RowExpression;
import io.github.isksss.mapperforge.ast.sql.SubQueryExpression;
import io.github.isksss.mapperforge.ast.sql.UnaryExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
import io.github.isksss.mapperforge.ast.sql.WindowExpression;
import org.junit.jupiter.api.Test;

final class SqlExpressionParserTest {
  @Test
  void parsesBinaryOperatorPrecedence() {
    BinaryExpression expression = assertInstanceOf(BinaryExpression.class, parse("a + b * 2"));

    assertColumn("a", expression.left());
    assertEquals("+", expression.operator());
    BinaryExpression right = assertInstanceOf(BinaryExpression.class, expression.right());
    assertColumn("b", right.left());
    assertEquals("*", right.operator());
    assertLiteral("2", right.right());
  }

  @Test
  void parsesUnaryAndGroupedExpression() {
    UnaryExpression expression = assertInstanceOf(UnaryExpression.class, parse("not (a = 1)"));

    assertEquals("NOT", expression.operator());
    assertInstanceOf(BinaryExpression.class, expression.expression());
  }

  @Test
  void parsesFunctionAndPlaceholderArguments() {
    FunctionExpression expression =
        assertInstanceOf(FunctionExpression.class, parse("coalesce(name, #{fallback})"));

    assertEquals("coalesce", expression.name());
    assertColumn("name", expression.arguments().get(0));
    assertInstanceOf(PlaceholderExpression.class, expression.arguments().get(1));
  }

  @Test
  void parsesCaseExpression() {
    CaseExpression expression =
        assertInstanceOf(
            CaseExpression.class, parse("case when age >= 20 then 'adult' else 'minor' end"));

    assertEquals(1, expression.whenClauses().size());
    assertEquals(
        "adult", ((LiteralExpression) expression.whenClauses().getFirst().result()).value());
    assertEquals("minor", ((LiteralExpression) expression.elseExpression()).value());
  }

  @Test
  void parsesBetweenAndInExpressions() {
    BetweenExpression between =
        assertInstanceOf(BetweenExpression.class, parse("age between 18 and 64"));
    assertColumn("age", between.expression());
    assertLiteral("18", between.lower());
    assertLiteral("64", between.upper());

    InExpression in = assertInstanceOf(InExpression.class, parse("status in ('ACTIVE', 'LOCKED')"));
    assertColumn("status", in.expression());
    assertEquals(2, in.values().size());
  }

  @Test
  void parsesCastArrayRowAndExists() {
    CastExpression cast = assertInstanceOf(CastExpression.class, parse("cast(#{id} as bigint)"));
    assertInstanceOf(PlaceholderExpression.class, cast.expression());
    assertEquals("bigint", cast.typeName());

    ArrayExpression array = assertInstanceOf(ArrayExpression.class, parse("array[1, 2]"));
    assertEquals(2, array.values().size());

    RowExpression row = assertInstanceOf(RowExpression.class, parse("row(id, name)"));
    assertEquals(2, row.values().size());

    ExistsExpression exists =
        assertInstanceOf(ExistsExpression.class, parse("exists (select 1 from users)"));
    assertEquals("select 1 from users", exists.subQuery());
  }

  @Test
  void parsesSubQueryJsonAndWindowExpressions() {
    SubQueryExpression subQuery =
        assertInstanceOf(SubQueryExpression.class, parse("(select id from users)"));
    assertEquals("select id from users", subQuery.sql());

    JsonExpression json = assertInstanceOf(JsonExpression.class, parse("payload ->> 'name'"));
    assertColumn("payload", json.expression());
    assertEquals("->>", json.operator());
    assertLiteral("name", json.path());

    WindowExpression window =
        assertInstanceOf(
            WindowExpression.class,
            parse("count(*) over (partition by tenant_id order by created_at)"));
    FunctionExpression function = assertInstanceOf(FunctionExpression.class, window.expression());
    assertEquals("count", function.name());
    assertEquals("partition by tenant_id order by created_at", window.windowSpec());
  }

  @Test
  void fallsBackToUnknownExpressionWhenTokensRemain() {
    UnknownExpression expression =
        assertInstanceOf(UnknownExpression.class, parse("name is not null"));

    assertEquals("name is not null", expression.raw());
  }

  @Test
  void fallsBackToUnknownExpressionForMalformedExpression() {
    UnknownExpression expression = assertInstanceOf(UnknownExpression.class, parse("a + )"));

    assertEquals("a + )", expression.raw());
  }

  @Test
  void stripsOuterWhitespaceAndIgnoresComments() {
    BinaryExpression expression =
        assertInstanceOf(
            BinaryExpression.class,
            parse(
                """

                -- leading comment
                id = #{id} /* trailing comment */
                """));

    assertEquals("=", expression.operator());
    assertColumn("id", expression.left());
    assertInstanceOf(PlaceholderExpression.class, expression.right());
  }

  @Test
  void blankSqlFallsBackToUnknownExpressionWithStrippedRawSql() {
    UnknownExpression expression = assertInstanceOf(UnknownExpression.class, parse(" \n\t "));

    assertEquals("", expression.raw());
  }

  private static Expression parse(String sql) {
    return new SqlExpressionParser(sql).parse();
  }

  private static void assertColumn(String expected, Expression expression) {
    assertEquals(expected, assertInstanceOf(ColumnExpression.class, expression).name());
  }

  private static void assertLiteral(String expected, Expression expression) {
    assertEquals(expected, assertInstanceOf(LiteralExpression.class, expression).value());
  }
}
