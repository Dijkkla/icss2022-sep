package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * A stylesheet is the root node of the AST, it consists of one or more statements
 */
@EqualsAndHashCode(callSuper = true)
public class Stylesheet extends ASTNode {
    public List<ASTNode> body;

    public Stylesheet() {
        this.body = new ArrayList<>();
    }

    public Stylesheet(List<ASTNode> body) {
        this.body = body;
    }

    @Override
    public String getNodeLabel() {
        return "Stylesheet";
    }

    @Override
    public List<ASTNode> getChildren() {
        return this.body;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        body.add(child);
        return this;
    }

    @Override
    public ASTNode removeChild(ASTNode child) {
        body.remove(child);
        return this;
    }
}
