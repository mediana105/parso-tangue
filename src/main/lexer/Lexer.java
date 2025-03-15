package main.lexer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Lexer implements Iterable<Token> {

    private final String src; // input code

    public Lexer(String src) {
        this.src = src;
    }

    public record Pos(int line, int col) {
    }

    private int curIndex = 0; // current position of main.lexer iterator in input code
    private int col = 1;
    private int line = 1;
    private boolean isEOF = false;

    @Override
    public Iterator<Token> iterator() {
        return new Iterator<>() {
            private Token nextToken = null;

            @Override
            public boolean hasNext() {
                if (nextToken == null) {
                    int prevInd = curIndex;
                    int prevCol = col;
                    int prevLine = line;
                    try {
                        nextToken = getNextToken();
                    } catch (LexerException e) {
                        throw new RuntimeException(e);
                    }
                    curIndex = prevInd;
                    col = prevCol;
                    line = prevLine;
                }
                return nextToken != null;
            }

            @Override
            public Token next() {
                try {
                    nextToken = getNextToken();
                } catch (LexerException e) {
                    throw new RuntimeException(e);
                }
                if (nextToken != null) {
                    return nextToken;
                } else {
                    return null;
                }
            }
        };
    }

    private Token createRegexToken(TokenType tokenType, Matcher matchResult) {
        String tokenValue = matchResult.group();
        curIndex += tokenValue.length();
        Token token = new TokenImpl(tokenType, tokenValue, new Pos(line, col));
        col += tokenValue.length();
        return token;
    }

    private Token createSingleCharToken(TokenType tokenType, char character) {
        String tokenValue = String.valueOf(character);
        Token token = new TokenImpl(tokenType, tokenValue, new Pos(line, col));
        curIndex++;
        col++;
        return token;
    }

    private void processWhitespaces() {
        if (src.charAt(curIndex) == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        curIndex++;
    }

    private Token processStringLiteral() throws LexerException {
        StringBuilder curToken = new StringBuilder();
        curToken.append('"');
        curIndex++; // Skip the opening quotation mark
        col++;

        while (curIndex < src.length()) {
            if (src.charAt(curIndex) == '"') {
                curToken.append('"'); // Closing quotation mark
                curIndex++;
                col++;
                break; // String ended
            } else if (src.charAt(curIndex) == '\\') {
                // Processing of escaped characters
                curIndex++;
                col++;
                if (curIndex >= src.length()) {
                    throw new LexerException("Unterminated escape sequence at Pos(line=" + line + ", col=" + col + ")");
                }
                switch (src.charAt(curIndex)) {
                    case '\\':
                        curToken.append('\\');
                        break;
                    case '"':
                        curToken.append('"');
                        break;
                    case 'n':
                        curToken.append('\n');
                        break;
                    case 'r':
                        curToken.append('\r');
                        break;
                    case 't':
                        curToken.append('\t');
                        break;
                    case 'b':
                        curToken.append('\b');
                        break;
                    case '$':
                        curToken.append('$');
                        break;
                    default:
                        throw new LexerException("Incorrect escaped symbol: \\" + src.charAt(curIndex) + " at Pos(line=" + line + ", col=" + col + ")");
                }
                curIndex++;
                col++;
            } else {
                curToken.append(src.charAt(curIndex));
                curIndex++;
                col++;
            }
        }

        // Checking that the string ends with closing quotation mark
        if (curToken.charAt(curToken.length() - 1) != '"') {
            throw new LexerException("Unterminated string literal at Pos(line=" + line + ", col=" + col + ")");
        }

        return new TokenImpl(TokenType.STRING, curToken.toString(), new Pos(line, col - curToken.length()));
    }

    private Token getNextToken() throws LexerException {
        List<Character> arithmeticOperations = Arrays.asList('+', '-', '*', '/', '%');
        List<Character> specialsSymbols = Arrays.asList(',', '{', '}', ';', '(', ')', ';');

        if (curIndex >= src.length()) {
            if (!isEOF) {
                isEOF = true;
            }
            return null;
        }

        while (curIndex < src.length()) {
            // Regular expression for keyword search: var, void, if, else, return
            Pattern keyWords = Pattern.compile("\\b(var|void|if|else|return)\\b");
            Matcher keyWordsMatcher = keyWords.matcher(src).region(curIndex, src.length());
            if (keyWordsMatcher.lookingAt()) {
                return createRegexToken(TokenType.KEYWORD, keyWordsMatcher);
            }

            // Identifiers - names of functions, names of variables
            Pattern identifiers = Pattern.compile("[$_a-zA-Z][$_a-zA-Z0-9]*");
            Matcher identifiersMatcher = identifiers.matcher(src).region(curIndex, src.length());
            if (identifiersMatcher.lookingAt()) {
                return createRegexToken(TokenType.IDENTIFIER, identifiersMatcher);
            }

            // Integers
            Pattern integers = Pattern.compile("-?\\d+");
            Matcher integersMatcher = integers.matcher(src).region(curIndex, src.length());
            if (integersMatcher.lookingAt()) {
                return createRegexToken(TokenType.INT, integersMatcher);
            }

            // Whitespace characters -- skip  to divide into tokens
            if (Character.isWhitespace(src.charAt(curIndex))) {
                processWhitespaces();
                continue;
            }

            // Comparison and assignment operators
            if (src.charAt(curIndex) == '=') {
                StringBuilder curToken = new StringBuilder();
                curToken.append("=");
                curIndex++;
                TokenType tokenType;
                if (curIndex < src.length() && src.charAt(curIndex) == '=') {
                    curToken.append(src.charAt(curIndex));
                    curIndex++;
                    tokenType = TokenType.COMPARISON;
                } else {
                    tokenType = TokenType.ASSIGN;
                }
                col += curToken.length();
                return new TokenImpl(tokenType, curToken.toString(), new Pos(line, col - curToken.length()));
            }

            // != operator
            if (src.charAt(curIndex) == '!') {
                if (curIndex < src.length() - 1 && src.charAt(++curIndex) == '=') {
                    col += 2;
                    curIndex++;
                    return new TokenImpl(TokenType.COMPARISON, "!=", new Pos(line, col - 2));
                } else {
                    throw new LexerException("Unknown character '!' at Pos(line=" + line + ", col=" + col + ")");
                }
            }

            // >= and <= operators
            if (src.charAt(curIndex) == '>' || src.charAt(curIndex) == '<') {
                char firstChar = src.charAt(curIndex);
                curIndex++;
                if (curIndex < src.length() && src.charAt(curIndex) == '=') {
                    // >= or <=
                    String operator = firstChar + "=";
                    col += 2;
                    curIndex++;
                    return new TokenImpl(TokenType.COMPARISON, operator, new Pos(line, col - 2));
                } else {
                    // > or <
                    col += 1;
                    return new TokenImpl(TokenType.OPERATION, String.valueOf(firstChar), new Pos(line, col - 1));
                }
            }

            // Check the remaining comparison operators and arithmetic operators
            if (specialsSymbols.contains(src.charAt(curIndex))) {
                return createSingleCharToken(TokenType.SPECIAL, src.charAt(curIndex));
            }

            // If the token starts with a quotation mark, it is a string
            if (src.charAt(curIndex) == '"') {
                return processStringLiteral();
            }

            // Check the remaining comparison operators and arithmetic operators
            if (arithmeticOperations.contains(src.charAt(curIndex))) {
                return createSingleCharToken(TokenType.OPERATION, src.charAt(curIndex));
            }

            throw new LexerException("Incorrect token at Pos(line=" + line + ", col=" + col + ")");
        }

        return null;
    }

    public static class LexerException extends RuntimeException {
        public LexerException(String message) {
            super(message);
        }
    }
}