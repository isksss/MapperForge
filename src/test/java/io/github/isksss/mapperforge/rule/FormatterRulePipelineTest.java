package io.github.isksss.mapperforge.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.IfElementNode;
import io.github.isksss.mapperforge.ast.mapper.ResultMapElementNode;
import io.github.isksss.mapperforge.ast.mapper.SelectElementNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.format.FormatterContext;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class FormatterRulePipelineTest {
  @Test
  void appliesAttributeOgnlAndSqlRulesWithoutMutatingInput() {
    SelectElementNode input =
        new SelectElementNode(
            List.of(new AttributeNode("id", "find")),
            List.of(
                new IfElementNode(
                    List.of(new AttributeNode("test", "id!=null&&name!='A>B and C'")),
                    List.of(new TextNode(TextType.SQL, "select id, name from users"))),
                new ResultMapElementNode(
                    List.of(
                        new AttributeNode("type", "User"),
                        new AttributeNode("id", "user"),
                        new AttributeNode("column", "ignored")),
                    List.of())));
    FormatterConfig config =
        new FormatterConfig(
            FormatterConfig.defaults().dialect(),
            FormatterConfig.defaults().formatterVersion(),
            FormatterConfig.defaults().include(),
            FormatterConfig.defaults().exclude(),
            FormatterConfig.defaults().indentSize(),
            FormatterConfig.defaults().maxLineLength(),
            FormatterConfig.defaults().lineEnding(),
            FormatterConfig.defaults().sqlFormatStyle(),
            FormatterConfig.defaults().tagWrapStyle(),
            FormatterConfig.defaults().attributeLayout(),
            FormatterConfig.defaults().preserveWhitespace(),
            FormatterConfig.defaults().preserveCdata(),
            FormatterConfig.defaults().formatSqlInsideCdata(),
            FormatterConfig.defaults().strict(),
            Map.of("resultMap", List.of("id", "type")));

    SelectElementNode output =
        (SelectElementNode)
            FormatterRulePipeline.v1()
                .apply(input, new FormatterContext(config, new SourceFile("mapper.xml", "")));

    assertNotSame(input, output);
    IfElementNode formattedIf = (IfElementNode) output.children().getFirst();
    assertEquals("id != null && name != 'A>B and C'", formattedIf.attributes().getFirst().value());
    assertEquals(
        """
        SELECT
            id,
            name
        FROM users""",
        ((TextNode) formattedIf.children().getFirst()).value());
    ResultMapElementNode resultMap = (ResultMapElementNode) output.children().get(1);
    assertEquals(
        List.of("id", "type", "column"),
        resultMap.attributes().stream().map(AttributeNode::name).toList());
    assertEquals(
        "id!=null&&name!='A>B and C'",
        ((IfElementNode) input.children().getFirst()).attributes().getFirst().value());
  }
}
