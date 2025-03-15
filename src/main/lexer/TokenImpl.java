package main.lexer;

public class TokenImpl implements Token {
    private final TokenType type;
    private final String repr;
    private final Lexer.Pos pos;

    public TokenImpl(TokenType type, String repr, Lexer.Pos pos) {
        this.type = type;
        this.repr = repr;
        this.pos = pos;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    @Override
    public String getRepr() {
        return repr;
    }
}