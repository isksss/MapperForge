package io.github.isksss.mapperforge.validation;

import io.github.isksss.mapperforge.ast.mapper.CDataNode;
import io.github.isksss.mapperforge.ast.mapper.CommentNode;
import io.github.isksss.mapperforge.ast.mapper.CommentType;
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
import io.github.isksss.mapperforge.format.OgnlFormatter;
import io.github.isksss.mapperforge.logging.MapperForgeLoggers;
import io.github.isksss.mapperforge.parse.MapperXmlParser;
import io.github.isksss.mapperforge.parse.ognl.OgnlExpressionParser;
import io.github.isksss.mapperforge.parse.sql.PlaceholderParser;
import io.github.isksss.mapperforge.parse.sql.SqlStatementParser;
import io.github.isksss.mapperforge.source.Position;
import io.github.isksss.mapperforge.source.Range;
import io.github.isksss.mapperforge.source.SourceFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {
  private static final Pattern PLACEHOLDER = Pattern.compile("[$#]\\{[^}]+}");
  private static final Pattern CDATA = Pattern.compile("<!\\[CDATA\\[.*?]]>", Pattern.DOTALL);
  private static final Pattern XML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
  private static final Pattern SQL_COMMENT =
      Pattern.compile("--[^\\r\\n]*|/\\*.*?\\*/", Pattern.DOTALL);
  private static final Pattern XML_ENTITY =
      Pattern.compile("&(?:amp|lt|gt|quot|apos);", Pattern.CASE_INSENSITIVE);
  private static final Pattern OGNL_ATTRIBUTE =
      Pattern.compile(
          "<([A-Za-z][\\w:-]*)\\b[^>]*\\s(test|value|collection)=\"([^\"]*)\"",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern SQL_ELEMENT =
      Pattern.compile(
          "<(select|insert|update|delete|sql)\\b[^>]*>(.*?)</\\1>",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern TAG_BOUNDARY_WHITESPACE = Pattern.compile(">(\\s+)<");
  private final MapperXmlParser parser = new MapperXmlParser();
  private final PlaceholderParser placeholderParser = new PlaceholderParser();
  private final OgnlFormatter ognlFormatter = new OgnlFormatter();

  public ValidationResult validate(SourceFile before, SourceFile after, FormatterConfig config) {
    MapperForgeLoggers.VALIDATOR.debug("Validating formatted mapper XML: {}", before.fileName());
    List<ValidationError> errors = new ArrayList<>();
    compareWhitespace(before.content(), after.content(), config, errors);
    compareAst(before, after, config, errors);
    comparePlaceholders(before.content(), after.content(), errors);
    compareSqlStatements(before, after, errors);
    ValidationResult result = new ValidationResult(errors.isEmpty(), List.copyOf(errors));
    if (!result.success()) {
      MapperForgeLoggers.VALIDATOR.warn(
          "Mapper XML validation failed: {} errors={}", before.fileName(), result.errors().size());
    }
    return result;
  }

  private void compareWhitespace(
      String before, String after, FormatterConfig config, List<ValidationError> errors) {
    if (!config.preserveWhitespace()) {
      return;
    }
    if (!find(TAG_BOUNDARY_WHITESPACE, before).equals(find(TAG_BOUNDARY_WHITESPACE, after))) {
      errors.add(
          new ValidationError(
              ErrorType.WHITESPACE, "Formatted XML changed whitespace sequence", null));
    }
  }

  private void comparePlaceholders(String before, String after, List<ValidationError> errors) {
    var beforeMatches = findMatches(PLACEHOLDER, before);
    var afterMatches = findMatches(PLACEHOLDER, after);
    var beforePlaceholders =
        beforeMatches.stream().map(TextMatch::value).map(placeholderParser::parse).toList();
    var afterPlaceholders =
        afterMatches.stream().map(TextMatch::value).map(placeholderParser::parse).toList();
    if (!beforePlaceholders.equals(afterPlaceholders)) {
      int differingIndex = firstDifferingIndex(beforePlaceholders, afterPlaceholders);
      Range location =
          differingIndex >= 0 && differingIndex < beforeMatches.size()
              ? beforeMatches.get(differingIndex).range()
              : null;
      errors.add(
          new ValidationError(
              ErrorType.PLACEHOLDER, "Formatted XML changed placeholder sequence", location));
    }
  }

  private void compareAst(
      SourceFile before, SourceFile after, FormatterConfig config, List<ValidationError> errors) {
    var beforeAst = comparable(parser.parse(before), config);
    var afterAst = comparable(parser.parse(after), config);
    if (!beforeAst.equals(afterAst)) {
      ErrorType type = classifyAstChange(before.content(), after.content(), config);
      errors.add(
          new ValidationError(
              type,
              "Formatted XML changed AST",
              astChangeLocation(type, before.content(), after.content())));
    }
  }

  private Range astChangeLocation(ErrorType type, String before, String after) {
    if (type == ErrorType.COMMENT) {
      return firstDifferingCommentRange(before, after);
    }
    if (type == ErrorType.CDATA) {
      return firstDifferingCdataRange(before, after);
    }
    if (type == ErrorType.EXPRESSION) {
      return firstDifferingOgnlAttributeRange(before, after);
    }
    return null;
  }

  private void compareSqlStatements(
      SourceFile before, SourceFile after, List<ValidationError> errors) {
    List<Object> beforeStatements = sqlStatementSignatures(parser.parse(before));
    List<Object> afterStatements = sqlStatementSignatures(parser.parse(after));
    if (!beforeStatements.equals(afterStatements)) {
      List<Range> beforeRanges = sqlStatementRanges(before.content());
      int differingIndex = firstDifferingIndex(beforeStatements, afterStatements);
      Range location =
          differingIndex >= 0 && differingIndex < beforeRanges.size()
              ? beforeRanges.get(differingIndex)
              : null;
      errors.add(
          new ValidationError(
              ErrorType.STATEMENT, "Formatted XML changed SQL statement structure", location));
    }
  }

  private ErrorType classifyAstChange(String before, String after, FormatterConfig config) {
    if (!commentSignatures(parser.parse(new SourceFile("before.xml", before)))
        .equals(commentSignatures(parser.parse(new SourceFile("after.xml", after))))) {
      return ErrorType.COMMENT;
    }
    if (config.preserveCdata()
        && !config.formatSqlInsideCdata()
        && !find(CDATA, before).equals(find(CDATA, after))) {
      return ErrorType.CDATA;
    }
    if (!ognlExpressionSignatures(parser.parse(new SourceFile("before.xml", before)))
        .equals(ognlExpressionSignatures(parser.parse(new SourceFile("after.xml", after))))) {
      return ErrorType.EXPRESSION;
    }
    return ErrorType.GENERIC_ELEMENT;
  }

  private MapperNode comparable(MapperNode node, FormatterConfig config) {
    return switch (node) {
      case TextNode ignored -> new TextNode(TextType.PLAIN_TEXT, "");
      case CommentNode comment -> comment;
      case CDataNode cdata ->
          config.preserveCdata() && !config.formatSqlInsideCdata()
              ? cdata
              : new TextNode(TextType.PLAIN_TEXT, "");
      case ElementNode element ->
          new GenericElementNode(
              element.tagName(),
              comparableAttributes(element),
              element.children().stream().map(child -> comparable(child, config)).toList());
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

  private List<Object> commentSignatures(MapperNode node) {
    List<Object> comments = new ArrayList<>();
    collectCommentSignatures(node, comments);
    return List.copyOf(comments);
  }

  private void collectCommentSignatures(MapperNode node, List<Object> comments) {
    switch (node) {
      case CommentNode comment -> comments.add(List.of(comment.type(), comment.content()));
      case ElementNode element ->
          element.children().forEach(child -> collectCommentSignatures(child, comments));
      default -> {}
    }
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
      case ElementNode element -> collectElementSqlStatementSignatures(element, statements);
      default -> {}
    }
  }

  private void collectElementSqlStatementSignatures(ElementNode element, List<Object> statements) {
    StringBuilder sqlBuffer = new StringBuilder();
    for (MapperNode child : element.children()) {
      if (child instanceof TextNode text && text.type() == TextType.SQL) {
        appendSqlChunk(sqlBuffer, text.value());
      } else if (child instanceof CommentNode comment && comment.type() == CommentType.SQL) {
        appendSqlChunk(sqlBuffer, comment.content());
      } else {
        flushSqlBuffer(sqlBuffer, statements);
        collectSqlStatementSignatures(child, statements);
      }
    }
    flushSqlBuffer(sqlBuffer, statements);
  }

  private void appendSqlChunk(StringBuilder sqlBuffer, String chunk) {
    if (!sqlBuffer.isEmpty()) {
      sqlBuffer.append('\n');
    }
    sqlBuffer.append(chunk);
  }

  private void flushSqlBuffer(StringBuilder sqlBuffer, List<Object> statements) {
    if (sqlBuffer.isEmpty()) {
      return;
    }
    statements.add(statementSignature(new SqlStatementParser(sqlBuffer.toString()).parse()));
    sqlBuffer.setLength(0);
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
    return ognlFormatter.format(raw);
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

  private List<TextMatch> findMatches(Pattern pattern, String value) {
    Matcher matcher = pattern.matcher(value);
    List<TextMatch> matches = new ArrayList<>();
    while (matcher.find()) {
      matches.add(
          new TextMatch(matcher.group(), sourceRange(value, matcher.start(), matcher.end())));
    }
    return matches;
  }

  private Range firstDifferingCommentRange(String before, String after) {
    List<TextMatch> beforeComments = commentMatches(before);
    List<TextMatch> afterComments = commentMatches(after);
    int differingIndex =
        firstDifferingIndex(
            beforeComments.stream().map(TextMatch::value).toList(),
            afterComments.stream().map(TextMatch::value).toList());
    return differingIndex >= 0 && differingIndex < beforeComments.size()
        ? beforeComments.get(differingIndex).range()
        : null;
  }

  private List<TextMatch> commentMatches(String source) {
    List<TextMatch> matches = new ArrayList<>();
    matches.addAll(findMatches(XML_COMMENT, source));
    matches.addAll(findMatches(SQL_COMMENT, source));
    matches.sort(
        (left, right) ->
            Integer.compare(left.range().start().offset(), right.range().start().offset()));
    return matches;
  }

  private Range firstDifferingCdataRange(String before, String after) {
    List<TextMatch> beforeMatches = findMatches(CDATA, before);
    List<TextMatch> afterMatches = findMatches(CDATA, after);
    int differingIndex =
        firstDifferingIndex(
            beforeMatches.stream().map(TextMatch::value).toList(),
            afterMatches.stream().map(TextMatch::value).toList());
    return differingIndex >= 0 && differingIndex < beforeMatches.size()
        ? beforeMatches.get(differingIndex).range()
        : null;
  }

  private Range firstDifferingOgnlAttributeRange(String before, String after) {
    List<TextMatch> beforeMatches = ognlAttributeMatches(before);
    List<TextMatch> afterMatches = ognlAttributeMatches(after);
    List<Object> beforeExpressions =
        beforeMatches.stream().map(TextMatch::value).map(this::ognlExpressionSignature).toList();
    List<Object> afterExpressions =
        afterMatches.stream().map(TextMatch::value).map(this::ognlExpressionSignature).toList();
    int differingIndex = firstDifferingIndex(beforeExpressions, afterExpressions);
    return differingIndex >= 0 && differingIndex < beforeMatches.size()
        ? beforeMatches.get(differingIndex).range()
        : null;
  }

  private List<TextMatch> ognlAttributeMatches(String source) {
    Matcher matcher = OGNL_ATTRIBUTE.matcher(source);
    List<TextMatch> matches = new ArrayList<>();
    while (matcher.find()) {
      String tagName = matcher.group(1).toLowerCase(Locale.ROOT);
      String attributeName = matcher.group(2);
      if (isOgnlAttribute(tagName, attributeName)) {
        matches.add(
            new TextMatch(
                unescapeXmlAttribute(matcher.group(3)),
                sourceRange(source, matcher.start(3), matcher.end(3))));
      }
    }
    return matches;
  }

  private String unescapeXmlAttribute(String value) {
    Matcher matcher = XML_ENTITY.matcher(value);
    StringBuilder result = new StringBuilder();
    while (matcher.find()) {
      matcher.appendReplacement(result, entityValue(matcher.group()));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  private String entityValue(String entity) {
    return switch (entity.toLowerCase(Locale.ROOT)) {
      case "&amp;" -> "&";
      case "&lt;" -> "<";
      case "&gt;" -> ">";
      case "&quot;" -> "\"";
      case "&apos;" -> "'";
      default -> entity;
    };
  }

  private List<Range> sqlStatementRanges(String source) {
    Matcher matcher = SQL_ELEMENT.matcher(source);
    List<Range> ranges = new ArrayList<>();
    while (matcher.find()) {
      int start = skipLeadingWhitespace(source, matcher.start(2), matcher.end(2));
      int end = trimTrailingWhitespace(source, start, matcher.end(2));
      if (start < end) {
        ranges.add(sourceRange(source, start, end));
      }
    }
    return ranges;
  }

  private int skipLeadingWhitespace(String source, int start, int end) {
    int index = start;
    while (index < end && Character.isWhitespace(source.charAt(index))) {
      index++;
    }
    return index;
  }

  private int trimTrailingWhitespace(String source, int start, int end) {
    int index = end;
    while (index > start && Character.isWhitespace(source.charAt(index - 1))) {
      index--;
    }
    return index;
  }

  private int firstDifferingIndex(List<?> before, List<?> after) {
    int size = Math.min(before.size(), after.size());
    for (int index = 0; index < size; index++) {
      if (!before.get(index).equals(after.get(index))) {
        return index;
      }
    }
    return before.size() == after.size() ? -1 : size;
  }

  private Range sourceRange(String source, int startOffset, int endOffset) {
    return new Range(positionAt(source, startOffset), positionAt(source, endOffset));
  }

  private Position positionAt(String source, int offset) {
    int line = 1;
    int column = 1;
    for (int index = 0; index < offset && index < source.length(); index++) {
      if (source.charAt(index) == '\n') {
        line++;
        column = 1;
      } else {
        column++;
      }
    }
    return new Position(offset, line, column);
  }

  private record TextMatch(String value, Range range) {}
}
