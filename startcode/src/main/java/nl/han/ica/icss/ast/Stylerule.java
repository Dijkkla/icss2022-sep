package nl.han.ica.icss.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Stylerule stylerule = (Stylerule) o;
        return Objects.equals(selectors, stylerule.selectors) &&
                Objects.equals(body, stylerule.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectors, body);
    }
}
