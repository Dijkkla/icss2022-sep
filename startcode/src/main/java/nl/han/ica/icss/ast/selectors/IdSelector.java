package nl.han.ica.icss.ast.selectors;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Selector;

@EqualsAndHashCode(callSuper = true)
public class IdSelector extends Selector {
    public String id;

    public IdSelector(String id) {
        this.id = id;
    }

    @Override
    public String getNodeLabel() {
        return "IdSelector " + id;
    }

    @Override
    public String toString() {
        return id;
    }
}
