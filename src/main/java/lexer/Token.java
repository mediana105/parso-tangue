package lexer;

public interface Token {
    TokenType type();

    String repr();

    Lexer.Pos pos();
}