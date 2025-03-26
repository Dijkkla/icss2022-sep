package nl.han.ica.icss.ast;

import nl.han.ica.icss.ast.types.ExpressionType;

public abstract class Expression extends ASTNode {
    public ExpressionType expressionType;

    public ExpressionType setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
        return this.expressionType;
    }
}
