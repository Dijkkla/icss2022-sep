package nl.han.ica.icss.ast.operations;

import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.types.OperationType;

public class SubtractOperation extends Operation {
    public OperationType operationType = OperationType.SUBTRACT;

    @Override
    public String getNodeLabel() {
        return "Subtract";
    }
}
