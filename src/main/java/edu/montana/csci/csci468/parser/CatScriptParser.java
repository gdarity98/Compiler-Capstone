package edu.montana.csci.csci468.parser;

import edu.montana.csci.csci468.parser.expressions.*;
import edu.montana.csci.csci468.parser.statements.*;
import edu.montana.csci.csci468.tokenizer.CatScriptTokenizer;
import edu.montana.csci.csci468.tokenizer.Token;
import edu.montana.csci.csci468.tokenizer.TokenList;
import edu.montana.csci.csci468.tokenizer.TokenType;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
        Statement forStmt = parseForStatement();
        if(forStmt != null){
            return forStmt;
        }

        Statement ifStmt = parseIfStatement();
        if(ifStmt != null){
            return ifStmt;
        }

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
        Statement funcDeclare = parseFunctionDeclaration();
        if(funcDeclare != null){
            return funcDeclare;
        }
        return new SyntaxErrorStatement(tokens.consumeToken());
    }

    private Statement parseForStatement(){
        if(tokens.match(FOR)){
            ForStatement forStatement = new ForStatement();
            forStatement.setStart(tokens.consumeToken());

            require(LEFT_PAREN, forStatement);

            forStatement.setVariableName(require(IDENTIFIER, forStatement).getStringValue());
            String name = forStatement.getVariableName();

            require(IN, forStatement);

            forStatement.setExpression(parseExpression());

            require(RIGHT_PAREN, forStatement);

            require(LEFT_BRACE, forStatement);

            List<Statement> statementList = new ArrayList<>();
            while(!tokens.match(RIGHT_BRACE)){
                if(tokens.match(EOF)){
                    break;
                }
                Statement aStatement = parseProgramStatement();
                statementList.add(aStatement);
            }
            forStatement.setBody(statementList);
            require(RIGHT_BRACE, forStatement);
            return forStatement;
        }else{
            return null;
        }
    }

    private Statement parseIfStatement(){
        if(tokens.match(IF)){
            IfStatement ifStatement = new IfStatement();
            ifStatement.setStart(tokens.consumeToken());
            require(LEFT_PAREN, ifStatement);
            ifStatement.setExpression(parseExpression());
            require(RIGHT_PAREN, ifStatement);
            require(LEFT_BRACE, ifStatement);
            List<Statement> trueStatementList = new ArrayList<>();
            while(!tokens.match(RIGHT_BRACE)){
                if(tokens.match(EOF)){
                    break;
                }
                Statement aStatement = parseProgramStatement();
                trueStatementList.add(aStatement);
            }
            ifStatement.setTrueStatements(trueStatementList);
            require(RIGHT_BRACE, ifStatement);

            List<Statement> elseStatementList = new ArrayList<>();
            if(tokens.match(ELSE)){
                tokens.consumeToken();
                if(tokens.match(IF)){
                    elseStatementList.add(parseIfStatement());
                }else{
                    require(LEFT_BRACE,ifStatement);
                    while(!tokens.match(RIGHT_BRACE)){
                        if(tokens.match(EOF)){
                            break;
                        }
                        Statement aStatement = parseProgramStatement();
                        elseStatementList.add(aStatement);
                    }
                    ifStatement.setElseStatements(elseStatementList);
                    require(RIGHT_BRACE, ifStatement);
                }
            }
            return ifStatement;
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
                // This is kinda what Im thinking for it
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
                    int count = 1;
                    while(tokens.getCurrentToken().getStringValue().equals("list")){
                        tokens.consumeToken();
                        require(LESS, variableStatement);
                        count = count + 1;
                    }

                    if(tokens.getCurrentToken().getStringValue().equals("int")){
                        tokens.consumeToken();
                        variableStatement.setExplicitType(CatscriptType.getListType(CatscriptType.INT));
                    }else if(tokens.getCurrentToken().getStringValue().equals("string")){
                        tokens.consumeToken();
                        variableStatement.setExplicitType(CatscriptType.getListType(CatscriptType.INT));
                    }else if(tokens.getCurrentToken().getStringValue().equals("bool")){
                        tokens.consumeToken();
                        variableStatement.setExplicitType(CatscriptType.getListType(CatscriptType.INT));
                    }else if(tokens.getCurrentToken().getStringValue().equals("object")) {
                        tokens.consumeToken();
                        variableStatement.setExplicitType(CatscriptType.getListType(CatscriptType.INT));
                    }
                    for(int k = 0; k < count; k++){
                        require(GREATER, variableStatement);
                    }
                }
            }
            require(EQUAL, variableStatement);
            variableStatement.setExpression(parseExpression());
            // variableStatement.setExplicitType(variableStatement.getExpression().getType());
            return variableStatement;
        }else{
            return null;
        }
    }

    private Statement parseAssignmentOrFunctionStatement(){
        if(tokens.match(IDENTIFIER)){
            Token identifierToken = tokens.consumeToken();
            if(tokens.match(LEFT_PAREN)){
                return parseFunctionCallStatement(identifierToken);
            }else{
                return parseAssignmentStatement(identifierToken);
            }
        }else{
            return null;
        }
    }

    private Statement parseAssignmentStatement(Token identifierToken){
        AssignmentStatement assignmentStatement = new AssignmentStatement();
        assignmentStatement.setStart(identifierToken);
        assignmentStatement.setVariableName(identifierToken.getStringValue());
        require(EQUAL, assignmentStatement);
        assignmentStatement.setExpression(parseExpression());
        return assignmentStatement;
    }

    private Statement parseFunctionCallStatement(Token identifierToken){
        List<Expression> argumentList = new ArrayList<>();
        FunctionCallExpression functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
        if(tokens.match(LEFT_PAREN)) {
            Token start = tokens.consumeToken();
            if (tokens.match(RIGHT_PAREN)) {
                functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
            } else {
                Expression val = parseExpression();
                argumentList.add(val);
            }
            while (tokens.match(COMMA)) {
                Token comma = tokens.consumeToken();
                Expression val = parseExpression();
                argumentList.add(val);
            }
            require(RIGHT_PAREN, functionCallExpression);
            functionCallExpression = new FunctionCallExpression(identifierToken.getStringValue(), argumentList);
        }
        FunctionCallStatement functionStatement = new FunctionCallStatement(functionCallExpression);
        return functionStatement;
    }

    private Statement parseFunctionDeclaration(){
        if(tokens.match(FUNCTION)){
            FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement();
            functionDefinitionStatement.setStart(tokens.consumeToken());
            functionDefinitionStatement.setName(require(tokens.getCurrentToken().getType(),
                    functionDefinitionStatement).getStringValue());
            require(LEFT_PAREN, functionDefinitionStatement);

            // Parameter list
            Token name = tokens.getCurrentToken();
            TypeLiteral typeLiteral = new TypeLiteral();
            while(!tokens.match(RIGHT_PAREN)){
                if(tokens.match(COMMA)){
                    Token comma = tokens.consumeToken();
                }
                if(tokens.match(IDENTIFIER)){
                    name = tokens.consumeToken();
                }
                if(tokens.match(COLON)){
                    tokens.consumeToken();
                    if(tokens.getCurrentToken().getStringValue().equals("int")){
                        tokens.consumeToken();
                        typeLiteral.setType(CatscriptType.INT);
                        functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                    }else if(tokens.getCurrentToken().getStringValue().equals("string")){
                        tokens.consumeToken();
                        typeLiteral.setType(CatscriptType.STRING);
                        functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                    }else if(tokens.getCurrentToken().getStringValue().equals("bool")){
                        tokens.consumeToken();
                        typeLiteral.setType(CatscriptType.BOOLEAN);
                        functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                    }else if(tokens.getCurrentToken().getStringValue().equals("object")){
                        tokens.consumeToken();
                        typeLiteral.setType(CatscriptType.OBJECT);
                        functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                    }else if(tokens.getCurrentToken().getStringValue().equals("list")){
                        tokens.consumeToken();
                        int count = 0;
                        if(tokens.match(LESS)){
                            require(LESS, functionDefinitionStatement);
                            count = 1;
                            while(tokens.getCurrentToken().getStringValue().equals("list")){
                                tokens.consumeToken();
                                require(LESS, functionDefinitionStatement);
                                count = count + 1;
                            }
                            if(tokens.getCurrentToken().getStringValue().equals("int")){
                                tokens.consumeToken();
                                typeLiteral.setType(CatscriptType.getListType(CatscriptType.INT));
                                functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                            }else if(tokens.getCurrentToken().getStringValue().equals("string")){
                                tokens.consumeToken();
                                typeLiteral.setType(CatscriptType.getListType(CatscriptType.STRING));
                                functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                            }else if(tokens.getCurrentToken().getStringValue().equals("bool")){
                                tokens.consumeToken();
                                typeLiteral.setType(CatscriptType.getListType(CatscriptType.BOOLEAN));
                                functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                            }else if(tokens.getCurrentToken().getStringValue().equals("object")){
                                tokens.consumeToken();
                                typeLiteral.setType(CatscriptType.getListType(CatscriptType.OBJECT));
                                functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                            }
                            if(count != 0){
                                for(int k = 0; k < count; k++){
                                    require(GREATER, functionDefinitionStatement);
                                }
                            }
                        }else{
                            typeLiteral.setType(CatscriptType.getListType(CatscriptType.OBJECT));
                            functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                        }
                    }
                }else{
                    typeLiteral.setType(CatscriptType.OBJECT);
                    functionDefinitionStatement.addParameter(name.getStringValue(), typeLiteral);
                }
            }
            require(RIGHT_PAREN, functionDefinitionStatement);

            //Function return type
            TypeLiteral returnType = new TypeLiteral();
            if(tokens.match(COLON)){
                Token colon = tokens.consumeToken();
                if(tokens.getCurrentToken().getStringValue().equals("int")){
                    tokens.consumeToken();
                    returnType.setType(CatscriptType.INT);
                    functionDefinitionStatement.setType(returnType);
                }else if(tokens.getCurrentToken().getStringValue().equals("string")){
                    tokens.consumeToken();
                    returnType.setType(CatscriptType.STRING);
                    functionDefinitionStatement.setType(returnType);
                }else if(tokens.getCurrentToken().getStringValue().equals("bool")){
                    tokens.consumeToken();
                    returnType.setType(CatscriptType.BOOLEAN);
                    functionDefinitionStatement.setType(returnType);
                }else if(tokens.getCurrentToken().getStringValue().equals("object")){
                    tokens.consumeToken();
                    returnType.setType(CatscriptType.OBJECT);
                    functionDefinitionStatement.setType(returnType);
                }else if(tokens.getCurrentToken().getStringValue().equals("list")) {
                    tokens.consumeToken();
                    require(LESS, functionDefinitionStatement);
                    int count = 1;
                    while (tokens.getCurrentToken().getStringValue().equals("list")) {
                        tokens.consumeToken();
                        require(LESS, functionDefinitionStatement);
                        count = count + 1;
                    }

                    if (tokens.getCurrentToken().getStringValue().equals("int")) {
                        tokens.consumeToken();
                        returnType.setType(CatscriptType.INT);
                        functionDefinitionStatement.setType(returnType);
                    } else if (tokens.getCurrentToken().getStringValue().equals("string")) {
                        tokens.consumeToken();
                        returnType.setType(CatscriptType.STRING);
                        functionDefinitionStatement.setType(returnType);
                    } else if (tokens.getCurrentToken().getStringValue().equals("bool")) {
                        tokens.consumeToken();
                        returnType.setType(CatscriptType.BOOLEAN);
                        functionDefinitionStatement.setType(returnType);
                    } else if (tokens.getCurrentToken().getStringValue().equals("object")) {
                        tokens.consumeToken();
                        returnType.setType(CatscriptType.OBJECT);
                        functionDefinitionStatement.setType(returnType);
                    }
                    for (int k = 0; k < count; k++) {
                        require(GREATER, functionDefinitionStatement);
                    }
                }
            }else{
                returnType.setType(CatscriptType.VOID);
                functionDefinitionStatement.setType(returnType);
            }

            // Function Body Statement or Return
            ReturnStatement returnStatement = new ReturnStatement();
            require(LEFT_BRACE, functionDefinitionStatement);
            List<Statement> statementList = new ArrayList<>();
            while(!tokens.match(RIGHT_BRACE)){
                if(tokens.match(EOF)){
                    break;
                }
                if(tokens.match(RETURN)){
                    tokens.consumeToken();
                    if(tokens.match(RIGHT_BRACE)){
                        statementList.add(returnStatement);
                        break;
                    }else{
                        returnStatement.setExpression(parseExpression());
                        statementList.add(returnStatement);
                    }
                    break;
                }
                Statement aStatement = parseProgramStatement();
                statementList.add(aStatement);

            }
            functionDefinitionStatement.setBody(statementList);
            require(RIGHT_BRACE, functionDefinitionStatement);
            returnStatement.setFunctionDefinition(functionDefinitionStatement);
            return functionDefinitionStatement;
        }else{
            return null;
        }
    }


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
                    Token closing_paren = tokens.consumeToken();
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
                boolean rightParen = tokens.match(RIGHT_PAREN);
                if(rightParen){
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
                Token end = tokens.consumeToken();
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
                Token end = tokens.consumeToken();
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
            ParenthesizedExpression parenthesizedExpression = new ParenthesizedExpression(expression);
            require(RIGHT_PAREN, parenthesizedExpression);
            return parenthesizedExpression;
        } else {
            SyntaxErrorExpression syntaxErrorExpression = new SyntaxErrorExpression();
            syntaxErrorExpression.setToken(tokens.consumeToken());
            return syntaxErrorExpression;
        }
    }

    /*
    Need to add TypeExpression?
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
