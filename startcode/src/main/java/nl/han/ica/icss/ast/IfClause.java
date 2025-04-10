package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class IfClause extends ASTNode {
    public Expression conditionalExpression;
    public List<ASTNode> body = new ArrayList<>();
    public ElseClause elseClause;

    public IfClause() {
    }

    public IfClause(Expression conditionalExpression, List<ASTNode> body) {
        this.conditionalExpression = conditionalExpression;
        this.body = body;
    }

    public IfClause(Expression conditionalExpression, List<ASTNode> body, ElseClause elseClause) {
        this.conditionalExpression = conditionalExpression;
        this.body = body;
        this.elseClause = elseClause;
    }

    @Override
    public String getNodeLabel() {
        return "If_Clause";
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        children.add(conditionalExpression);
        children.addAll(body);
        if (elseClause != null) children.add(elseClause);

        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (child instanceof Expression) conditionalExpression = (Expression) child;
        else if (child instanceof ElseClause) elseClause = (ElseClause) child;
        else body.add(child);

        return this;
    }
}
