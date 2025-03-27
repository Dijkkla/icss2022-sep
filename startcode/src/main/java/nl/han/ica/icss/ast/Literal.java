package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public abstract class Literal extends Expression {
    @Override
    public void setExpressionType(ExpressionType expressionType) {
        throw new IllegalAccessError("Not allowed to change the expressionType of Literal");
    }
}
