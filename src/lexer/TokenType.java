package lexer;

public enum TokenType {
    OPERATION, // +, -, *, /, %

    COMPARISON, // <=, >=, <, >, ==, !=

    ASSIGN, // =

    SPECIAL, // {, }, (, ), ,, ;

    IDENTIFIER, // names of functions and names of variables

    KEYWORD, // int, void, if, else, return

    // Literals
    STRING,
    INT
}