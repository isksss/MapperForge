package io.github.isksss.mapperforge.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.CommentType;
import io.github.isksss.mapperforge.ast.mapper.GenericElementNode;
import io.github.isksss.mapperforge.ast.mapper.IfElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperElementNode;
import io.github.isksss.mapperforge.ast.mapper.SelectElementNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.source.SourceFile;
import org.junit.jupiter.api.Test;

final class MapperXmlParserTest {
  private final MapperXmlParser parser = new MapperXmlParser();

  @Test
  void parsesMyBatisElementsAsDedicatedNodes() {
    MapperElementNode mapper =
        parser
            .parseMapper(
                new SourceFile(
                    "all-tags.xml",
                    """
                    <mapper namespace="sample.Mapper">
                      <select id="select"><if test="id != null">select id</if></select>
                      <insert id="insert">insert</insert>
                      <update id="update">update</update>
                      <delete id="delete">delete</delete>
                      <sql id="cols">id</sql>
                      <resultMap id="rm" type="User">
                        <association property="a"></association>
                        <collection property="c"></collection>
                        <constructor><arg column="id"></arg><idArg column="id"></idArg></constructor>
                        <discriminator column="kind"><case value="A"></case></discriminator>
                      </resultMap>
                      <choose><when test="x">x</when><otherwise>y</otherwise></choose>
                      <foreach collection="ids" item="id"></foreach>
                      <trim></trim><where></where><set></set><include refid="cols"></include><bind name="x" value="y"></bind>
                      <unknown-tag value="1"></unknown-tag>
                    </mapper>
                    """))
            .orElseThrow();

    assertEquals("mapper", mapper.tagName());
    assertInstanceOf(SelectElementNode.class, mapper.children().getFirst());
    SelectElementNode select = (SelectElementNode) mapper.children().getFirst();
    assertInstanceOf(IfElementNode.class, select.children().getFirst());
    assertTrue(
        mapper.children().stream()
            .anyMatch(
                child ->
                    child instanceof GenericElementNode generic
                        && generic.tagName().equals("unknown-tag")));
  }

  @Test
  void returnsEmptyWhenRootIsNotMapper() {
    assertTrue(parser.parseMapper(new SourceFile("not-mapper.xml", "<root></root>")).isEmpty());
  }

  @Test
  void marksSqlTextAndParsesCdataSqlRecoverably() {
    MapperElementNode mapper =
        parser
            .parseMapper(
                new SourceFile(
                    "sql.xml",
                    """
                    <mapper namespace="sample.Mapper">
                      <select id="find">select id from users</select>
                      <select id="unknown"><![CDATA[merge into users using source]]></select>
                    </mapper>
                    """))
            .orElseThrow();

    SelectElementNode select = (SelectElementNode) mapper.children().get(0);
    TextNode sqlText = (TextNode) select.children().getFirst();
    assertEquals(TextType.SQL, sqlText.type());

    SelectElementNode unknownSelect = (SelectElementNode) mapper.children().get(1);
    CDataNode cdata = (CDataNode) unknownSelect.children().getFirst();
    assertInstanceOf(UnknownStatement.class, cdata.parsed().orElseThrow());
  }

  @Test
  void exposesSqlCommentsAsSqlCommentNodes() {
    MapperElementNode mapper =
        parser
            .parseMapper(
                new SourceFile(
                    "sql-comments.xml",
                    """
                    <mapper namespace="sample.Mapper">
                      <select id="find">select id -- keep selected columns
                        from users where active = 1 /* keep filter */
                      </select>
                    </mapper>
                    """))
            .orElseThrow();

    SelectElementNode select = (SelectElementNode) mapper.children().getFirst();

    assertTrue(
        select.children().stream()
            .anyMatch(
                child ->
                    child instanceof CommentNode comment
                        && comment.type() == CommentType.SQL
                        && comment.content().equals("-- keep selected columns")));
    assertTrue(
        select.children().stream()
            .anyMatch(
                child ->
                    child instanceof CommentNode comment
                        && comment.type() == CommentType.SQL
                        && comment.content().equals("/* keep filter */")));
  }
}
