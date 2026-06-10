package io.github.isksss.mapperforge.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.isksss.mapperforge.MapperForge;
import io.github.isksss.mapperforge.config.Dialect;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.config.SqlPrinter;
import io.github.isksss.mapperforge.source.SourceFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
final class MybatisDatabaseIntegrationTest {
  @Container
  private static final PostgreSQLContainer<?> POSTGRESQL =
      new PostgreSQLContainer<>("postgres:17-alpine");

  @Container private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

  @Test
  void formattedMapperRunsAgainstPostgresql() throws IOException {
    assertFormattedMapperRuns(
        POSTGRESQL,
        "org.postgresql.Driver",
        "create table users (id bigint primary key, name varchar(100) not null, email varchar(200) not null, deleted boolean not null)",
        "golden/mybatis-db/before.xml",
        "golden/mybatis-db/after.xml",
        config(Dialect.POSTGRESQL, SqlPrinter.LEGACY));
  }

  @Test
  void formattedMapperRunsAgainstMysql() throws IOException {
    assertFormattedMapperRuns(
        MYSQL,
        "com.mysql.cj.jdbc.Driver",
        "create table users (id bigint primary key, name varchar(100) not null, email varchar(200) not null, deleted boolean not null)",
        "golden/mybatis-db/before.xml",
        "golden/mybatis-db/after.xml",
        config(Dialect.MYSQL, SqlPrinter.LEGACY));
  }

  @Test
  void astSqlPrinterMapperRunsAgainstPostgresql() throws IOException {
    assertFormattedMapperRuns(
        POSTGRESQL,
        "org.postgresql.Driver",
        "create table users (id bigint primary key, name varchar(100) not null, email varchar(200) not null, deleted boolean not null)",
        "golden/mybatis-db-ast/before.xml",
        "golden/mybatis-db-ast/after.xml",
        config(Dialect.POSTGRESQL, SqlPrinter.AST));
  }

  @Test
  void astSqlPrinterMapperRunsAgainstMysql() throws IOException {
    assertFormattedMapperRuns(
        MYSQL,
        "com.mysql.cj.jdbc.Driver",
        "create table users (id bigint primary key, name varchar(100) not null, email varchar(200) not null, deleted boolean not null)",
        "golden/mybatis-db-ast/before.xml",
        "golden/mybatis-db-ast/after.xml",
        config(Dialect.MYSQL, SqlPrinter.AST));
  }

  private void assertFormattedMapperRuns(
      JdbcDatabaseContainer<?> container,
      String driverClassName,
      String createTableSql,
      String beforeResource,
      String afterResource,
      FormatterConfig config)
      throws IOException {
    String before = resource(beforeResource);
    String expected = resource(afterResource);
    String formatted = new MapperForge().format(new SourceFile("UserMapper.xml", before), config);
    assertEquals(expected, formatted);

    SqlSessionFactory sqlSessionFactory =
        sqlSessionFactory(container, driverClassName, createTableSql, formatted);
    try (var session = sqlSessionFactory.openSession(true)) {
      UserMapper mapper = session.getMapper(UserMapper.class);
      mapper.insertUser(new UserRow(1L, "Alice", "alice@example.test"));
      mapper.insertUser(new UserRow(2L, "Bob", "bob@example.test"));

      assertEquals(
          List.of(new UserRow(1L, "Alice", "alice@example.test")),
          mapper.findUsers(List.of(1L, 2L), "Alice"));
    }
  }

  private FormatterConfig config(Dialect dialect, SqlPrinter sqlPrinter) {
    FormatterConfig defaults = FormatterConfig.defaults();
    return new FormatterConfig(
        dialect,
        defaults.formatterVersion(),
        defaults.include(),
        defaults.exclude(),
        defaults.indentSize(),
        defaults.maxLineLength(),
        defaults.lineEnding(),
        defaults.sqlFormatStyle(),
        sqlPrinter,
        defaults.tagWrapStyle(),
        defaults.attributeLayout(),
        defaults.preserveWhitespace(),
        defaults.preserveCdata(),
        defaults.formatSqlInsideCdata(),
        defaults.strict(),
        defaults.attributeOrder());
  }

  private SqlSessionFactory sqlSessionFactory(
      JdbcDatabaseContainer<?> container,
      String driverClassName,
      String createTableSql,
      String mapperXml) {
    DataSource dataSource =
        new PooledDataSource(
            driverClassName,
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword());
    Environment environment =
        new Environment("integration", new JdbcTransactionFactory(), dataSource);
    Configuration configuration = new Configuration(environment);
    configuration.addMapper(UserMapper.class);
    XMLMapperBuilder mapperBuilder =
        new XMLMapperBuilder(
            new ByteArrayInputStream(mapperXml.getBytes(StandardCharsets.UTF_8)),
            configuration,
            "UserMapper.xml",
            configuration.getSqlFragments());
    mapperBuilder.parse();

    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    try (var session = sqlSessionFactory.openSession(true)) {
      var statement = session.getConnection().createStatement();
      statement.execute("drop table if exists users");
      statement.execute(createTableSql);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to initialize integration database", e);
    }
    return sqlSessionFactory;
  }

  private String resource(String name) throws IOException {
    try (var stream = Resources.getResourceAsStream(name)) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  interface UserMapper {
    int insertUser(UserRow user);

    List<UserRow> findUsers(
        @org.apache.ibatis.annotations.Param("ids") List<Long> ids,
        @org.apache.ibatis.annotations.Param("name") String name);
  }

  public static final class UserRow {
    private Long id;
    private String name;
    private String email;

    public UserRow() {}

    public UserRow(Long id, String name, String email) {
      this.id = id;
      this.name = name;
      this.email = email;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof UserRow userRow)) {
        return false;
      }
      return Objects.equals(id, userRow.id)
          && Objects.equals(name, userRow.name)
          && Objects.equals(email, userRow.email);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, name, email);
    }
  }
}
