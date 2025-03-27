package nl.han.ica.icss.ast.literals;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.types.ExpressionType;

@EqualsAndHashCode(callSuper = true)
public class PixelLiteral extends Literal {
    public int value;

    public PixelLiteral(int value) {
        this.value = value;
        expressionType = ExpressionType.PIXEL;
    }

    public PixelLiteral(String text) {
        this.value = Integer.parseInt(text.substring(0, text.length() - 2));
        expressionType = ExpressionType.PIXEL;
    }

    @Override
    public String getNodeLabel() {
        return "Pixel literal (" + value + ")";
    }
}
