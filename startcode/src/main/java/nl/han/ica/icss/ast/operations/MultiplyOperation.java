package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

public class MultiplyOperation extends Operation {
    public OperationType operationType = OperationType.MULTIPLY;

    @Override
    public String getNodeLabel() {
        return "Multiply";
    }
}
