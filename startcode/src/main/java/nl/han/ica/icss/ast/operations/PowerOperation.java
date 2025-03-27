package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

@EqualsAndHashCode(callSuper = true)
public class PowerOperation extends Operation {

    public PowerOperation() {
        super(OperationType.POWER);
    }
}
