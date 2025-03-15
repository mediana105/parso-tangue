package lexer;

public record TokenImpl(TokenType type, String repr, Lexer.Pos pos) implements Token {
}