package io.github.isksss.mapperforge.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.isksss.mapperforge.ast.mapper.ArgElementNode;
import io.github.isksss.mapperforge.ast.mapper.AssociationElementNode;
import io.github.isksss.mapperforge.ast.mapper.AttributeNode;
import io.github.isksss.mapperforge.ast.mapper.BindElementNode;
import io.github.isksss.mapperforge.ast.mapper.CaseElementNode;
import io.github.isksss.mapperforge.ast.mapper.ChooseElementNode;
import io.github.isksss.mapperforge.ast.mapper.CollectionElementNode;
import io.github.isksss.mapperforge.ast.mapper.ConstructorElementNode;
import io.github.isksss.mapperforge.ast.mapper.DeleteElementNode;
import io.github.isksss.mapperforge.ast.mapper.DiscriminatorElementNode;
import io.github.isksss.mapperforge.ast.mapper.ElementNode;
import io.github.isksss.mapperforge.ast.mapper.ForeachElementNode;
import io.github.isksss.mapperforge.ast.mapper.GenericElementNode;
import io.github.isksss.mapperforge.ast.mapper.IdArgElementNode;
import io.github.isksss.mapperforge.ast.mapper.IfElementNode;
import io.github.isksss.mapperforge.ast.mapper.IncludeElementNode;
import io.github.isksss.mapperforge.ast.mapper.InsertElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperElementNode;
import io.github.isksss.mapperforge.ast.mapper.MapperNode;
import io.github.isksss.mapperforge.ast.mapper.OtherwiseElementNode;
import io.github.isksss.mapperforge.ast.mapper.ResultMapElementNode;
import io.github.isksss.mapperforge.ast.mapper.SelectElementNode;
import io.github.isksss.mapperforge.ast.mapper.SetElementNode;
import io.github.isksss.mapperforge.ast.mapper.SqlElementNode;
import io.github.isksss.mapperforge.ast.mapper.TextNode;
import io.github.isksss.mapperforge.ast.mapper.TextType;
import io.github.isksss.mapperforge.ast.mapper.TrimElementNode;
import io.github.isksss.mapperforge.ast.mapper.UpdateElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhenElementNode;
import io.github.isksss.mapperforge.ast.mapper.WhereElementNode;
import io.github.isksss.mapperforge.ast.ognl.OgnlCallExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlCollectionExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlExpression;
import io.github.isksss.mapperforge.ast.ognl.OgnlLiteralExpression;
import io.github.isksss.mapperforge.ast.sql.ArrayExpression;
import io.github.isksss.mapperforge.ast.sql.CaseExpression;
import io.github.isksss.mapperforge.ast.sql.ColumnExpression;
import io.github.isksss.mapperforge.ast.sql.Expression;
import io.github.isksss.mapperforge.ast.sql.FunctionExpression;
import io.github.isksss.mapperforge.ast.sql.InExpression;
import io.github.isksss.mapperforge.ast.sql.LiteralExpression;
import io.github.isksss.mapperforge.ast.sql.RowExpression;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

final class AstImmutabilityTest {
  private static final Path AST_SOURCE_ROOT =
      Path.of("src/main/java/io/github/isksss/mapperforge/ast");
  private static final Pattern TOP_LEVEL_IMMUTABLE_DECLARATION =
      Pattern.compile("public\\s+(record|sealed\\s+interface|enum)\\s+\\w+");
  private static final Pattern SETTER_METHOD =
      Pattern.compile(
          "\\b(?:public|protected|private)?\\s*(?:final\\s+)?void\\s+set[A-Z]\\w*\\s*\\(");

  @Test
  void astSourcesUseImmutableTopLevelDeclarations() throws IOException {
    try (var paths = Files.walk(AST_SOURCE_ROOT)) {
      for (Path source : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
        String content = Files.readString(source);
        assertTrue(
            TOP_LEVEL_IMMUTABLE_DECLARATION.matcher(content).find(),
            source + " must use a record, sealed interface, or enum top-level declaration");
      }
    }
  }

  @Test
  void astSourcesDoNotDeclareSetters() throws IOException {
    try (var paths = Files.walk(AST_SOURCE_ROOT)) {
      for (Path source : paths.filter(path -> path.toString().endsWith(".java")).toList()) {
        String content = Files.readString(source);
        assertFalse(SETTER_METHOD.matcher(content).find(), source + " must not declare setters");
      }
    }
  }

  @ParameterizedTest
  @MethodSource("elementNodeTypes")
  void elementNodeListsAreDefensivelyCopied(Class<? extends ElementNode> nodeType)
      throws ReflectiveOperationException {
    List<AttributeNode> attributes = new ArrayList<>();
    attributes.add(new AttributeNode("id", "find"));
    List<MapperNode> children = new ArrayList<>();
    children.add(new TextNode(TextType.SQL, "select 1"));

    ElementNode node = newElementNode(nodeType, attributes, children);
    attributes.add(new AttributeNode("parameterType", "long"));
    children.add(new TextNode(TextType.SQL, "where id = #{id}"));

    assertTrue(node.attributes().size() == 1, nodeType.getSimpleName() + " copies attributes");
    assertTrue(node.children().size() == 1, nodeType.getSimpleName() + " copies children");
    assertThrows(
        UnsupportedOperationException.class,
        () -> node.attributes().add(new AttributeNode("resultType", "long")));
    assertThrows(
        UnsupportedOperationException.class,
        () -> node.children().add(new TextNode(TextType.SQL, "order by id")));
  }

  @Test
  void sqlExpressionListsAreDefensivelyCopied() {
    List<Expression> values = new ArrayList<>();
    values.add(new LiteralExpression("1"));

    ArrayExpression array = new ArrayExpression(values);
    RowExpression row = new RowExpression(values);
    FunctionExpression function = new FunctionExpression("coalesce", values);
    InExpression in = new InExpression(new ColumnExpression("status"), values);
    values.add(new LiteralExpression("2"));

    assertListIsCopiedAndUnmodifiable(array.values(), new LiteralExpression("3"));
    assertListIsCopiedAndUnmodifiable(row.values(), new LiteralExpression("3"));
    assertListIsCopiedAndUnmodifiable(function.arguments(), new LiteralExpression("3"));
    assertListIsCopiedAndUnmodifiable(in.values(), new LiteralExpression("3"));

    List<CaseExpression.WhenClause> clauses = new ArrayList<>();
    clauses.add(
        new CaseExpression.WhenClause(new ColumnExpression("active"), new LiteralExpression("1")));
    CaseExpression caseExpression = new CaseExpression(clauses, new LiteralExpression("0"));
    clauses.add(
        new CaseExpression.WhenClause(new ColumnExpression("locked"), new LiteralExpression("2")));

    assertListIsCopiedAndUnmodifiable(
        caseExpression.whenClauses(),
        new CaseExpression.WhenClause(new ColumnExpression("deleted"), new LiteralExpression("3")));
  }

  @Test
  void ognlExpressionListsAreDefensivelyCopied() {
    List<OgnlExpression> values = new ArrayList<>();
    values.add(new OgnlLiteralExpression("ACTIVE", "'ACTIVE'"));

    OgnlCallExpression call = new OgnlCallExpression("allowed", values);
    OgnlCollectionExpression collection = new OgnlCollectionExpression(values);
    values.add(new OgnlLiteralExpression("LOCKED", "'LOCKED'"));

    assertListIsCopiedAndUnmodifiable(
        call.arguments(), new OgnlLiteralExpression("DELETED", "'DELETED'"));
    assertListIsCopiedAndUnmodifiable(
        collection.values(), new OgnlLiteralExpression("DELETED", "'DELETED'"));
  }

  private static <T> void assertListIsCopiedAndUnmodifiable(List<T> actual, T newValue) {
    assertTrue(actual.size() == 1);
    assertThrows(UnsupportedOperationException.class, () -> actual.add(newValue));
  }

  private static ElementNode newElementNode(
      Class<? extends ElementNode> nodeType,
      List<AttributeNode> attributes,
      List<MapperNode> children)
      throws ReflectiveOperationException {
    try {
      if (nodeType == GenericElementNode.class) {
        return nodeType
            .getConstructor(String.class, List.class, List.class)
            .newInstance("custom", attributes, children);
      }
      return nodeType.getConstructor(List.class, List.class).newInstance(attributes, children);
    } catch (InvocationTargetException exception) {
      throw new AssertionError(nodeType.getSimpleName() + " constructor failed", exception);
    }
  }

  private static Stream<Class<? extends ElementNode>> elementNodeTypes() {
    return Stream.of(
        MapperElementNode.class,
        SelectElementNode.class,
        InsertElementNode.class,
        UpdateElementNode.class,
        DeleteElementNode.class,
        SqlElementNode.class,
        ResultMapElementNode.class,
        AssociationElementNode.class,
        CollectionElementNode.class,
        ConstructorElementNode.class,
        ArgElementNode.class,
        IdArgElementNode.class,
        DiscriminatorElementNode.class,
        CaseElementNode.class,
        IfElementNode.class,
        ChooseElementNode.class,
        WhenElementNode.class,
        OtherwiseElementNode.class,
        ForeachElementNode.class,
        TrimElementNode.class,
        WhereElementNode.class,
        SetElementNode.class,
        IncludeElementNode.class,
        BindElementNode.class,
        GenericElementNode.class);
  }
}
