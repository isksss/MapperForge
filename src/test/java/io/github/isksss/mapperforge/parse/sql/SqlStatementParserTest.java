package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.isksss.mapperforge.ast.sql.BinaryExpression;
import io.github.isksss.mapperforge.ast.sql.ColumnExpression;
import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.FunctionExpression;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.PlaceholderExpression;
import io.github.isksss.mapperforge.ast.sql.SelectStatement;
import io.github.isksss.mapperforge.ast.sql.SetOperationStatement;
import io.github.isksss.mapperforge.ast.sql.Statement;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.ast.sql.UpdateStatement;
import io.github.isksss.mapperforge.ast.sql.WithStatement;
import org.junit.jupiter.api.Test;

final class SqlStatementParserTest {
  @Test
  void parsesSupportedStatementKinds() {
    assertInstanceOf(SelectStatement.class, parse("select id from users"));
    assertInstanceOf(InsertStatement.class, parse("insert into users (id) values (#{id})"));
    assertInstanceOf(UpdateStatement.class, parse("update users set name = #{name}"));
    assertInstanceOf(DeleteStatement.class, parse("delete from users where id = #{id}"));
    assertInstanceOf(
        WithStatement.class, parse("with u as (select id from users) select id from u"));
  }

  @Test
  void parsesSetOperations() {
    assertInstanceOf(SetOperationStatement.class, parse("select id from a union select id from b"));
    SetOperationStatement statement =
        (SetOperationStatement) parse("select id from a intersect select id from b");

    assertEquals("INTERSECT", statement.operator());
  }

  @Test
  void parsesSelectListFromAndWhereExpressions() {
    SelectStatement statement =
        (SelectStatement)
            parse(
                "select id, coalesce(name, #{fallback}) from users where id = #{id} and active = 1");

    assertEquals("users", statement.from());
    assertEquals(2, statement.selectItems().size());
    assertInstanceOf(ColumnExpression.class, statement.selectItems().get(0));
    assertInstanceOf(FunctionExpression.class, statement.selectItems().get(1));

    BinaryExpression where = assertInstanceOf(BinaryExpression.class, statement.where());
    assertEquals("AND", where.operator());
    BinaryExpression left = assertInstanceOf(BinaryExpression.class, where.left());
    assertEquals("=", left.operator());
    assertInstanceOf(PlaceholderExpression.class, left.right());
  }

  @Test
  void fallsBackToUnknownStatement() {
    assertInstanceOf(UnknownStatement.class, parse("merge into users using source"));
  }

  private Statement parse(String sql) {
    return new SqlStatementParser(sql).parse();
  }
}
