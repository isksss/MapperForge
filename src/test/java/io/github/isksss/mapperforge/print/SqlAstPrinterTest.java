package io.github.isksss.mapperforge.print;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import org.junit.jupiter.api.Test;

final class SqlAstPrinterTest {
  @Test
  void printsSelectStatementFromAst() {
    String sql =
        "select u.id, u.name from users u left join orders o on o.user_id = u.id "
            + "where o.total > 0 order by u.id desc limit 10";

    assertEquals(
        """
        SELECT
            u.id,
            u.name
        FROM users u
        LEFT JOIN orders o
            ON o.user_id = u.id
        WHERE o.total > 0
        ORDER BY u.id DESC
        LIMIT 10""",
        print(sql));
  }

  @Test
  void printsInsertUpdateAndDeleteStatementsFromAst() {
    assertEquals(
        """
        INSERT INTO users (id, name)
        VALUES
            (#{id}, #{name})
        RETURNING id""",
        print("insert into users (id, name) values (#{id}, #{name}) returning id"));

    assertEquals(
        """
        UPDATE users
        SET
            name = #{name},
            updated_at = now()
        WHERE id = #{id}""",
        print("update users set name = #{name}, updated_at = now() where id = #{id}"));

    assertEquals(
        """
        DELETE FROM users
        USING archived_users au
        WHERE users.id = au.id
        RETURNING users.id""",
        print(
            "delete from users using archived_users au where users.id = au.id returning users.id"));
  }

  private static String print(String sql) {
    return LayoutEngine.render(
        new SqlAstPrinter().print(new SqlStatementParser(sql).parse()), FormatterConfig.defaults());
  }
}
