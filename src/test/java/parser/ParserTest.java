package parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    @Test
    @DisplayName("Empty Input")
    public void testEmptyInput() throws FileNotFoundException {
        String filePath = getPath() + "empty.pt";
        Parser parser = new Parser(filePath);
        parser.parse();
        assertEquals("", parser.toString());
    }

    @Test
    public void testParseProgram() throws FileNotFoundException {
        Program actualTree = getActualTree();
        Program expectedTree = new Program(List.of(new FuncDeclaration("int", new Identifier("add"), List.of(new Identifier("a"), new Identifier("b")), new Block(List.of(new ReturnStatement(new BinaryOp(new Identifier("a"), "+", new Identifier("b")))))), new Assignment(new Identifier("c"), new IntLiteral(6)), new FuncCall(new Identifier("add"), List.of(new IntLiteral(5), new IntLiteral(6))), new Block(List.of(new Assignment(new Identifier("x"), new BinaryOp(new Identifier("x"), "+", new IntLiteral(1))), new FuncDeclaration("void", new Identifier("reduce"), List.of(new Identifier("c"), new Identifier("x")), new Block(List.of(new Assignment(new Identifier("c"), new BinaryOp(new Identifier("c"), "-", new Identifier("x")))))), new FuncCall(new Identifier("reduce"), List.of(new IntLiteral(1), new IntLiteral(2))))), new FuncDeclaration("void", new Identifier("main"), List.of(), new Block(List.of(new Assignment(new Identifier("x"), new IntLiteral(10)), new Assignment(new Identifier("y"), new IntLiteral(20)), new Assignment(new Identifier("z"), new BinaryOp(new Identifier("x"), "+", new Identifier("y"))), new IfStatement(new BinaryOp(new Identifier("z"), ">", new IntLiteral(25)), new Block(List.of(new FuncCall(new Identifier("print"), List.of(new StringLiteral("\"z is greater than 25\""))))), new Block(List.of(new FuncCall(new Identifier("print"), List.of(new StringLiteral("\"z is less than or equal to 25\"")))))))))));
        assertEquals(expectedTree.toString(0), actualTree.toString(0));
    }

    private Program getActualTree() throws FileNotFoundException {
        String filePath = getPath() + "complexProgram.pt";
        Parser parser = new Parser(filePath);
        return parser.parse();
    }

    @Test
    @DisplayName("Complex math expression")
    public void testComplexMathExpressions() throws FileNotFoundException {
        String filePath = getPath() + "complexMathExpr.pt";
        Parser parser = new Parser(filePath);
        Program program = parser.parse();

        Program expectedTree = new Program(List.of(new Assignment(new Identifier("x"), new BinaryOp(new BinaryOp(new Identifier("a"), "+", new Identifier("b")), "*", new BinaryOp(new Identifier("c"), "-", new Identifier("d"))))));
        assertEquals(expectedTree.toString(0), program.toString(0));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSyntaxTestCases")
    @DisplayName("Parameterized test for invalid syntax")
    public void testInvalidSyntaxParameterized(String fileName, String expectedErrorMessage) throws FileNotFoundException {
        String filePath = getPath() + fileName;
        Parser parser = new Parser(filePath);
        Parser.ParserException exception = assertThrows(Parser.ParserException.class, parser::parse);

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidSyntaxTestCases() {
        return Stream.of(
                Arguments.of("incorrectSyntax1.pt", "Incorrect syntax: expected ;, found: }"),
                Arguments.of("incorrectSyntax2.pt", "Unexpected token concat after identifier.")
        );
    }

    private String getPath() {
        return "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    }
}