package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

public class AddOperation extends Operation {
    public OperationType operationType = OperationType.ADD;

    @Override
    public String getNodeLabel() {
        return "Add";
    }
}
