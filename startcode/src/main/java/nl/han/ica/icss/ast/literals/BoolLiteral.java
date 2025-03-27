package nl.han.ica.icss.ast.literals;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public class BoolLiteral extends Literal {
    public boolean value;

    public BoolLiteral(boolean value) {
        this.value = value;
        expressionType = ExpressionType.BOOL;
    }

    public BoolLiteral(String text) {
        this.value = text.equals("TRUE");
        expressionType = ExpressionType.BOOL;
    }

    @Override
    public String getNodeLabel() {
        String textValue = value ? "TRUE" : "FALSE";
        return "Bool Literal (" + textValue + ")";
    }
}
