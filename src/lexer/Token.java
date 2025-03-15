package lexer;

public interface Token {
    TokenType getType();

    String getRepr();

}