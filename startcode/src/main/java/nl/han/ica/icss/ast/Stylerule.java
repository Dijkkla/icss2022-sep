package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class Stylerule extends ASTNode {
    public List<Selector> selectors = new ArrayList<>();
    public List<ASTNode> body = new ArrayList<>();

    public Stylerule() {
    }

    public Stylerule(Selector selector, List<ASTNode> body) {
        this.selectors = new ArrayList<>();
        this.selectors.add(selector);
        this.body = body;
    }

    @Override
    public String getNodeLabel() {
        return "Stylerule";
    }

    @Override
    public List<ASTNode> getChildren() {
        List<ASTNode> children = new ArrayList<>();
        children.addAll(selectors);
        children.addAll(body);

        return children;
    }

    @Override
    public ASTNode addChild(ASTNode child) {
        if (child instanceof Selector)
            selectors.add((Selector) child);
        else
            body.add(child);

        return this;
    }
}
