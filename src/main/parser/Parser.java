package main.parser;

import main.lexer.Lexer;
import main.lexer.Token;
import main.lexer.TokenType;

import java.util.*;

// The node of the abstract syntax tree (AST)
interface Node {
    void print(int offset);
}

class Program implements Node {
    private final List<Node> nodes;

    Program(List<Node> nodes) {
        this.nodes = nodes;
    }


    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "Program: ");
        for (Node node : nodes) {
            switch (node) {
                case Block block -> {
                    // print Blocks
                    System.out.println(curOffset + "  Block:");
                    block.print(offset + 2);
                }
                case FuncDeclaration funcDeclaration -> {
                    // print Functions
                    System.out.println(curOffset + "  Function:");
                    funcDeclaration.print(offset + 2);
                }
                case Statement statement -> {
                    // print Statements
                    System.out.println(curOffset + "  Statement:");
                    statement.print(offset + 2);
                }
                case null, default -> {
                }
            }
        }
    }
}

// Identifier Node for names of functions and names of variables
class Identifier implements Node {
    private final String name;

    Identifier(String name) {
        this.name = name;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "Identifier: " + name);
    }
}

// Block Node for code blocks enclosed in {}
class Block implements Node {
    // Block consists from statements separated by ;
    private final List<Statement> statements;

    Block(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "Block: ");
        for (Statement statement : statements) {
            if (statement != null) {
                statement.print(offset + 1);
            }
        }
    }

}

// Statement Node for statements
interface Statement extends Node {
}

// FuncDeclaration Node for declaring functions
class FuncDeclaration implements Statement {
    private final String type; // void or int
    private final Identifier identifier; // name of function
    private final List<Identifier> args; // function arguments
    private final Block funcBody; // function body

    FuncDeclaration(String type, Identifier identifier, List<Identifier> args, Block funcBody) {
        this.type = type;
        this.identifier = identifier;
        this.args = args;
        this.funcBody = funcBody;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "FuncDeclaration: ");
        System.out.println(curOffset + "  Type: " + type);
        System.out.print(curOffset + "  Name: ");
        identifier.print(0);
        if (args.isEmpty()) {
            System.out.println(curOffset + "  Parameters: ()");
        } else {
            System.out.println(curOffset + "  Parameters: ");
            for (Identifier arg : args) {
                arg.print(offset + 2);
            }
        }

        System.out.println(curOffset + "  Body: ");
        funcBody.print(offset + 2);
    }

}

// FuncCall Node for calling functions in code (statements)
class FuncCall implements Statement {
    private final Identifier identifier; // name of function
    private final List<MathExpr> args; // function arguments

    FuncCall(Identifier identifier, List<MathExpr> args) {
        this.identifier = identifier;
        this.args = args;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "FuncCall: ");
        identifier.print(offset + 1);
        System.out.println(curOffset + "  Parameters: ");
        for (MathExpr arg : args) {
            arg.print(offset + 2);
        }
    }
}

// Assignment Node for '='
class Assignment implements Statement {
    private final Identifier identifier; // the left side of assignment
    private final Statement expr; // the right side of assignment

    Assignment(Identifier identifier, Statement expr) {
        this.identifier = identifier;
        this.expr = expr;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "Assignment: ");
        identifier.print(offset + 1);
        expr.print(offset + 1);
    }
}

// MathExpr Node for expressions
interface MathExpr extends Statement {
}

// BinaryOp Node for arithmetic operations and comparison operations
class BinaryOp implements MathExpr {
    private final MathExpr left;
    private final String operator; // arithmetic operations or comparison operations
    private final MathExpr right;

    BinaryOp(MathExpr left, String operator, MathExpr right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "BinaryOp: " + operator);
        left.print(offset + 1);
        right.print(offset + 1);
    }
}

// Literal Node for String and Int literals
class Literal implements MathExpr {
    private final String value;

    Literal(String value) {
        this.value = value;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "Literal: " + value);
    }
}

// IfStatement Node for the conditional if statement
class IfStatement implements Statement {
    private final MathExpr condition; // the condition of the if statement
    private final Block thenBlock;
    private final Block elseBlock;    // can be null

    IfStatement(MathExpr condition, Block thenBlock, Block elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }


    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "IfStatement: ");
        condition.print(offset + 1);
        System.out.println(curOffset + "  Then: ");
        thenBlock.print(offset + 2);
        if (elseBlock != null) {
            System.out.println(curOffset + "  Else: ");
            elseBlock.print(offset + 2);
        }
    }
}

// ReturnStatement Node for the return value of the function
class ReturnStatement implements Statement {
    private final MathExpr expression;

    ReturnStatement(MathExpr expression) {
        this.expression = expression;
    }

    @Override
    public void print(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        System.out.println(curOffset + "ReturnStatement: ");
        expression.print(offset + 1);
    }
}

public class Parser {
    private Token curToken;
    private Token nextToken;
    private final Iterator<Token> tokens;
    private Program program;

    public Parser(Lexer lexer) {
        this.tokens = lexer.iterator();
        this.curToken = tokens.next();
        this.nextToken = tokens.next();
    }

    public void parse() {
        parseProgram();
    }

    public void print() {
        if (program == null) {
            throw new ParserException("Program wasn't parsed");
        } else {
            program.print(0);
        }
    }


    private void acceptToken(String target) {
        if (curToken == null || !target.equals(curToken.getRepr())) {
            throw new ParserException("Incorrect syntax]: expected " + target + ", found: " +
                    (curToken != null ? curToken.getRepr() : "null"));
        }
        nextSym();
    }

    private void parseProgram() {
        List<Node> nodes = new ArrayList<>(); // common list for all program elements
        while (curToken != null && curToken.getType() != null) {
            if (curToken.getRepr().equals("void") || curToken.getRepr().equals("int")) { // the returned value
                FuncDeclaration function = (FuncDeclaration) parseFunctionDeclaration();
                nodes.add(function);
                //functions.add(function);
            } else if (curToken.getRepr().equals("if")) {
                Statement ifStatement = parseIfStatement();
                nodes.add(ifStatement);
                //statements.add(ifStatement);
            } else if (curToken.getRepr().equals("{")) {
                Block block = parseBlock();
                nodes.add(block);
                // blocks.add(block);
            } else {
                Statement statement = parseSentences();
                // if statement null -- the next token is '{'
                if (statement != null) {
                    // statements.add(statement);
                    nodes.add(statement);
                }
            }
        }
        if (curToken != null && curToken.getType() != null) {
            throw new ParserException("Unexpected token: " + curToken.getType() + ". Expected EOF.");
        }
        this.program = new Program(nodes);
    }

    private Block parseBlock() {
        acceptToken("{"); // block should start with '{'
        List<Statement> statements = new ArrayList<>();
        while (curToken != null && !"}".equals(curToken.getRepr()) && curToken.getType() != null) {
            if (curToken.getRepr().equals(";")) {
                nextSym(); // move to the next statement
                continue;
            }
            Statement statement = parseSentences();
            statements.add(statement);
        }
        acceptToken("}"); // block should end with '}'
        return new Block(statements);
    }

    private Statement parseSentences() {
        if (curToken != null && "{".equals(curToken.getRepr())) {
            return null; // after statement, there may be a block starting with {
        }
        Statement statementStartWithKeyword = parseStatementStartWithKeyword(); // function declaration, return and if statements
        if (statementStartWithKeyword != null) return statementStartWithKeyword;
        if (curToken != null && (curToken.getType() == TokenType.IDENTIFIER)) {
            return parseStatementStartWithIdentifier(); //
        } else if (curToken != null && (";".equals(curToken.getRepr()))) {
            nextSym(); // skip ';'
            return parseSentences(); // function declaration
        } else {
            throw new ParserException("Expected identifier for sentence but got " +
                    (curToken != null ? curToken.getType() : "null"));
        }
    }


    private Statement parseStatementStartWithKeyword() {
        if (curToken != null && curToken.getType() == TokenType.KEYWORD) {
            if (curToken.getRepr().equals("return")) {
                return parseReturnStatement();
            } else if (curToken.getRepr().equals("if")) {
                return parseIfStatement();
            } else {
                return parseFunctionDeclaration();
            }
        }
        return null;
    }

    private Statement parseStatementStartWithIdentifier() {
        Identifier identifierNode = new Identifier(curToken.getRepr());
        if (nextToken != null && nextToken.getType() == TokenType.ASSIGN) {
            nextSym(); // skip '='
            return parseAssignment(identifierNode); // assignment
        } else if (nextToken != null && (nextToken.getType() == TokenType.OPERATION)) {
            nextSym();
            return parseExpr(); // math expression
        } else if (nextToken != null && ("(".equals(nextToken.getRepr()))) {
            return parseFuncCall(identifierNode); // function call
        } else {
            throw new ParserException("Unexpected token " + (nextToken != null ? nextToken.getRepr() : "null") + " after identifier.");
        }
    }

    private MathExpr parseExpr() {
        return parseEqualityExpr();
    }

    private MathExpr parseEqualityExpr() {
        return parseBinaryOperation(Arrays.asList("==", "!=", ">=", "<="), this::parseComparisonOrAssignExpr);
    }

    private MathExpr parseMulDivModExpr() {
        return parseBinaryOperation(Arrays.asList("*", "/", "%"), this::parsePrimaryExpr);
    }

    private MathExpr parseAddSubExpr() {
        return parseBinaryOperation(Arrays.asList("+", "-"), this::parseMulDivModExpr);
    }

    private MathExpr parseComparisonOrAssignExpr() {
        return parseBinaryOperation(Arrays.asList("<", ">", "="), this::parseAddSubExpr);
    }

    private MathExpr parseBinaryOperation(List<String> operators, java.util.function.Supplier<MathExpr> parseOperand) {
        MathExpr left = parseOperand.get();
        while (curToken != null && operators.contains(curToken.getRepr())) {
            String operator = curToken.getRepr();
            nextSym();
            MathExpr right = parseOperand.get();
            left = new BinaryOp(left, operator, right);
        }
        return left;
    }

    private MathExpr parsePrimaryExpr() {
        if (curToken != null && "(".equals(curToken.getRepr())) {
            nextSym();
            MathExpr expr = parseExpr();
            acceptToken(")");
            return expr; // expression in parentheses
        } else if (curToken != null) {
            String value = curToken.getRepr();
            nextSym();
            return new Literal(value); // literal
        } else {
            throw new ParserException("Unexpected token in Primary Expr");
        }
    }

    private Assignment parseAssignment(Identifier identifier) {
        if (curToken == null || curToken.getType() != TokenType.ASSIGN) {
            throw new ParserException("Expected assignment.");
        }
        nextSym(); // skip '='
        if (nextToken != null && nextToken.getRepr().equals("(")) {
            FuncCall funcCall = parseFuncCall(identifier);
            return new Assignment(identifier, funcCall); // identifier = funcCall
        } else {
            MathExpr expr = parseExpr();
            return new Assignment(identifier, expr); // identifier = expr
        }
    }

    private IfStatement parseIfStatement() {
        acceptToken("if");
        acceptToken("("); // the condition is enclosed in parentheses
        MathExpr condition = parseExpr(); // parse condition
        acceptToken(")");
        Block thenBlock = parseBlock();

        Block elseBlock = null;
        if (curToken != null && "else".equals(curToken.getRepr())) {
            nextSym(); // skip else token
            elseBlock = parseBlock();
        }
        return new IfStatement(condition, thenBlock, elseBlock);
    }

    private Statement parseFunctionDeclaration() {
        String type = curToken.getRepr();
        nextSym(); // skip return type
        Identifier identifier = new Identifier(curToken.getRepr()); // function name
        nextSym(); // skip function name
        acceptToken("(");
        List<Identifier> args = new ArrayList<>();
        // parse arguments
        while (curToken != null && !")".equals(curToken.getRepr())) {
            args.add(new Identifier(curToken.getRepr()));
            nextSym(); // skip arg
            if (curToken != null && ",".equals(curToken.getRepr())) {
                nextSym(); // skip ','
            }
        }
        acceptToken(")");
        Block body = parseBlock();
        return new FuncDeclaration(type, identifier, args, body);
    }

    private Statement parseReturnStatement() {
        acceptToken("return");
        MathExpr expression = parseExpr();
        acceptToken(";");
        return new ReturnStatement(expression);
    }

    private FuncCall parseFuncCall(Identifier funcName) {
        nextSym(); // skip name of function
        List<MathExpr> args = new ArrayList<>();
        acceptToken("(");

        while (curToken != null && !")".equals(curToken.getRepr())) {
            if (",".equals(curToken.getRepr())) {
                nextSym(); // skip ','
            } else {
                MathExpr expr = parseExpr();
                args.add(expr);
            }
        }
        acceptToken(")");
        return new FuncCall(funcName, args);
    }

    private void nextSym() {
        curToken = nextToken;
        if (tokens.hasNext()) {
            nextToken = tokens.next();
        } else {
            nextToken = null;
        }
    }

    static String getCurOffset(int offset) {
        return "  ".repeat(offset);
    }

    public static class ParserException extends RuntimeException {
        public ParserException(String message) {
            super(message);
        }
    }


    public static void main(String[] args) {
        String sourceCode = """
                int add(a, b) {
                    return a + b;
                }
                
                c = 6;
                add(5, 6);
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
                    z = add(x, y);
                    if (z > 25) {
                        print("z is greater than 25");
                    } else {
                        print("z is less than or equal to 25");
                    }
                }
                """;
        try {
            Lexer lexer = new Lexer(sourceCode);
            Parser parser = new Parser(lexer);
            parser.parse();
            parser.print();
        } catch (Parser.ParserException e) {
            System.err.println("Parser error: " + e.getMessage());
        }
    }
}