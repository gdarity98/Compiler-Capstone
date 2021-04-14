package edu.montana.csci.csci468.parser.statements;

import edu.montana.csci.csci468.bytecode.ByteCodeGenerator;
import edu.montana.csci.csci468.eval.CatscriptRuntime;
import edu.montana.csci.csci468.parser.CatscriptType;
import edu.montana.csci.csci468.parser.ErrorType;
import edu.montana.csci.csci468.parser.ParseError;
import edu.montana.csci.csci468.parser.SymbolTable;
import edu.montana.csci.csci468.parser.expressions.Expression;

public class VariableStatement extends Statement {
    private Expression expression;
    private String variableName;
    private CatscriptType explicitType;
    private CatscriptType type;

    public Expression getExpression() {
        return expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public void setExpression(Expression parseExpression) {
        this.expression = addChild(parseExpression);
    }

    public void setExplicitType(CatscriptType type) {
        this.explicitType = type;
    }

    public CatscriptType getExplicitType() {
        return explicitType;
    }

    public boolean isGlobal() {
        return getParent() instanceof CatScriptProgram;
    }

    @Override
    public void validate(SymbolTable symbolTable) {
        expression.validate(symbolTable);
        if (symbolTable.hasSymbol(variableName)) {
            addError(ErrorType.DUPLICATE_NAME);
        } else {
            // Done
            if(explicitType != null){
                CatscriptType type = expression.getType();
                boolean x = expression.getType().isAssignableFrom(explicitType);
                if(!explicitType.isAssignableFrom(expression.getType())){
                    addError(ErrorType.INCOMPATIBLE_TYPES);
                }else{
                    type = explicitType;
                    symbolTable.registerSymbol(variableName, type);
                }
            }else{
                type = expression.getType();
                symbolTable.registerSymbol(variableName, type);
            }
        }
    }

    public CatscriptType getType() {
        return type;
    }

    //==============================================================
    // Implementation
    //==============================================================
    @Override
    public void execute(CatscriptRuntime runtime) {
        Object exp = expression.evaluate();
        String s = String.valueOf(expression.evaluate(runtime));

        if(runtime.getValue(String.valueOf(exp)) != null && String.valueOf(expression.evaluate(runtime)) != null){
            exp = runtime.getValue(String.valueOf(exp));
            runtime.setValue(variableName, exp);
        }else{
            runtime.setValue(variableName, expression.evaluate(runtime));
        }
    }

    @Override
    public void transpile(StringBuilder javascript) {
        super.transpile(javascript);
    }

    @Override
    public void compile(ByteCodeGenerator code) {
        if(isGlobal()){
            //store in a field
        }else{
            //store in a slot
        }
    }
}
