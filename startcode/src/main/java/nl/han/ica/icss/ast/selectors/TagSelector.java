package nl.han.ica.icss.ast.selectors;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Selector;

@EqualsAndHashCode(callSuper = true)
public class TagSelector extends Selector {
    public String tag;

    public TagSelector(String tag) {
        this.tag = tag;
    }

    @Override
    public String getNodeLabel() {
        return "TagSelector " + tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}
