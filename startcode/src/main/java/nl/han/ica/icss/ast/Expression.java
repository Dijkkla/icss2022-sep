package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import nl.han.ica.icss.ast.types.ExpressionType;

@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class Expression extends ASTNode {
    public ExpressionType expressionType = ExpressionType.UNDEFINED;
}
