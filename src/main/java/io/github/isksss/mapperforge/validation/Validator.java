package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.GenericElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.ast.ognl.OgnlBinaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCallExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlLiteralExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlNameExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnaryExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlUnknownExpression;
import io.github.isksss.mapperforge.ast.sql.DeleteStatement;
import io.github.isksss.mapperforge.ast.sql.Expression;
import io.github.isksss.mapperforge.ast.sql.InsertStatement;
import io.github.isksss.mapperforge.ast.sql.SelectStatement;
import io.github.isksss.mapperforge.ast.sql.SetOperationStatement;
import io.github.isksss.mapperforge.ast.sql.Statement;
import io.github.isksss.mapperforge.ast.sql.UnknownExpression;
import io.github.isksss.mapperforge.ast.sql.UnknownStatement;
import io.github.isksss.mapperforge.ast.sql.UpdateStatement;
import io.github.isksss.mapperforge.ast.sql.WithStatement;
import io.github.isksss.mapperforge.config.FormatterConfig;
import io.github.isksss.mapperforge.parse.MapperXmlParser;
import io.github.isksss.mapperforge.parse.ognl.OgnlExpressionParser;
import io.github.isksss.mapperforge.parse.sql.PlaceholderParser;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {
  private static final Pattern PLACEHOLDER = Pattern.compile("[$#]\\{[^}]+}");
  private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
  private static final Pattern CDATA = Pattern.compile("<!\\[CDATA\\[.*?]]>", Pattern.DOTALL);
  private final MapperXmlParser parser = new MapperXmlParser();
  private final PlaceholderParser placeholderParser = new PlaceholderParser();

  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    List<ValidationError> errors = new ArrayList<>();
    compareAst(before, after, errors);
    comparePlaceholders(before.content(), after.content(), errors);
    compareSqlStatements(before, after, errors);
    return new ValidationResult(errors.isEmpty(), List.copyOf(errors));
  }

  private void comparePlaceholders(String before, String after, List<ValidationError> errors) {
    var beforePlaceholders =
        find(PLACEHOLDER, before).stream().map(placeholderParser::parse).toList();
    var afterPlaceholders =
        find(PLACEHOLDER, after).stream().map(placeholderParser::parse).toList();
    if (!beforePlaceholders.equals(afterPlaceholders)) {
      errors.add(
          new ValidationError(
              ErrorType.PLACEHOLDER, "Formatted XML changed placeholder sequence", null));
    }
  }

  private void compareAst(SourceFile before, SourceFile after, List<ValidationError> errors) {
    var beforeAst = comparable(parser.parse(before));
    var afterAst = comparable(parser.parse(after));
    if (!beforeAst.equals(afterAst)) {
      errors.add(
          new ValidationError(
              classifyAstChange(before.content(), after.content()),
              "Formatted XML changed AST",
              null));
    }
  }

  private void compareSqlStatements(
      SourceFile before, SourceFile after, List<ValidationError> errors) {
    List<Object> beforeStatements = sqlStatementSignatures(parser.parse(before));
    List<Object> afterStatements = sqlStatementSignatures(parser.parse(after));
    if (!beforeStatements.equals(afterStatements)) {
      errors.add(
          new ValidationError(
              ErrorType.STATEMENT, "Formatted XML changed SQL statement structure", null));
    }
  }

  private ErrorType classifyAstChange(String before, String after) {
    if (!find(COMMENT, before).equals(find(COMMENT, after))) {
      return ErrorType.COMMENT;
    }
    if (!find(CDATA, before).equals(find(CDATA, after))) {
      return ErrorType.CDATA;
    }
    if (!ognlExpressionSignatures(parser.parse(new SourceFile("before.xml", before)))
        .equals(ognlExpressionSignatures(parser.parse(new SourceFile("after.xml", after))))) {
      return ErrorType.EXPRESSION;
    }
    return ErrorType.GENERIC_ELEMENT;
  }

  private MapperNode comparable(MapperNode node) {
    return switch (node) {
      case TextNode ignored -> new TextNode(TextType.PLAIN_TEXT, "");
      case CommentNode comment -> comment;
      case CDataNode cdata -> cdata;
      case ElementNode element ->
          new GenericElementNode(
              element.tagName(),
              comparableAttributes(element),
              element.children().stream().map(this::comparable).toList());
    };
  }

  private List<io.github.isksss.mapperforge.ast.mapper.AttributeNode> comparableAttributes(
      ElementNode element) {
    return element.attributes().stream()
        .map(
            attribute -> {
              if (isOgnlAttribute(element.tagName(), attribute.name())) {
                return new io.github.isksss.mapperforge.ast.mapper.AttributeNode(
                    attribute.name(), ognlExpressionSignature(attribute.value()).toString());
              }
              return attribute;
            })
        .toList();
  }

  private List<Object> sqlStatementSignatures(MapperNode node) {
    List<Object> statements = new ArrayList<>();
    collectSqlStatementSignatures(node, statements);
    return List.copyOf(statements);
  }

  private List<Object> ognlExpressionSignatures(MapperNode node) {
    List<Object> expressions = new ArrayList<>();
    collectOgnlExpressionSignatures(node, expressions);
    return List.copyOf(expressions);
  }

  private void collectOgnlExpressionSignatures(MapperNode node, List<Object> expressions) {
    if (node instanceof ElementNode element) {
      element.attributes().stream()
          .filter(attribute -> isOgnlAttribute(element.tagName(), attribute.name()))
          .map(attribute -> ognlExpressionSignature(attribute.value()))
          .forEach(expressions::add);
      element.children().forEach(child -> collectOgnlExpressionSignatures(child, expressions));
    }
  }

  private boolean isOgnlAttribute(String tagName, String attributeName) {
    return switch (tagName) {
      case "if", "when" -> "test".equals(attributeName);
      case "bind" -> "value".equals(attributeName);
      case "foreach" -> "collection".equals(attributeName);
      default -> false;
    };
  }

  private void collectSqlStatementSignatures(MapperNode node, List<Object> statements) {
    switch (node) {
      case TextNode text when text.type() == TextType.SQL ->
          statements.add(statementSignature(new SqlStatementParser(text.value()).parse()));
      case CDataNode cdata ->
          cdata
              .parsed()
              .filter(Statement.class::isInstance)
              .map(Statement.class::cast)
              .ifPresent(sql -> statements.add(statementSignature(sql)));
      case ElementNode element ->
          element.children().forEach(child -> collectSqlStatementSignatures(child, statements));
      default -> {}
    }
  }

  private Object statementSignature(Statement statement) {
    return switch (statement) {
      case SelectStatement select ->
          List.of(
              "SELECT",
              expressions(select.selectItems()),
              select.from(),
              expression(select.where()),
              expressions(select.groupBy()),
              expression(select.having()),
              select.orderBy().stream()
                  .map(order -> List.of(expression(order.expression()), order.direction()))
                  .toList(),
              expression(select.limit()),
              expression(select.offset()),
              select.joins().stream()
                  .map(join -> List.of(join.kind(), join.table(), expression(join.on())))
                  .toList(),
              select.windows(),
              List.of(select.fetch().raw(), expression(select.fetch().count())));
      case InsertStatement insert ->
          List.of(
              "INSERT",
              insert.table(),
              insert.columns(),
              expressions(insert.values()),
              insert.valueRows().stream().map(this::expressions).toList(),
              statementSignature(insert.selectSource()),
              expressions(insert.returning()));
      case UpdateStatement update ->
          List.of(
              "UPDATE",
              update.table(),
              update.assignments().stream()
                  .map(assignment -> List.of(assignment.column(), expression(assignment.value())))
                  .toList(),
              expression(update.where()));
      case DeleteStatement delete ->
          List.of(
              "DELETE",
              delete.table(),
              delete.using(),
              expression(delete.where()),
              expressions(delete.returning()));
      case SetOperationStatement setOperation ->
          List.of("SET_OPERATION", setOperation.operator(), normalizeSql(setOperation.raw()));
      case WithStatement with -> List.of("WITH", normalizeSql(with.raw()));
      case UnknownStatement unknown -> List.of("UNKNOWN", normalizeSql(unknown.raw()));
    };
  }

  private List<Object> expressions(List<Expression> expressions) {
    return expressions.stream().map(this::expression).toList();
  }

  private Object expression(Expression expression) {
    if (expression instanceof UnknownExpression unknown) {
      return List.of("UNKNOWN", normalizeSql(unknown.raw()));
    }
    return expression;
  }

  private Object ognlExpressionSignature(String expression) {
    return ognlExpression(new OgnlExpressionParser(expression).parse());
  }

  private Object ognlExpression(OgnlExpression expression) {
    return switch (expression) {
      case OgnlBinaryExpression binary ->
          List.of(
              "BINARY",
              ognlExpression(binary.left()),
              binary.operator().toLowerCase(Locale.ROOT),
              ognlExpression(binary.right()));
      case OgnlUnaryExpression unary ->
          List.of(
              "UNARY",
              unary.operator().toLowerCase(Locale.ROOT),
              ognlExpression(unary.expression()));
      case OgnlCallExpression call ->
          List.of(
              "CALL", call.name(), call.arguments().stream().map(this::ognlExpression).toList());
      case OgnlCollectionExpression collection ->
          List.of("COLLECTION", collection.values().stream().map(this::ognlExpression).toList());
      case OgnlLiteralExpression literal -> List.of("LITERAL", literal.value());
      case OgnlNameExpression name -> List.of("NAME", name.name());
      case OgnlUnknownExpression unknown -> List.of("UNKNOWN", normalizeOgnl(unknown.raw()));
    };
  }

  private String normalizeOgnl(String raw) {
    return raw.strip().replaceAll("\\s+", " ");
  }

  private String normalizeSql(String raw) {
    return raw.strip().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
  }

  private void compare(
      String name,
      Pattern pattern,
      String before,
      String after,
      ErrorType type,
      List<ValidationError> errors) {
    List<String> beforeValues = find(pattern, before);
    List<String> afterValues = find(pattern, after);
    if (!beforeValues.equals(afterValues)) {
      errors.add(new ValidationError(type, "Formatted XML changed " + name + " sequence", null));
    }
  }

  private List<String> find(Pattern pattern, String value) {
    Matcher matcher = pattern.matcher(value);
    List<String> values = new ArrayList<>();
    while (matcher.find()) {
      values.add(matcher.group());
    }
    return values;
  }
}
