package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;

@EqualsAndHashCode(callSuper = true)
public class DivideOperation extends Operation {

    @Override
    public String getNodeLabel() {
        return "Divide";
    }
}
