package io.github.isksss.mapperforge.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.source.SourceFile;
import org.junit.jupiter.api.Test;

final class ValidatorTest {
  private final Validator validator = new Validator();

  @Test
  void acceptsWhitespaceOnlyChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml", "<mapper namespace=\"sample\"><unknown a=\"1\">text</unknown></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            """
            <mapper namespace="sample">
                <unknown a="1">
                    text
                </unknown>
            </mapper>
            """);

    assertTrue(validator.validate(before, after, FormatterConfig.defaults()).success());
  }

  @Test
  void rejectsGenericElementAttributeChanges() {
    SourceFile before =
        new SourceFile("before.xml", "<mapper namespace=\"sample\"><unknown a=\"1\"/></mapper>");
    SourceFile after =
        new SourceFile("after.xml", "<mapper namespace=\"sample\"><unknown a=\"2\"/></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertFalse(result.success());
    assertEquals(ErrorType.GENERIC_ELEMENT, result.errors().getFirst().type());
  }

  @Test
  void rejectsCommentChanges() {
    SourceFile before =
        new SourceFile("before.xml", "<mapper namespace=\"sample\"><!-- a --></mapper>");
    SourceFile after =
        new SourceFile("after.xml", "<mapper namespace=\"sample\"><!-- b --></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertFalse(result.success());
    assertEquals(ErrorType.COMMENT, result.errors().getFirst().type());
  }

  @Test
  void rejectsCdataChangesWhenPreserved() {
    SourceFile before =
        new SourceFile(
            "before.xml", "<mapper namespace=\"sample\"><select><![CDATA[a]]></select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml", "<mapper namespace=\"sample\"><select><![CDATA[b]]></select></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertFalse(result.success());
    assertEquals(ErrorType.CDATA, result.errors().getFirst().type());
  }
}
