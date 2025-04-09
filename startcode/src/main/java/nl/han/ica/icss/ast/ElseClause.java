package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class ElseClause extends ASTNode {

    public List<ASTNode> body = new ArrayList<>();

    public ElseClause() {
    }

    public ElseClause(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public String getNodeLabel() {
        return "Else_Clause";
    }

    @Override
    public List<ASTNode> getChildren() {
        return new ArrayList<>(body);
    }

    @Override
    public ASTNode addChild(ASTNode child) {

        body.add(child);

        return this;
    }
}
