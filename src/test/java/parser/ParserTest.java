package parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    @Test
    @DisplayName("Empty Input")
    public void testEmptyInput() {
        Parser parser = new Parser("");
        parser.parse();
        assertEquals("", parser.toString());
    }

    @Test
    public void testParseProgram() {
        Program actualTree = getActualTree();

        Program expectedTree = new Program(List.of(new FuncDeclaration("int", new Identifier("add"), List.of(new Identifier("a"), new Identifier("b")), new Block(List.of(new ReturnStatement(new BinaryOp(new Identifier("a"), "+", new Identifier("b")))))), new Assignment(new Identifier("c"), new IntLiteral(6)), new FuncCall(new Identifier("add"), List.of(new IntLiteral(5), new IntLiteral(6))), new Block(List.of(new Assignment(new Identifier("x"), new BinaryOp(new Identifier("x"), "+", new IntLiteral(1))), new FuncDeclaration("void", new Identifier("reduce"), List.of(new Identifier("c"), new Identifier("x")), new Block(List.of(new Assignment(new Identifier("c"), new BinaryOp(new Identifier("c"), "-", new Identifier("x")))))), new FuncCall(new Identifier("reduce"), List.of(new IntLiteral(1), new IntLiteral(2))))), new FuncDeclaration("void", new Identifier("main"), List.of(), new Block(List.of(new Assignment(new Identifier("x"), new IntLiteral(10)), new Assignment(new Identifier("y"), new IntLiteral(20)), new Assignment(new Identifier("z"), new BinaryOp(new Identifier("x"), "+", new Identifier("y"))), new IfStatement(new BinaryOp(new Identifier("z"), ">", new IntLiteral(25)), new Block(List.of(new FuncCall(new Identifier("print"), List.of(new StringLiteral("\"z is greater than 25\""))))), new Block(List.of(new FuncCall(new Identifier("print"), List.of(new StringLiteral("\"z is less than or equal to 25\"")))))))))));

        assertEquals(expectedTree.toString(0), actualTree.toString(0));
    }

    private Program getActualTree() {
        String input = """
                int add(a, b) {
                    return a + b;
                }
                
                c = 6;
                add(5, 6);
                {
                    x = x + 1;
                    void reduce(c, x) {
                        c = c - x;
                    }
                    reduce(1, 2);
                }
                
                void main() {
                    x = 10;
                    y = 20;
                    z = x + y;
                    if (z > 25) {
                        print("z is greater than 25");
                    } else {
                        print("z is less than or equal to 25");
                    }
                }
                """;

        Parser parser = new Parser(input);
        return parser.parse();
    }

    @Test
    @DisplayName("Complex math expression")
    public void testComplexMathExpressions() {
        String input = """
                x = (a + b) * (c - d);
                """;
        Parser parser = new Parser(input);
        Program program = parser.parse();

        Program expectedTree = new Program(List.of(new Assignment(new Identifier("x"), new BinaryOp(new BinaryOp(new Identifier("a"), "+", new Identifier("b")), "*", new BinaryOp(new Identifier("c"), "-", new Identifier("d"))))));
        assertEquals(expectedTree.toString(0), program.toString(0));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSyntaxTestCases")
    @DisplayName("Parameterized test for invalid syntax")
    public void testInvalidSyntaxParameterized(String input, String expectedErrorMessage) {
        Parser parser = new Parser(input);
        Parser.ParserException exception = assertThrows(Parser.ParserException.class, parser::parse);

        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidSyntaxTestCases() {
        return Stream.of(Arguments.of("String concat(x, y) { return x + y; }", "Unexpected token concat after identifier."), Arguments.of("int add(a, b) { return a + b", "Incorrect syntax: expected ;, found: null"));
    }
}