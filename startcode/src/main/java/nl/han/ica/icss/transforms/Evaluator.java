package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.types.OperationType;

import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

//    public Evaluator() {
//        variableValues = new HANLinkedList<>();
//    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();

        transformStylesheet(ast.root);
    }

    private void transformStylesheet(Stylesheet stylesheet) {
        variableValues.addFirst(new HashMap<>());
        stylesheet.body.forEach(child -> {
            switch (child) {
                case VariableAssignment variableAssignment -> transformVariableAssignment(variableAssignment);
                case Stylerule stylerule -> transformStylerule(stylerule);
                case null, default -> {
                }
            }
        });
        variableValues.clear();
    }

    private void transformStylerule(Stylerule stylerule) {
        //TODO: implement
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment) {
        variableAssignment.expression = transformExpression(variableAssignment.expression);
        variableValues.getFirst().put(
                variableAssignment.name.name,
                (Literal) variableAssignment.expression);
    }

    private Literal transformExpression(Expression expression) {
        return switch (expression) {
            case Operation operation -> transformOperation(operation);
            case Literal literal -> transformLiteral(literal);
            case VariableReference variableReference -> transformVariableReference(variableReference);
            case null, default -> null;
        };
    }

    private Literal transformVariableReference(VariableReference variableReference) {
        for (int scope = 0; scope < variableValues.getSize(); scope++) {
            if (variableValues.get(scope).get(variableReference.name) != null) {
                return variableValues.get(scope).get(variableReference.name);
            }
        }
        return null;
    }

    private Literal transformLiteral(Literal literal) {
        return literal;
    }

    private Literal transformOperation(Operation operation) {
        int[] values = operation.getChildren().stream()
                .map(expression -> transformExpression((Expression) expression))
                .mapToInt(literal -> switch (literal.expressionType) {
                    case PERCENTAGE -> ((PercentageLiteral) literal).value;
                    case PIXEL -> ((PixelLiteral) literal).value;
                    case SCALAR -> ((ScalarLiteral) literal).value;
                    default -> 0;
                })
                .toArray();
        int returnValue = evaluateOperation(operation.operationType, values[0], values[1]);
        return switch (operation.expressionType) {
            case PERCENTAGE -> new PercentageLiteral(returnValue);
            case PIXEL -> new PixelLiteral(returnValue);
            case SCALAR -> new ScalarLiteral(returnValue);
            default -> null;
        };
    }

    private int evaluateOperation(OperationType operationType, int lhs, int rhs) {
        return switch (operationType) {
            case MULTIPLY -> evaluateMultiplyOperation(lhs, rhs);
            case ADD -> evaluateAddOperation(lhs, rhs);
            case SUBTRACT -> evaluateSubtractOperation(lhs, rhs);
        };
    }

    private int evaluateMultiplyOperation(int lhs, int rhs) {
        return lhs * rhs;
    }

    private int evaluateAddOperation(int lhs, int rhs) {
        return lhs + rhs;
    }

    private int evaluateSubtractOperation(int lhs, int rhs) {
        return lhs - rhs;
    }


}
