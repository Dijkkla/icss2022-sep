package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class VariableReference extends Expression {
    public String name;

    public VariableReference(String name) {
        super();
        this.name = name;
    }

    @Override
    public String getNodeLabel() {
        return "VariableReference (" + name + ")";
    }
}
