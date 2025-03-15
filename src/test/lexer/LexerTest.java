package test.lexer;

import main.lexer.Token;
import main.lexer.TokenImpl;
import main.lexer.TokenType;
import org.junit.Test;
import main.lexer.Lexer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class LexerTest {
    @Test
    @DisplayName("Empty Input")
    public void testEmptyInput() {
        Iterator<Token> iterator = new Lexer("").iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("Input only with whitespaces")
    public void testWhitespacesSource() {
        Iterator<Token> iterator = new Lexer("    \t\t\t\n\n \r").iterator();
        assertFalse(iterator.hasNext());
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    @DisplayName("Division input code into tokens")
    void testLexer(String input, List<Token> expectedTokens) {
        Lexer lexer = new Lexer(input);
        Iterator<Token> iterator = lexer.iterator();

        for (Token expectedToken : expectedTokens) {
            assertTrue(iterator.hasNext());
            Token actualToken = iterator.next();
            assertEquals(expectedToken.getType(), actualToken.getType());
            assertEquals(expectedToken.getRepr(), actualToken.getRepr());
            assertEquals(expectedToken.getPos(), actualToken.getPos());
        }
        assertFalse(iterator.hasNext());
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        "print(x, 5);",
                        Arrays.asList(
                                new TokenImpl(TokenType.IDENTIFIER, "print", new Lexer.Pos(1, 1)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(1, 6)),
                                new TokenImpl(TokenType.IDENTIFIER, "x", new Lexer.Pos(1, 7)),
                                new TokenImpl(TokenType.SPECIAL, ",", new Lexer.Pos(1, 8)),
                                new TokenImpl(TokenType.INT, "5", new Lexer.Pos(1, 10)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(1, 11)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(1, 12))
                        )
                ),
                Arguments.of(
                        "if (x == 3) {\n    z = \"string\";\n}",
                        Arrays.asList(
                                new TokenImpl(TokenType.KEYWORD, "if", new Lexer.Pos(1, 1)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(1, 4)),
                                new TokenImpl(TokenType.IDENTIFIER, "x", new Lexer.Pos(1, 5)),
                                new TokenImpl(TokenType.COMPARISON, "==", new Lexer.Pos(1, 7)),
                                new TokenImpl(TokenType.INT, "3", new Lexer.Pos(1, 10)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(1, 11)),
                                new TokenImpl(TokenType.SPECIAL, "{", new Lexer.Pos(1, 13)),
                                new TokenImpl(TokenType.IDENTIFIER, "z", new Lexer.Pos(2, 5)),
                                new TokenImpl(TokenType.ASSIGN, "=", new Lexer.Pos(2, 7)),
                                new TokenImpl(TokenType.STRING, "\"string\"", new Lexer.Pos(2, 9)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(2, 17)),
                                new TokenImpl(TokenType.SPECIAL, "}", new Lexer.Pos(3, 1))
                        )
                ),
                Arguments.of(
                        "void reduce(c, x) {\n    c = c - x;\n}",
                        Arrays.asList(
                                new TokenImpl(TokenType.KEYWORD, "void", new Lexer.Pos(1, 1)),
                                new TokenImpl(TokenType.IDENTIFIER, "reduce", new Lexer.Pos(1, 6)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(1, 12)),
                                new TokenImpl(TokenType.IDENTIFIER, "c", new Lexer.Pos(1, 13)),
                                new TokenImpl(TokenType.SPECIAL, ",", new Lexer.Pos(1, 14)),
                                new TokenImpl(TokenType.IDENTIFIER, "x", new Lexer.Pos(1, 16)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(1, 17)),
                                new TokenImpl(TokenType.SPECIAL, "{", new Lexer.Pos(1, 19)),
                                new TokenImpl(TokenType.IDENTIFIER, "c", new Lexer.Pos(2, 5)),
                                new TokenImpl(TokenType.ASSIGN, "=", new Lexer.Pos(2, 7)),
                                new TokenImpl(TokenType.IDENTIFIER, "c", new Lexer.Pos(2, 9)),
                                new TokenImpl(TokenType.OPERATION, "-", new Lexer.Pos(2, 11)),
                                new TokenImpl(TokenType.IDENTIFIER, "x", new Lexer.Pos(2, 13)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(2, 14)),
                                new TokenImpl(TokenType.SPECIAL, "}", new Lexer.Pos(3, 1))
                        )
                ),
                Arguments.of(
                        "int add(a, b) {\n    return a + b;\n}",
                        Arrays.asList(
                                new TokenImpl(TokenType.KEYWORD, "int", new Lexer.Pos(1, 1)),
                                new TokenImpl(TokenType.IDENTIFIER, "add", new Lexer.Pos(1, 5)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(1, 8)),
                                new TokenImpl(TokenType.IDENTIFIER, "a", new Lexer.Pos(1, 9)),
                                new TokenImpl(TokenType.SPECIAL, ",", new Lexer.Pos(1, 10)),
                                new TokenImpl(TokenType.IDENTIFIER, "b", new Lexer.Pos(1, 12)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(1, 13)),
                                new TokenImpl(TokenType.SPECIAL, "{", new Lexer.Pos(1, 15)),
                                new TokenImpl(TokenType.KEYWORD, "return", new Lexer.Pos(2, 5)),
                                new TokenImpl(TokenType.IDENTIFIER, "a", new Lexer.Pos(2, 12)),
                                new TokenImpl(TokenType.OPERATION, "+", new Lexer.Pos(2, 14)),
                                new TokenImpl(TokenType.IDENTIFIER, "b", new Lexer.Pos(2, 16)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(2, 17)),
                                new TokenImpl(TokenType.SPECIAL, "}", new Lexer.Pos(3, 1))
                        )
                ),
                Arguments.of(
                        "if (long_var >= 4) {\n    return a % b;\n} else {\nreturn add(1, long_var);}",
                        Arrays.asList(
                                new TokenImpl(TokenType.KEYWORD, "if", new Lexer.Pos(1, 1)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(1, 4)),
                                new TokenImpl(TokenType.IDENTIFIER, "long_var", new Lexer.Pos(1, 5)),
                                new TokenImpl(TokenType.COMPARISON, ">=", new Lexer.Pos(1, 14)),
                                new TokenImpl(TokenType.INT, "4", new Lexer.Pos(1, 17)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(1, 18)),
                                new TokenImpl(TokenType.SPECIAL, "{", new Lexer.Pos(1, 20)),
                                new TokenImpl(TokenType.KEYWORD, "return", new Lexer.Pos(2, 5)),
                                new TokenImpl(TokenType.IDENTIFIER, "a", new Lexer.Pos(2, 12)),
                                new TokenImpl(TokenType.OPERATION, "%", new Lexer.Pos(2, 14)),
                                new TokenImpl(TokenType.IDENTIFIER, "b", new Lexer.Pos(2, 16)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(2, 17)),
                                new TokenImpl(TokenType.SPECIAL, "}", new Lexer.Pos(3, 1)),
                                new TokenImpl(TokenType.KEYWORD, "else", new Lexer.Pos(3, 3)),
                                new TokenImpl(TokenType.SPECIAL, "{", new Lexer.Pos(3, 8)),
                                new TokenImpl(TokenType.KEYWORD, "return", new Lexer.Pos(4, 1)),
                                new TokenImpl(TokenType.IDENTIFIER, "add", new Lexer.Pos(4, 8)),
                                new TokenImpl(TokenType.SPECIAL, "(", new Lexer.Pos(4, 11)),
                                new TokenImpl(TokenType.INT, "1", new Lexer.Pos(4, 12)),
                                new TokenImpl(TokenType.SPECIAL, ",", new Lexer.Pos(4, 13)),
                                new TokenImpl(TokenType.IDENTIFIER, "long_var", new Lexer.Pos(4, 15)),
                                new TokenImpl(TokenType.SPECIAL, ")", new Lexer.Pos(4, 23)),
                                new TokenImpl(TokenType.SPECIAL, ";", new Lexer.Pos(4, 24)),
                                new TokenImpl(TokenType.SPECIAL, "}", new Lexer.Pos(4, 25))
                        )
                )
        );
    }
}
