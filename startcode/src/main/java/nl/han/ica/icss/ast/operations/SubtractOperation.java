package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;

@EqualsAndHashCode(callSuper = true)
public class SubtractOperation extends Operation {

    @Override
    public String getNodeLabel() {
        return "Subtract";
    }
}
