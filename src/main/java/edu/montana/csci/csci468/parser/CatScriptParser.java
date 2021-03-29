package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.List;

import static edu.montana.csci.csci468.tokenizer.TokenType.*;

public class CatScriptParser {

    private TokenList tokens;
    private FunctionDefinitionStatement currentFunctionDefinition;

    public CatScriptProgram parse(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();

        // first parse an expression
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        if (tokens.hasMoreTokens()) {
            tokens.reset();
            while (tokens.hasMoreTokens()) {
                program.addStatement(parseProgramStatement());
            }
        } else {
            program.setExpression(expression);
        }

        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    public CatScriptProgram parseAsExpression(String source) {
        tokens = new CatScriptTokenizer(source).getTokens();
        CatScriptProgram program = new CatScriptProgram();
        program.setStart(tokens.getCurrentToken());
        Expression expression = parseExpression();
        program.setExpression(expression);
        program.setEnd(tokens.getCurrentToken());
        return program;
    }

    //============================================================
    //  Statements
    //============================================================

    private Statement parseProgramStatement() {
        Statement printStmt = parsePrintStatement();
        if (printStmt != null) {
            return printStmt;
        }

        Statement varStmt = parseVariableStatement();
        if(varStmt != null){
            return varStmt;
        }

        Statement assFunStmt = parseAssignmentOrFunctionStatement();
        if(assFunStmt != null){
            return assFunStmt;
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseForStatement(){
        if(tokens.match(FOR)){
            return null;
        }else{
            return null;
        }
    }

    private Statement parseIfStatement(){
        if(tokens.match(IF)){
            return null;
        }else{
            return null;
        }
    }

    private Statement parsePrintStatement() {
        if (tokens.match(PRINT)) {

            PrintStatement printStatement = new PrintStatement();
            printStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, printStatement);
            printStatement.setExpression(parseExpression());
            printStatement.setEnd(require(RIGHT_PAREN, printStatement));

            return printStatement;
        } else {
            return null;
        }
    }

    private Statement parseVariableStatement(){
        if(tokens.match(VAR)){
            VariableStatement variableStatement = new VariableStatement();
            variableStatement.setStart(tokens.consumeToken());

            if(tokens.match(IDENTIFIER)){
                Token name = tokens.consumeToken();
                variableStatement.setVariableName(name.getStringValue());
            }
            if(tokens.match(COLON)){
                // I need to make a type_expression and call it here
                // This is kinda what Im thinking for it but it is wrong!!
                tokens.consumeToken();
                if(tokens.getCurrentToken().getStringValue().equals("int")){
                    tokens.consumeToken();
                    variableStatement.setExplicitType(CatscriptType.INT);
                }else if(tokens.getCurrentToken().getStringValue().equals("string")){
                    tokens.consumeToken();
                    variableStatement.setExplicitType(CatscriptType.STRING);
                }else if(tokens.getCurrentToken().getStringValue().equals("bool")){
                    tokens.consumeToken();
                    variableStatement.setExplicitType(CatscriptType.BOOLEAN);
                }else if(tokens.getCurrentToken().getStringValue().equals("object")){
                    tokens.consumeToken();
                    variableStatement.setExplicitType(CatscriptType.OBJECT);
                }else if(tokens.getCurrentToken().getStringValue().equals("list")){
                    tokens.consumeToken();
                    require(LESS, variableStatement);
                    // need to recursively call type_expression from here (could have a list<list<list<list<int>>>>)
                    // need something like this also: variableStatement.setExplicitType(CatscriptType.INT);
                }
            }
            require(EQUAL, variableStatement);
            variableStatement.setExpression(parseExpression());
            return variableStatement;
        }else{
            return null;
        }
    }

    private Statement parseAssignmentOrFunctionStatement(){
        if(tokens.match(IDENTIFIER)){
            if(tokens.match(LEFT_PAREN)){
                return parseFunctionCallStatement();
            }else{
                return parseAssignmentStatement();
            }
        }else{
            return null;
        }
    }

    private Statement parseAssignmentStatement(){
        return null;
    }

    private Statement parseFunctionCallStatement(){
        return null;
    }

    /*
    Also Need:
        functionDeclaration
        functionBodyStatement
        parameterList
        parameter
        returnStatement
     */

    //============================================================
    //  Expressions
    //============================================================

    private Expression parseExpression() {
        return parseEqualityExpression();
    }

    private Expression parseEqualityExpression(){
        Expression expression = parseComparisonExpression();
        if(tokens.match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = tokens.consumeToken();
            final Expression rhs = parseComparisonExpression();
            EqualityExpression equalityExpression = new EqualityExpression(operator, expression, rhs);
            equalityExpression.setStart(expression.getStart());
            equalityExpression.setEnd(rhs.getEnd());
            return equalityExpression;
        } else {
            return expression;
        }
    }

    private Expression parseComparisonExpression(){
        Expression expression = parseAdditiveExpression();
        if(tokens.match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = tokens.consumeToken();
            final Expression rhs = parseAdditiveExpression();
            ComparisonExpression comparisonExpression = new ComparisonExpression(operator, expression, rhs);
            comparisonExpression.setStart(expression.getStart());
            comparisonExpression.setEnd(rhs.getEnd());
            return comparisonExpression;
        } else {
            return expression;
        }
    }

    private Expression parseAdditiveExpression() {
        Expression expression = parseFactorExpression();
        while (tokens.match(PLUS, MINUS)) {
            Token operator = tokens.consumeToken();
            final Expression rightHandSide = parseFactorExpression();
            expression = new AdditiveExpression(operator, expression, rightHandSide);
            expression.setStart(expression.getStart());
            expression.setEnd(rightHandSide.getEnd());
        }
        return expression;
    }

    private Expression parseFactorExpression(){
        Expression expression = parseUnaryExpression();
        while(tokens.match(SLASH, STAR)){
            Token operator = tokens.consumeToken();
            final Expression rhs = parseUnaryExpression();
            expression = new FactorExpression(operator, expression, rhs);
            expression.setStart(expression.getStart());
            expression.setEnd(rhs.getEnd());
        }
        return expression;
    }

    private Expression parseUnaryExpression(){
        if(tokens.match(MINUS, NOT)){
            Token token = tokens.consumeToken();
            Expression rhs = parseUnaryExpression();
            UnaryExpression unaryExpression = new UnaryExpression(token, rhs);
            unaryExpression.setStart(token);
            unaryExpression.setEnd(rhs.getEnd());
            return unaryExpression;
        }else{
            return parsePrimaryExpression();
        }
    }

    private Expression parsePrimaryExpression() {
        if(tokens.match(IDENTIFIER)) {
            Token identifierToken = tokens.consumeToken();
            if(tokens.match(LEFT_PAREN)){
                Token start = tokens.consumeToken();
                List<Expression> argumentList = new ArrayList<>();
                if(tokens.match(RIGHT_PAREN)){
                    FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
                    return functionCallExpression;
                }else{
                    Expression val = parseExpression();
                    argumentList.add(val);
                }
                while(tokens.match(COMMA)){
                    Token comma = tokens.consumeToken();
                    Expression val = parseExpression();
                    argumentList.add(val);
                }
                boolean rightBracket = tokens.match(RIGHT_PAREN);
                if(rightBracket){
                    FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
                    return functionCallExpression;
                }else{
                    FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
                    functionCallExpression.addError(ErrorType.UNTERMINATED_ARG_LIST);
                    return functionCallExpression;
                }
            }else {
                IdentifierExpression identifierExpression = new IdentifierExpression(identifierToken.getStringValue());
                identifierExpression.setToken(identifierToken);
                return identifierExpression;
            }
        } else if(tokens.match(STRING)){
            Token stringToken = tokens.consumeToken();
            StringLiteralExpression stringExpression = new StringLiteralExpression(stringToken.getStringValue());
            stringExpression.setToken(stringToken);
            return stringExpression;
        } else if (tokens.match(INTEGER)) {
            Token integerToken = tokens.consumeToken();
            IntegerLiteralExpression integerExpression = new IntegerLiteralExpression(integerToken.getStringValue());
            integerExpression.setToken(integerToken);
            return integerExpression;
        } else if(tokens.match(TRUE) || tokens.match(FALSE)){
            Token booleanToken = tokens.consumeToken();
            BooleanLiteralExpression booleanLiteralExpression = new BooleanLiteralExpression(booleanToken.getType() == TRUE);
            booleanLiteralExpression.setToken(booleanToken);
            return booleanLiteralExpression;
        } else if(tokens.match(NULL)){
            Token nullToken = tokens.consumeToken();
            NullLiteralExpression nullLiteralExpression = new NullLiteralExpression();
            nullLiteralExpression.setToken(nullToken);
            return nullLiteralExpression;
        } else if(tokens.match(LEFT_BRACKET)){
            Token start = tokens.consumeToken();
            List<Expression> exprs = new ArrayList<>();
            if(tokens.match(RIGHT_BRACKET)){
                ListLiteralExpression listLiteralExpression = new ListLiteralExpression(exprs);
                return listLiteralExpression;
            }else{
                Expression val = parseExpression();
                exprs.add(val);
            }
            while(tokens.match(COMMA)){
                Token comma = tokens.consumeToken();
                Expression val = parseExpression();
                exprs.add(val);
            }
            boolean rightBracket = tokens.match(RIGHT_BRACKET);
            if(rightBracket){
                ListLiteralExpression listLiteralExpression = new ListLiteralExpression(exprs);
                return listLiteralExpression;
            }else{
                ListLiteralExpression listLiteralExpression = new ListLiteralExpression(exprs);
                listLiteralExpression.addError(ErrorType.UNTERMINATED_LIST);
                return listLiteralExpression;
            }

        } else if(tokens.match(LEFT_PAREN)){
            Token start = tokens.consumeToken();
            Expression expression = parseExpression();
            boolean rightParen = tokens.match(RIGHT_PAREN);
            if(rightParen){
                ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(expression);
                return parenthesizedExpression;
            }else{
                ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(expression);
                parenthesizedExpression.addError(ErrorType.UNEXPECTED_TOKEN);
                return parenthesizedExpression;
            }
        } else {
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression();
            syntaxErrorExpression.setToken(tokens.consumeToken());
            return syntaxErrorExpression;
        }
    }

    /*
    Need to add TypeExpression
     */

    //============================================================
    //  Parse Helpers
    //============================================================
    private Token require(TokenType type, ParseElement elt) {
        return require(type, elt, ErrorType.UNEXPECTED_TOKEN);
    }

    private Token require(TokenType type, ParseElement elt, ErrorType msg) {
        if(tokens.match(type)){
            return tokens.consumeToken();
        } else {
            elt.addError(msg, tokens.getCurrentToken());
            return tokens.getCurrentToken();
        }
    }

}
