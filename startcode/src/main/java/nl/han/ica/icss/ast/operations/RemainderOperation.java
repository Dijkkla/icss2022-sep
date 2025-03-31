package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

@EqualsAndHashCode(callSuper = true)
public class RemainderOperation extends Operation {

    public RemainderOperation() {
        super(OperationType.REMAINDER);
    }
}
