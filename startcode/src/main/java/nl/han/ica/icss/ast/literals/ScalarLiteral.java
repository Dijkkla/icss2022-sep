package nl.han.ica.icss.ast.literals;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public class ScalarLiteral extends Literal {
    public int value;

    public ScalarLiteral(int value) {
        this.value = value;
        expressionType = ExpressionType.SCALAR;
    }

    public ScalarLiteral(String text) {
        this.value = Integer.parseInt(text);
        expressionType = ExpressionType.SCALAR;
    }

    @Override
    public String getNodeLabel() {
        return "Scalar literal (" + value + ")";
    }
}
