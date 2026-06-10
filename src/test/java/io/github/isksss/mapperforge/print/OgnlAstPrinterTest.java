package io.github.isksss.mapperforge.print;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.parse.ognl.OgnlExpressionParser;
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

  private String print(String expression) {
    return printer.print(new OgnlExpressionParser(expression).parse());
  }
}
