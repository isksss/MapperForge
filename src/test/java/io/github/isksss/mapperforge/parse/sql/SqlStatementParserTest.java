package io.github.isksss.mapperforge.parse.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.github.isksss.mapperforge.ast.sql.BinaryExpression;
import io.github.isksss.mapperforge.ast.sql.ColumnExpression;
import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.FunctionExpression;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.LiteralExpression;
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
  void parsesSelectGroupHavingOrderLimitAndOffset() {
    SelectStatement statement =
        (SelectStatement)
            parse(
                "select tenant_id, count(*) from users where active = 1 group by tenant_id "
                    + "having count(*) > 10 order by tenant_id desc limit 20 offset 40");

    assertEquals("users", statement.from());
    assertInstanceOf(BinaryExpression.class, statement.where());
    assertEquals(1, statement.groupBy().size());
    assertEquals("tenant_id", ((ColumnExpression) statement.groupBy().getFirst()).name());
    assertInstanceOf(BinaryExpression.class, statement.having());
    assertEquals(1, statement.orderBy().size());
    assertEquals(
        "tenant_id", ((ColumnExpression) statement.orderBy().getFirst().expression()).name());
    assertEquals("DESC", statement.orderBy().getFirst().direction());
    assertEquals("20", ((LiteralExpression) statement.limit()).value());
    assertEquals("40", ((LiteralExpression) statement.offset()).value());
  }

  @Test
  void parsesInsertColumnsAndValues() {
    InsertStatement statement =
        (InsertStatement) parse("insert into users (id, name) values (#{id}, #{name})");

    assertEquals("users", statement.table());
    assertEquals(java.util.List.of("id", "name"), statement.columns());
    assertEquals(2, statement.values().size());
    assertInstanceOf(PlaceholderExpression.class, statement.values().get(0));
  }

  @Test
  void parsesUpdateAssignmentsAndWhere() {
    UpdateStatement statement =
        (UpdateStatement)
            parse("update users set name = #{name}, updated_at = now() where id = #{id}");

    assertEquals("users", statement.table());
    assertEquals(2, statement.assignments().size());
    assertEquals("name", statement.assignments().getFirst().column());
    assertInstanceOf(PlaceholderExpression.class, statement.assignments().getFirst().value());
    assertInstanceOf(BinaryExpression.class, statement.where());
  }

  @Test
  void parsesDeleteTableAndWhere() {
    DeleteStatement statement = (DeleteStatement) parse("delete from users where active = 0");

    assertEquals("users", statement.table());
    BinaryExpression where = assertInstanceOf(BinaryExpression.class, statement.where());
    assertEquals("=", where.operator());
    assertInstanceOf(LiteralExpression.class, where.right());
  }

  @Test
  void fallsBackToUnknownStatement() {
    assertInstanceOf(UnknownStatement.class, parse("merge into users using source"));
  }

  private Statement parse(String sql) {
    return new SqlStatementParser(sql).parse();
  }
}
