package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

@EqualsAndHashCode(callSuper = true)
public class GreaterThanOperation extends Operation {

    public GreaterThanOperation() {
        super(OperationType.GREATER_THAN);
    }
}
