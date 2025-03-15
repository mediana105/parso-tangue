package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;

import java.util.*;

// The node of the abstract syntax tree (AST)
interface Node {
    String toString(int offset);
}

class Program implements Node {
    private final List<Node> nodes;

    Program(List<Node> nodes) {
        this.nodes = nodes;
    }


    @Override
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        if (nodes.isEmpty()) {
            return "";
        }
        sb.append(curOffset).append("Program:\n");
        for (Node node : nodes) {
            switch (node) {
                case Block block -> {
                    // print Blocks
                    sb.append(curOffset).append("  Block:\n");
                    sb.append(block.toString(offset + 2));
                }
                case FuncDeclaration funcDeclaration -> {
                    // print Functions
                    sb.append(curOffset).append("  Function:\n");
                    sb.append(funcDeclaration.toString(offset + 2));
                }
                case Statement statement -> {
                    // print Statements
                    sb.append(curOffset).append("  Statement:\n");
                    sb.append(statement.toString(offset + 2));
                }
                case null, default -> {
                }
            }
        }
        return sb.toString();
    }
}

// Identifier Node for names of functions and names of variables
class Identifier implements MathExpr {
    private final String name;

    Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("Identifier: ").append(name).append('\n');
        return sb.toString();
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
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("Block: \n");
        for (Statement statement : statements) {
            if (statement != null) {
                sb.append(statement.toString(offset + 1));
            }
        }
        return sb.toString();
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
    public String toString(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        StringBuilder sb = new StringBuilder();
        sb.append(curOffset).append("FuncDeclaration: \n");
        sb.append(curOffset).append("  Type: ").append(type).append('\n');
        sb.append(curOffset).append("  Name: \n");
        sb.append(identifier.toString(0));
        if (args.isEmpty()) {
            sb.append(curOffset).append("  Parameters: ()\n");
        } else {
            sb.append(curOffset).append("  Parameters: \n");
            for (Identifier arg : args) {
                sb.append(arg.toString(offset + 2));
            }
        }
        sb.append(curOffset).append("  Body: \n");
        sb.append(funcBody.toString(offset + 2));
        return sb.toString();
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
    public String toString(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        StringBuilder sb = new StringBuilder();
        sb.append(curOffset).append("FuncCall: \n");
        sb.append(identifier.toString(offset + 1));
        sb.append(curOffset).append("  Parameters: \n");
        for (MathExpr arg : args) {
            sb.append(arg.toString(offset + 2));
        }
        return sb.toString();
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
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("Assignment: \n");
        sb.append(identifier.toString(offset + 1));
        sb.append(expr.toString(offset + 1));
        return sb.toString();
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
    public String toString(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        return curOffset + "BinaryOp: " + operator + '\n' +
                left.toString(offset + 1) +
                right.toString(offset + 1);
    }
}

// Literal Node for String and Int literals
interface Literal extends MathExpr {
}

class StringLiteral implements Literal {
    private final String value;

    StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("StringLiteral: ").append(value).append('\n');
        return sb.toString();
    }
}

class IntLiteral implements Literal {
    private final int value;

    IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("IntLiteral: ").append(value).append('\n');
        return sb.toString();
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
    public String toString(int offset) {
        String curOffset = Parser.getCurOffset(offset);
        StringBuilder sb = new StringBuilder();
        sb.append(curOffset).append("IfStatement: \n");
        sb.append(condition.toString(offset + 1));
        sb.append(curOffset).append("  Then: \n");
        sb.append(thenBlock.toString(offset + 2));
        if (elseBlock != null) {
            sb.append(curOffset).append("  Else: \n");
            sb.append(elseBlock.toString(offset + 2));
        }
        return sb.toString();
    }
}

// ReturnStatement Node for the return value of the function
class ReturnStatement implements Statement {
    private final MathExpr expression;

    ReturnStatement(MathExpr expression) {
        this.expression = expression;
    }

    @Override
    public String toString(int offset) {
        StringBuilder sb = new StringBuilder();
        String curOffset = Parser.getCurOffset(offset);
        sb.append(curOffset).append("ReturnStatement: \n");
        sb.append(expression.toString(offset + 1));
        return sb.toString();
    }
}

public class Parser {
    private Token curToken;
    private Token nextToken;
    private final Iterator<Token> tokens;
    private Program program;

    public Parser(String src) {
        Lexer lexer = new Lexer(src);
        this.tokens = lexer.iterator();
        this.curToken = tokens.next();
        this.nextToken = tokens.next();
    }

    Program parse() {
        return parseProgram();
    }

    public String toString() {
        if (program == null) {
            throw new ParserException("Program wasn't parsed");
        } else {
            return program.toString(0);
        }
    }


    private void acceptToken(String target) {
        if (curToken == null || !target.equals(curToken.repr())) {
            throw new ParserException("Incorrect syntax: expected " + target + ", found: " +
                    (curToken != null ? curToken.repr() : "null"));
        }
        nextSym();
    }

    private Program parseProgram() {
        List<Node> nodes = new ArrayList<>(); // common list for all program elements
        while (curToken != null && curToken.type() != null) {
            if (curToken.repr().equals("void") || curToken.repr().equals("int")) { // the returned value
                FuncDeclaration function = (FuncDeclaration) parseFunctionDeclaration();
                nodes.add(function);
            } else if (curToken.repr().equals("if")) {
                Statement ifStatement = parseIfStatement();
                nodes.add(ifStatement);
            } else if (curToken.repr().equals("{")) {
                Block block = parseBlock();
                nodes.add(block);
            } else {
                Statement statement = parseSentences();
                // if statement null -- the next token is '{'
                if (statement != null) {
                    nodes.add(statement);
                }
            }
        }
        if (curToken != null && curToken.type() != null) {
            throw new ParserException("Unexpected token: " + curToken.type() + ". Expected EOF.");
        }
        this.program = new Program(nodes);
        return program;
    }

    private Block parseBlock() {
        acceptToken("{"); // block should start with '{'
        List<Statement> statements = new ArrayList<>();
        while (curToken != null && !"}".equals(curToken.repr()) && curToken.type() != null) {
            if (curToken.repr().equals(";")) {
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
        if (curToken != null && "{".equals(curToken.repr())) {
            return null; // after statement, there may be a block starting with {
        }
        Statement statementStartWithKeyword = parseStatementStartWithKeyword(); // function declaration, return and if statements
        if (statementStartWithKeyword != null) return statementStartWithKeyword;
        if (curToken != null && (curToken.type() == TokenType.IDENTIFIER)) {
            return parseStatementStartWithIdentifier(); //
        } else if (curToken != null && (";".equals(curToken.repr()))) {
            nextSym(); // skip ';'
            return parseSentences(); // function declaration
        } else {
            if (nextToken != null) {
                throw new ParserException("Expected identifier for sentence but got " +
                        (curToken != null ? curToken.type() : "null"));
            }
            else {
                return null;
            }
        }
    }


    private Statement parseStatementStartWithKeyword() {
        if (curToken != null && curToken.type() == TokenType.KEYWORD) {
            return switch (curToken.repr()) {
                case "return" -> parseReturnStatement();
                case "if" -> parseIfStatement();
                default -> parseFunctionDeclaration();
            };
        }
        return null;
    }

    private Statement parseStatementStartWithIdentifier() {
        Identifier identifierNode = new Identifier(curToken.repr());
        if (nextToken != null && nextToken.type() == TokenType.ASSIGN) {
            nextSym(); // skip '='
            return parseAssignment(identifierNode); // assignment
        } else if (nextToken != null && (nextToken.type() == TokenType.OPERATION)) {
            nextSym();
            return parseExpr(); // math expression
        } else if (nextToken != null && ("(".equals(nextToken.repr()))) {
            return parseFuncCall(identifierNode); // function call
        } else {
            throw new ParserException("Unexpected token " + (nextToken != null ? nextToken.repr() : "null") + " after identifier.");
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
        while (curToken != null && operators.contains(curToken.repr())) {
            String operator = curToken.repr();
            nextSym();
            MathExpr right = parseOperand.get();
            left = new BinaryOp(left, operator, right);
        }
        return left;
    }

    private MathExpr parsePrimaryExpr() {
        if (curToken != null && "(".equals(curToken.repr())) {
            nextSym();
            MathExpr expr = parseExpr();
            acceptToken(")");
            return expr;
        } else if (curToken != null) {
            Token value = curToken;
            nextSym();
            if (value.repr().matches("-?\\d+")) { // int literal
                return new IntLiteral(Integer.parseInt(value.repr()));
            } else if (value.type() == TokenType.IDENTIFIER) {
                return new Identifier(value.repr());
            } else {
                return new StringLiteral(value.repr()); // string literal
            }
        } else {
            throw new ParserException("Unexpected token in Primary Expr");
        }
    }

    private Assignment parseAssignment(Identifier identifier) {
        if (curToken == null || curToken.type() != TokenType.ASSIGN) {
            throw new ParserException("Expected assignment.");
        }
        nextSym(); // skip '='
        if (nextToken != null && nextToken.repr().equals("(")) {
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
        if (curToken != null && "else".equals(curToken.repr())) {
            nextSym(); // skip else token
            elseBlock = parseBlock();
        }
        return new IfStatement(condition, thenBlock, elseBlock);
    }

    private Statement parseFunctionDeclaration() {
        String type = curToken.repr();
        nextSym(); // skip return type
        Identifier identifier = new Identifier(curToken.repr()); // function name
        nextSym(); // skip function name
        acceptToken("(");
        List<Identifier> args = new ArrayList<>();
        // parse arguments
        while (curToken != null && !")".equals(curToken.repr())) {
            args.add(new Identifier(curToken.repr()));
            nextSym(); // skip arg
            if (curToken != null && ",".equals(curToken.repr())) {
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

        while (curToken != null && !")".equals(curToken.repr())) {
            if (",".equals(curToken.repr())) {
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
            Parser parser = new Parser(sourceCode);
            Program program1 = parser.parse();
            System.out.println(program1.toString(0));
        } catch (Parser.ParserException e) {
            System.err.println("Parser error: " + e.getMessage());
        }
    }
}