package nl.han.ica.icss.ast.literals;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public class PercentageLiteral extends Literal {
    public int value;

    public PercentageLiteral(int value) {
        this.value = value;
        expressionType = ExpressionType.PERCENTAGE;
    }

    public PercentageLiteral(String text) {
        this.value = Integer.parseInt(text.substring(0, text.length() - 1));
        expressionType = ExpressionType.PERCENTAGE;
    }

    @Override
    public String getNodeLabel() {
        return "Percentage literal (" + value + ")";
    }
}
