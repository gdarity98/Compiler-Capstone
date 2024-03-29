package edu.montana.csci.csci468.tokenizer;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptTokenizer {

    TokenList tokenList;
    String src;
    int postion = 0;
    int line = 1;
    int lineOffset = 0;

    public CatScriptTokenizer(String source) {
        src = source;
        tokenList = new TokenList(this);
        tokenize();
    }

    private void tokenize() {
        consumeWhitespace();
        while (!tokenizationEnd()) {
            scanToken();
            consumeWhitespace();
        }
        tokenList.addToken(EOF, "<EOF>", postion, postion, line, lineOffset);
    }

    private void scanToken() {
        if(scanNumber()) {
            return;
        }
        if(scanString()) {
            return;
        }
        if(scanIdentifier()) {
            return;
        }
        scanSyntax();
    }

    private boolean scanString() {
        // Done I think
        if(!isDigit(peek())){
            int start = postion;
            char c = peek();
            String cS = Character.toString(c);
            if(!cS.equals("\"")){
                return false;
            }
            while(!isDigit(peek()) && !tokenizationEnd()){
                c = peek();
                takeChar();
                char s = src.charAt(start);
                try{
                    char e = src.charAt(postion);
                    String sS = Character.toString(s);
                    String sE = Character.toString(e);
                    if(sS.equals(" ")){
                        return false;
                    }
                    if(s == e){
                        start += 1;
                        tokenList.addToken(STRING, src.substring(start, postion), start, postion, line, lineOffset);
                        postion += 1;
                        start = postion;
                    }else if(sE.equals("\"") && !sS.equals("\"")){
                        postion = start;
                        return false;
                    }
                }catch(Exception z){
                    char e = src.charAt(postion-1);
                    String sS = Character.toString(s);
                    String sE = Character.toString(e);
                    if(!sE.equals("\"") && !sS.equals("\"")){
                        postion = start;
                        return true;
                        // scanIdentifier();
                    }else{
                        tokenList.addToken(ERROR, "<No String Close>", start, postion, line, lineOffset);
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    private boolean scanIdentifier() {
        if( isAlpha(peek())) {
            int start = postion;
            while (isAlphaNumeric(peek())) {
                takeChar();
            }
            String value = src.substring(start, postion);
            if (KEYWORDS.containsKey(value)) {
                tokenList.addToken(KEYWORDS.get(value), value, start, postion, line, lineOffset);
            } else {
                tokenList.addToken(IDENTIFIER, value, start, postion, line, lineOffset);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean scanNumber() {
        if(isDigit(peek())) {
            int start = postion;
            while (isDigit(peek())) {
                takeChar();
            }
            tokenList.addToken(INTEGER, src.substring(start, postion), start, postion, line, lineOffset);
            return true;
        } else {
            return false;
        }
    }

    private void scanSyntax() {
        int start = postion;
        if(matchAndConsume('+')) {
            tokenList.addToken(PLUS, "+", start, postion, line, lineOffset);
        } else if(matchAndConsume('-')) {
            tokenList.addToken(MINUS, "-", start, postion, line, lineOffset);
        } else if(matchAndConsume('=')) {
            if (matchAndConsume('=')) {
                tokenList.addToken(EQUAL_EQUAL, "==", start, postion, line, lineOffset);
            } else {
                tokenList.addToken(EQUAL, "=", start, postion, line, lineOffset);
            }
        } else if(matchAndConsume(')')){
            tokenList.addToken(RIGHT_PAREN,")",start,postion,line,lineOffset);
        } else if(matchAndConsume('(')){
            tokenList.addToken(LEFT_PAREN,"(",start,postion,line,lineOffset);
        }else if(matchAndConsume('}')){
            tokenList.addToken(RIGHT_BRACE,"}",start,postion,line,lineOffset);
        }else if(matchAndConsume('{')){
            tokenList.addToken(LEFT_BRACE,"{",start,postion,line,lineOffset);
        }else if(matchAndConsume(']')){
            tokenList.addToken(RIGHT_BRACKET,"]",start,postion,line,lineOffset);
        }else if(matchAndConsume('[')){
            tokenList.addToken(LEFT_BRACKET,"[",start,postion,line,lineOffset);
        }else if(matchAndConsume(':')){
            tokenList.addToken(COLON,":",start,postion,line,lineOffset);
        }else if(matchAndConsume(',')){
            tokenList.addToken(COMMA,",",start,postion,line,lineOffset);
        }else if(matchAndConsume('.')){
            tokenList.addToken(DOT,".",start,postion,line,lineOffset);
        }else if(matchAndConsume('/')){
            if(matchAndConsume('/')){
                while(peek() != '\n' && !tokenizationEnd()){
                    char p = peek();
                    takeChar();
                }
                line++;
                //Is this right for lineOffset
                lineOffset++;
            }else {
                tokenList.addToken(SLASH, "/", start, postion, line, lineOffset);
            }
        }else if(matchAndConsume('*')){
            tokenList.addToken(STAR,"*",start,postion,line,lineOffset);
        }else if(matchAndConsume('!')){
            if(matchAndConsume('=')){
                tokenList.addToken(BANG_EQUAL,"!=",start,postion,line,lineOffset);
            }
        }else if(matchAndConsume('>')){
            if(matchAndConsume('=')){
                tokenList.addToken(GREATER_EQUAL, ">=", start, postion, line, lineOffset);
            }else{
                tokenList.addToken(GREATER, ">", start, postion, line, lineOffset);
            }
        }else if(matchAndConsume('<')){
            if(matchAndConsume('=')){
                tokenList.addToken(LESS_EQUAL, "<=", start, postion, line, lineOffset);
            }else{
                tokenList.addToken(LESS, "<", start, postion, line, lineOffset);
            }
        }else {
            tokenList.addToken(ERROR, "<Unexpected Token: [" + takeChar() + "]>", start, postion, line, lineOffset);
        }
    }

    private void consumeWhitespace() {
        // Done I think? idk about lineOffset
        while (!tokenizationEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t') {
                postion++;
                continue;
            } else if (c == '\n') {
                postion++;
                line++;
                //Idk if this is how you do lineOffset?
                lineOffset++;
                continue;
            }
            break;
        }
    }

    //===============================================================
    // Utility functions
    //===============================================================

    private char peek() {
        if (tokenizationEnd()) return '\0';
        return src.charAt(postion);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char takeChar() {
        char c = src.charAt(postion);
        postion++;
        return c;
    }

    private boolean tokenizationEnd() {
        return postion >= src.length();
    }

    public boolean matchAndConsume(char c) {
        if (peek() == c) {
            takeChar();
            return true;
        }
        return false;
    }

    public TokenList getTokens() {
        return tokenList;
    }
}