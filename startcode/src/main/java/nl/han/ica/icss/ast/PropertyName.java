package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PropertyName extends ASTNode {
    public String name;

    public PropertyName() {
        super();
        name = "undefined";
    }

    public PropertyName(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getNodeLabel() {
        return "Property: (" + name + ")";
    }
}
