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
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
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
  void parsesSelectJoins() {
    SelectStatement statement =
        (SelectStatement)
            parse(
                "select u.id from users u left join orders o on o.user_id = u.id "
                    + "inner join profiles p on p.user_id = u.id where o.total > 0");

    assertEquals("users u", statement.from());
    assertEquals(2, statement.joins().size());
    assertEquals("LEFT JOIN", statement.joins().get(0).kind());
    assertEquals("orders o", statement.joins().get(0).table());
    assertInstanceOf(BinaryExpression.class, statement.joins().get(0).on());
    assertEquals("INNER JOIN", statement.joins().get(1).kind());
    assertEquals("profiles p", statement.joins().get(1).table());
  }

  @Test
  void parsesSelectWindowAndFetch() {
    SelectStatement statement =
        (SelectStatement)
            parse(
                "select tenant_id, row_number() over w from users "
                    + "window w as (partition by tenant_id order by created_at) "
                    + "fetch first 10 rows only");

    assertEquals("users", statement.from());
    assertEquals(1, statement.windows().size());
    assertEquals("w", statement.windows().getFirst().name());
    assertEquals(
        "partition by tenant_id order by created_at", statement.windows().getFirst().spec());
    assertEquals("FETCH FIRST 10 ROWS ONLY", statement.fetch().raw());
    assertEquals("10", ((LiteralExpression) statement.fetch().count()).value());
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
  void parsesInsertMultipleValueRows() {
    InsertStatement statement =
        (InsertStatement) parse("insert into users (id, name) values (1, 'a'), (2, 'b')");

    assertEquals("users", statement.table());
    assertEquals(2, statement.valueRows().size());
    assertEquals(2, statement.valueRows().getFirst().size());
    assertEquals("1", ((LiteralExpression) statement.valueRows().getFirst().getFirst()).value());
    assertEquals("b", ((LiteralExpression) statement.valueRows().get(1).get(1)).value());
  }

  @Test
  void parsesInsertSelectAndReturning() {
    InsertStatement statement =
        (InsertStatement)
            parse(
                "insert into archived_users (id, name) select id, name from users where active = 0 "
                    + "returning id, name");

    assertEquals("archived_users", statement.table());
    assertEquals(java.util.List.of("id", "name"), statement.columns());
    assertInstanceOf(SelectStatement.class, statement.selectSource());
    assertEquals(2, statement.returning().size());
    assertEquals("id", ((ColumnExpression) statement.returning().getFirst()).name());
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
  void parsesDeleteUsingAndReturning() {
    DeleteStatement statement =
        (DeleteStatement)
            parse(
                "delete from users using archived_users au "
                    + "where users.id = au.id returning users.id");

    assertEquals("users", statement.table());
    assertEquals("archived_users au", statement.using());
    assertInstanceOf(BinaryExpression.class, statement.where());
    assertEquals(1, statement.returning().size());
    assertEquals("users.id", ((ColumnExpression) statement.returning().getFirst()).name());
  }

  @Test
  void fallsBackToUnknownStatement() {
    assertInstanceOf(UnknownStatement.class, parse("merge into users using source"));
  }

  @Test
  void keepsUnsupportedWhereExpressionAsUnknownExpression() {
    SelectStatement statement =
        (SelectStatement) parse("select id from users where name is not null");

    UnknownExpression where = assertInstanceOf(UnknownExpression.class, statement.where());
    assertEquals("name is not null", where.raw());
  }

  @Test
  void parsesQuotedIdentifiersInSelectStatement() {
    SelectStatement statement =
        (SelectStatement)
            parse("select \"user\".\"id\", `user`.`name` from `user` where `user`.`id` = #{id}");

    assertEquals("\"user\".\"id\"", ((ColumnExpression) statement.selectItems().get(0)).name());
    assertEquals("`user`.`name`", ((ColumnExpression) statement.selectItems().get(1)).name());
    assertEquals("`user`", statement.from());
    BinaryExpression where = assertInstanceOf(BinaryExpression.class, statement.where());
    assertEquals("`user`.`id`", ((ColumnExpression) where.left()).name());
  }

  @Test
  void stripsOuterWhitespaceAndIgnoresCommentsForStatementClassification() {
    SelectStatement statement =
        (SelectStatement)
            parse(
                """

                -- leading comment
                select id from users /* trailing comment */
                """);

    assertEquals(
        "-- leading comment\nselect id from users /* trailing comment */", statement.raw());
    assertEquals("users", statement.from());
    assertEquals(1, statement.selectItems().size());
  }

  @Test
  void blankSqlFallsBackToUnknownStatementWithStrippedRawSql() {
    UnknownStatement statement = (UnknownStatement) parse(" \n\t ");

    assertEquals("", statement.raw());
  }

  private Statement parse(String sql) {
    return new SqlStatementParser(sql).parse();
  }
}
