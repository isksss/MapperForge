package io.github.isksss.mapperforge.print;

import io.github.isksss.mapperforge.ast.sql.ArrayExpression;
import io.github.isksss.mapperforge.ast.sql.BetweenExpression;
import io.github.isksss.mapperforge.ast.sql.BinaryExpression;
import io.github.isksss.mapperforge.ast.sql.CaseExpression;
import io.github.isksss.mapperforge.ast.sql.CastExpression;
import io.github.isksss.mapperforge.ast.sql.ColumnExpression;
import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.ExistsExpression;
import io.github.isksss.mapperforge.ast.sql.Expression;
import io.github.isksss.mapperforge.ast.sql.FunctionExpression;
import io.github.isksss.mapperforge.ast.sql.InExpression;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.JsonExpression;
import io.github.isksss.mapperforge.ast.sql.LiteralExpression;
import io.github.isksss.mapperforge.ast.sql.ParameterExpression;
import io.github.isksss.mapperforge.ast.sql.PlaceholderExpression;
import io.github.isksss.mapperforge.ast.sql.PlaceholderType;
import io.github.isksss.mapperforge.ast.sql.RowExpression;
import io.github.isksss.mapperforge.ast.sql.SelectStatement;
import io.github.isksss.mapperforge.ast.sql.SetOperationStatement;
import io.github.isksss.mapperforge.ast.sql.Statement;
import io.github.isksss.mapperforge.ast.sql.SubQueryExpression;
import io.github.isksss.mapperforge.ast.sql.UnaryExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.ast.sql.UpdateStatement;
import io.github.isksss.mapperforge.ast.sql.WindowExpression;
import io.github.isksss.mapperforge.ast.sql.WithStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class SqlAstPrinter {
  public Doc print(Statement statement) {
    List<Doc> docs = new ArrayList<>();
    List<String> lines = lines(statement);
    for (int i = 0; i < lines.size(); i++) {
      if (i > 0) {
        docs.add(Docs.hardLine());
      }
      docs.add(Docs.text(lines.get(i)));
    }
    return Docs.concat(docs);
  }

  private List<String> lines(Statement statement) {
    return switch (statement) {
      case SelectStatement select -> selectLines(select);
      case InsertStatement insert -> insertLines(insert);
      case UpdateStatement update -> updateLines(update);
      case DeleteStatement delete -> deleteLines(delete);
      case SetOperationStatement setOperation -> List.of(normalize(setOperation.raw()));
      case WithStatement with -> List.of(normalize(with.raw()));
      case UnknownStatement unknown -> List.of(normalize(unknown.raw()));
    };
  }

  private List<String> selectLines(SelectStatement select) {
    List<String> lines = new ArrayList<>();
    lines.add("SELECT");
    for (int i = 0; i < select.selectItems().size(); i++) {
      String suffix = i == select.selectItems().size() - 1 ? "" : ",";
      lines.add("    " + expression(select.selectItems().get(i)) + suffix);
    }
    if (!select.from().isBlank()) {
      lines.add("FROM " + select.from());
    }
    for (SelectStatement.JoinItem join : select.joins()) {
      lines.add(join.kind() + " " + join.table());
      lines.add("    ON " + expression(join.on()));
    }
    addExpressionLine(lines, "WHERE", select.where());
    addExpressionListLine(lines, "GROUP BY", select.groupBy());
    addExpressionLine(lines, "HAVING", select.having());
    if (!select.orderBy().isEmpty()) {
      lines.add(
          "ORDER BY "
              + select.orderBy().stream()
                  .map(
                      order ->
                          expression(order.expression())
                              + (order.direction().isBlank() ? "" : " " + order.direction()))
                  .collect(Collectors.joining(", ")));
    }
    addExpressionLine(lines, "LIMIT", select.limit());
    addExpressionLine(lines, "OFFSET", select.offset());
    if (!select.windows().isEmpty()) {
      lines.add(
          "WINDOW "
              + select.windows().stream()
                  .map(window -> window.name() + " AS (" + window.spec() + ")")
                  .collect(Collectors.joining(", ")));
    }
    if (!select.fetch().raw().isBlank()) {
      lines.add(select.fetch().raw());
    }
    return lines;
  }

  private List<String> insertLines(InsertStatement insert) {
    List<String> lines = new ArrayList<>();
    lines.add("INSERT INTO " + insert.table() + columns(insert.columns()));
    if (!insert.valueRows().isEmpty()) {
      lines.add("VALUES");
      for (int i = 0; i < insert.valueRows().size(); i++) {
        String suffix = i == insert.valueRows().size() - 1 ? "" : ",";
        lines.add("    (" + expressions(insert.valueRows().get(i)) + ")" + suffix);
      }
    } else if (!(insert.selectSource() instanceof UnknownStatement)) {
      lines.addAll(lines(insert.selectSource()));
    }
    addExpressionListLine(lines, "RETURNING", insert.returning());
    return lines;
  }

  private List<String> updateLines(UpdateStatement update) {
    List<String> lines = new ArrayList<>();
    lines.add("UPDATE " + update.table());
    if (!update.assignments().isEmpty()) {
      lines.add("SET");
      for (int i = 0; i < update.assignments().size(); i++) {
        UpdateStatement.Assignment assignment = update.assignments().get(i);
        String suffix = i == update.assignments().size() - 1 ? "" : ",";
        lines.add("    " + assignment.column() + " = " + expression(assignment.value()) + suffix);
      }
    }
    addExpressionLine(lines, "WHERE", update.where());
    return lines;
  }

  private List<String> deleteLines(DeleteStatement delete) {
    List<String> lines = new ArrayList<>();
    lines.add("DELETE FROM " + delete.table());
    if (!delete.using().isBlank()) {
      lines.add("USING " + delete.using());
    }
    addExpressionLine(lines, "WHERE", delete.where());
    addExpressionListLine(lines, "RETURNING", delete.returning());
    return lines;
  }

  private void addExpressionLine(List<String> lines, String keyword, Expression expression) {
    if (!isEmpty(expression)) {
      lines.add(keyword + " " + expression(expression));
    }
  }

  private void addExpressionListLine(
      List<String> lines, String keyword, List<Expression> expressions) {
    if (!expressions.isEmpty()) {
      lines.add(keyword + " " + expressions(expressions));
    }
  }

  private String columns(List<String> columns) {
    return columns.isEmpty() ? "" : " (" + String.join(", ", columns) + ")";
  }

  private String expressions(List<Expression> expressions) {
    return expressions.stream().map(this::expression).collect(Collectors.joining(", "));
  }

  private String expression(Expression expression) {
    return switch (expression) {
      case LiteralExpression literal -> literal.value();
      case ColumnExpression column -> column.name();
      case ParameterExpression parameter -> parameter.name();
      case PlaceholderExpression placeholder ->
          placeholderPrefix(placeholder) + "{" + placeholder.expression() + "}";
      case FunctionExpression function ->
          function.name() + "(" + expressions(function.arguments()) + ")";
      case BinaryExpression binary ->
          expression(binary.left()) + " " + binary.operator() + " " + expression(binary.right());
      case UnaryExpression unary -> unary.operator() + " " + expression(unary.expression());
      case CaseExpression caseExpression -> caseExpression.toString();
      case ExistsExpression exists -> "EXISTS (" + exists.subQuery() + ")";
      case InExpression in ->
          expression(in.expression()) + " IN (" + expressions(in.values()) + ")";
      case BetweenExpression between ->
          expression(between.expression())
              + " BETWEEN "
              + expression(between.lower())
              + " AND "
              + expression(between.upper());
      case CastExpression cast ->
          "CAST(" + expression(cast.expression()) + " AS " + cast.typeName() + ")";
      case SubQueryExpression subQuery -> "(" + subQuery.sql() + ")";
      case ArrayExpression array -> "ARRAY[" + expressions(array.values()) + "]";
      case RowExpression row -> "ROW(" + expressions(row.values()) + ")";
      case JsonExpression json ->
          expression(json.expression()) + " " + json.operator() + " " + expression(json.path());
      case WindowExpression window ->
          expression(window.expression()) + " OVER (" + window.windowSpec() + ")";
      case UnknownExpression unknown -> normalize(unknown.raw());
    };
  }

  private boolean isEmpty(Expression expression) {
    return expression instanceof UnknownExpression unknown && unknown.raw().isBlank();
  }

  private String placeholderPrefix(PlaceholderExpression placeholder) {
    return placeholder.type() == PlaceholderType.HASH ? "#" : "$";
  }

  private String normalize(String raw) {
    return raw.strip().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
  }
}
