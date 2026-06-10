package io.github.isksss.mapperforge.print;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.parse.ognl.OgnlExpressionParser;
import java.util.List;
import org.junit.jupiter.api.Test;

final class OgnlAstPrinterTest {
  private final OgnlAstPrinter printer = new OgnlAstPrinter();

  @Test
  void printsOperatorsWithStableSpacing() {
    assertEquals(
        "name != null && status in {'ACTIVE','NEW'}",
        print("name!=null&&status in {'ACTIVE','NEW'}"));
  }

  @Test
  void keepsParenthesesWhenRequiredByPrecedence() {
    assertEquals("not (name == null || admin)", print("not (name==null||admin)"));
  }

  @Test
  void printsMethodCallsAndPropertyAccess() {
    assertEquals(
        "helper.allowed(user.id, status) && user instanceof AdminUser",
        print("helper.allowed(user.id,status)&&user instanceof AdminUser"));
  }

  @Test
  void printsCollectionValuesWithoutAddedSpaces() {
    assertEquals(
        "{'ACTIVE','NEW'}",
        printer.print(new OgnlCollectionExpression(List.of(parse("'ACTIVE'"), parse("'NEW'")))));
  }

  @Test
  void printsUnknownExpressionRawValue() {
    assertEquals("enabled ? name != null : admin", print("enabled ? name != null : admin"));
  }

  private String print(String expression) {
    return printer.print(parse(expression));
  }

  private io.github.isksss.mapperforge.ast.ognl.OgnlExpression parse(String expression) {
    return new OgnlExpressionParser(expression).parse();
  }
}
