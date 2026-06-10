package io.github.isksss.mapperforge.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class OgnlFormatterTest {
  private final OgnlFormatter formatter = new OgnlFormatter();

  @Test
  void formatsOperatorsWithoutChangingStringLiterals() {
    assertEquals(
        "name != null && name != 'A>B and C' && status in {'ACTIVE','NEW'}",
        formatter.format("name!=null&&name!='A>B and C'&&status in {'ACTIVE','NEW'}"));
  }

  @Test
  void formatsWordsAndAccessExpressions() {
    assertEquals(
        "not user.disabled && user.profile.name != null && helper.allowed(user.id) || user instanceof AdminUser",
        formatter.format(
            "not user.disabled&&user.profile.name!=null&&helper.allowed(user.id)||user instanceof AdminUser"));
  }
}
