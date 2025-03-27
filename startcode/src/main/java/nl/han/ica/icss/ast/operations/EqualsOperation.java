package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

@EqualsAndHashCode(callSuper = true)
public class EqualsOperation extends Operation {

    public EqualsOperation() {
        super(OperationType.EQUALS);
    }
}
