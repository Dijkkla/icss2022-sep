package nl.han.ica.icss.ast.operations;

import lombok.EqualsAndHashCode;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

@EqualsAndHashCode(callSuper = true)
public class AddOperation extends Operation {

    public AddOperation() {
        super(OperationType.ADD);
    }

    @Override
    public String getNodeLabel() {
        return "Add";
    }
}
