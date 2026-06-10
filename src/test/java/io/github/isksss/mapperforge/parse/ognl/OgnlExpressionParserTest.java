package io.github.isksss.mapperforge.parse.ognl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.isksss.mapperforge.ast.ognl.OgnlBinaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCallExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlLiteralExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlNameExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnknownExpression;
import org.junit.jupiter.api.Test;

final class OgnlExpressionParserTest {
  @Test
  void parsesBooleanOperatorPrecedence() {
    OgnlBinaryExpression expression =
        assertInstanceOf(
            OgnlBinaryExpression.class, parse("name != null && status == 'ACTIVE' || admin"));

    assertEquals("||", expression.operator());
    OgnlBinaryExpression left = assertInstanceOf(OgnlBinaryExpression.class, expression.left());
    assertEquals("&&", left.operator());
    assertName("admin", expression.right());
  }

  @Test
  void parsesWordOperatorsAndUnaryNot() {
    OgnlBinaryExpression expression =
        assertInstanceOf(
            OgnlBinaryExpression.class, parse("not user.disabled and user instanceof AdminUser"));

    assertEquals("and", expression.operator());
    OgnlUnaryExpression unary = assertInstanceOf(OgnlUnaryExpression.class, expression.left());
    assertEquals("not", unary.operator());
    OgnlBinaryExpression right = assertInstanceOf(OgnlBinaryExpression.class, expression.right());
    assertEquals("instanceof", right.operator());
  }

  @Test
  void parsesMethodCallsAndCollectionLiterals() {
    OgnlBinaryExpression expression =
        assertInstanceOf(
            OgnlBinaryExpression.class,
            parse("helper.allowed(user.id, status) && status in {'ACTIVE','NEW'}"));

    OgnlCallExpression call = assertInstanceOf(OgnlCallExpression.class, expression.left());
    assertEquals("helper.allowed", call.name());
    assertEquals(2, call.arguments().size());

    OgnlBinaryExpression right = assertInstanceOf(OgnlBinaryExpression.class, expression.right());
    assertEquals("in", right.operator());
    OgnlCollectionExpression collection =
        assertInstanceOf(OgnlCollectionExpression.class, right.right());
    assertEquals(2, collection.values().size());
    assertEquals(
        "ACTIVE",
        assertInstanceOf(OgnlLiteralExpression.class, collection.values().get(0)).value());
  }

  @Test
  void fallsBackToUnknownExpressionWhenTokensRemain() {
    OgnlUnknownExpression expression =
        assertInstanceOf(OgnlUnknownExpression.class, parse("name matches '^A'"));

    assertEquals("name matches '^A'", expression.raw());
  }

  @Test
  void fallsBackToUnknownExpressionForMalformedExpression() {
    OgnlUnknownExpression expression =
        assertInstanceOf(OgnlUnknownExpression.class, parse("name != )"));

    assertEquals("name != )", expression.raw());
  }

  private static OgnlExpression parse(String ognl) {
    return new OgnlExpressionParser(ognl).parse();
  }

  private static void assertName(String expected, OgnlExpression expression) {
    assertEquals(expected, assertInstanceOf(OgnlNameExpression.class, expression).name());
  }
}
