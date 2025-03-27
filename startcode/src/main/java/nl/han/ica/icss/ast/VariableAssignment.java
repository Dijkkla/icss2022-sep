package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * An assignment binds an expression to an identifier.
 */
@EqualsAndHashCode(callSuper = true)
public class VariableAssignment extends ASTNode {
    public VariableReference name;
    public Expression expression;

    @Override
    public String getNodeLabel() {
        return "VariableAssignment (" + name.name + ")";
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (name == null) {
            name = (VariableReference) child;
        } else if (expression == null) {
            expression = (Expression) child;
        }

        return this;
    }

    @Override
    public List<ASTNode> getChildren() {

        List<ASTNode> children = new ArrayList<>();
        if (name != null)
            children.add(name);
        if (expression != null)
            children.add(expression);
        return children;
    }
}
