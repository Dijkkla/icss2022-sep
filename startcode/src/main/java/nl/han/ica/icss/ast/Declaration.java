package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/*
 * A Declaration defines a style property. Declarations are things like "width: 100px"
 */
@EqualsAndHashCode(callSuper = true)
public class Declaration extends ASTNode {
    public PropertyName property;
    public Expression expression;

    public Declaration(String property) {
        super();
        this.property = new PropertyName(property);
    }

    @Override
    public String getNodeLabel() {
        return "Declaration";
    }

    @Override
    public List<ASTNode> getChildren() {

        List<ASTNode> children = new ArrayList<>();
        if (property != null)
            children.add(property);
        if (expression != null)
            children.add(expression);
        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (child instanceof PropertyName) {
            property = (PropertyName) child;
        } else if (child instanceof Expression) {
            expression = (Expression) child;
        }
        return this;
    }
}
