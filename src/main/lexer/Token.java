package main.lexer;

public interface Token {
    TokenType getType();

    String getRepr();

    Lexer.Pos getPos();
}