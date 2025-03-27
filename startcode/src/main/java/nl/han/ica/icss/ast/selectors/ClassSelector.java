package nl.han.ica.icss.ast.selectors;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Selector;

@EqualsAndHashCode(callSuper = true)
public class ClassSelector extends Selector {
    public String cls;

    public ClassSelector(String cls) {
        this.cls = cls;
    }

    @Override
    public String getNodeLabel() {
        return "ClassSelector " + cls;
    }

    @Override
    public String toString() {
        return cls;
    }
}
