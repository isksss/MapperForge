package io.github.isksss.mapperforge.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.error.ErrorCode;
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
  void rejectsWhitespaceChangesWhenPreserveWhitespaceIsEnabled() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users</select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            """
            <mapper namespace="sample">
                <select id="find">
                    select id from users
                </select>
            </mapper>
            """);

    ValidationResult result = validator.validate(before, after, preserveWhitespaceConfig());

    assertFalse(result.success());
    assertEquals(ErrorType.WHITESPACE, result.errors().getFirst().type());
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
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
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
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
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
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
  }

  @Test
  void acceptsCdataSqlFormattingWhenConfigured() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select><![CDATA[select id from users where active = 1]]></select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            """
            <mapper namespace="sample">
                <select>
                    <![CDATA[SELECT
                id
            FROM users
            WHERE active = 1]]>
                </select>
            </mapper>
            """);

    assertTrue(validator.validate(before, after, formatSqlInsideCdataConfig()).success());
  }

  @Test
  void acceptsCdataWrapperRemovalWhenCdataIsNotPreserved() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select><![CDATA[select id from users where age < #{age}]]></select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            "<mapper namespace=\"sample\"><select>select id from users where age &lt; #{age}</select></mapper>");

    assertTrue(validator.validate(before, after, cdataNotPreservedConfig()).success());
  }

  @Test
  void rejectsPlaceholderOptionChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select * from users where id = #{id,jdbcType=BIGINT}</select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select * from users where id = #{id,jdbcType=VARCHAR}</select></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertEquals(ErrorType.PLACEHOLDER, result.errors().getFirst().type());
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
  }

  @Test
  void acceptsSqlFormattingOnlyChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users where active = 1</select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            """
            <mapper namespace="sample">
                <select id="find">
                    SELECT id
                    FROM users
                    WHERE active = 1
                </select>
            </mapper>
            """);

    assertTrue(validator.validate(before, after, FormatterConfig.defaults()).success());
  }

  @Test
  void rejectsSqlStatementSemanticChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users where active = 1</select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            "<mapper namespace=\"sample\"><select id=\"find\">select id from users where active = 0</select></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertFalse(result.success());
    assertEquals(ErrorType.STATEMENT, result.errors().getFirst().type());
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
  }

  @Test
  void acceptsOgnlFormattingOnlyChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\"><if test=\"name!=null&amp;&amp;status in {'ACTIVE','NEW'}\">and name = #{name}</if></select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            """
            <mapper namespace="sample">
                <select id="find">
                    <if test="name != null &amp;&amp; status in {'ACTIVE','NEW'}">
                        AND name = #{name}
                    </if>
                </select>
            </mapper>
            """);

    assertTrue(validator.validate(before, after, FormatterConfig.defaults()).success());
  }

  @Test
  void rejectsOgnlSemanticChanges() {
    SourceFile before =
        new SourceFile(
            "before.xml",
            "<mapper namespace=\"sample\"><select id=\"find\"><if test=\"name!=null\">and name = #{name}</if></select></mapper>");
    SourceFile after =
        new SourceFile(
            "after.xml",
            "<mapper namespace=\"sample\"><select id=\"find\"><if test=\"name==null\">and name = #{name}</if></select></mapper>");

    ValidationResult result = validator.validate(before, after, FormatterConfig.defaults());

    assertFalse(result.success());
    assertEquals(ErrorType.EXPRESSION, result.errors().getFirst().type());
    assertEquals(ErrorCode.VALIDATION_ERROR, result.errors().getFirst().code());
  }

  private static FormatterConfig preserveWhitespaceConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        defaults.sqlPrinter(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        true,
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        defaults.attributeOrder());
  }

  private static FormatterConfig cdataNotPreservedConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        defaults.sqlPrinter(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        false,
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        defaults.attributeOrder());
  }

  private static FormatterConfig formatSqlInsideCdataConfig() {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        defaults.dialect(),
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        defaults.sqlPrinter(),
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        true,
        defaults.strict(),
        defaults.attributeOrder());
  }
}
