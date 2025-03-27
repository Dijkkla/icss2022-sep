package nl.han.ica.icss.ast;

import nl.han.ica.icss.ast.types.ExpressionType;

public abstract class Literal extends Expression {
    @Override
    public ExpressionType setExpressionType(ExpressionType expressionType) {
        throw new IllegalAccessError("Not allowed to change the expressionType of Literal");
    }
}
