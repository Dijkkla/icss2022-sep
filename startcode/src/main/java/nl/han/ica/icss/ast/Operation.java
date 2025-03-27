package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.types.OperationType;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public abstract class Operation extends Expression {
    public final OperationType operationType;
    public Expression lhs;
    public Expression rhs;

    protected Operation(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        if (lhs != null) children.add(lhs);
        if (rhs != null) children.add(rhs);
        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (lhs == null) {
            lhs = (Expression) child;
        } else if (rhs == null) {
            rhs = (Expression) child;
        }
        return this;
    }

    @Override
    public String getNodeLabel() {
        return operationType.toString();
    }
}
