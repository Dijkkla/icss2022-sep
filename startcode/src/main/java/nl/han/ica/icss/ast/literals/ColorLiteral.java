package nl.han.ica.icss.ast.literals;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public class ColorLiteral extends Literal {
    public String value;

    public ColorLiteral(String value) {
        this.value = value;
        expressionType = ExpressionType.COLOR;
    }

    @Override
    public String getNodeLabel() {
        return "Color literal (" + value + ")";
    }
}
