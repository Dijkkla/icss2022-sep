package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
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
        stylesheet.body.forEach(node -> {
            switch (node) {
                case VariableAssignment variableAssignment -> transformVariableAssignment(variableAssignment);
                case Stylerule stylerule -> transformStylerule(stylerule);
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        variableValues.clear();
    }

    private void transformStylerule(Stylerule stylerule) {
        transformBlock(stylerule.body);
    }

    private void transformBlock(ArrayList<ASTNode> nodes) {
        variableValues.addFirst(new HashMap<>());
        nodes.forEach(node -> {
            switch (node) {
                case Declaration declaration -> transformDeclaration(declaration);
                case VariableAssignment variableAssignment -> transformVariableAssignment(variableAssignment);
                case IfClause ifClause -> transformIfClause(ifClause);
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        variableValues.removeFirst();
    }

    private void transformDeclaration(Declaration declaration) {
        declaration.expression = transformExpression(declaration.expression);
    }

    private void transformIfClause(IfClause ifClause) {
        if (((BoolLiteral) ifClause.conditionalExpression).value) {
            transformBlock(ifClause.body);
        } else {
            transformElseClause(ifClause.elseClause);
        }
        //TODO: remove ifClause from AST
    }

    private void transformElseClause(ElseClause elseClause) {
        transformBlock(elseClause.body);
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment) {
        variableAssignment.expression = transformExpression(variableAssignment.expression);
        variableValues.getFirst().put(
                variableAssignment.name.name,
                (Literal) variableAssignment.expression);
        //TODO: remove variableAssignments from AST
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
        int returnValue = switch (operation) {
            case MultiplyOperation ignored -> evaluateMultiplyOperation(values[0], values[1]);
            case AddOperation ignored -> evaluateAddOperation(values[0], values[1]);
            case SubtractOperation ignored -> evaluateSubtractOperation(values[0], values[1]);
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
        return switch (operation.expressionType) {
            case PERCENTAGE -> new PercentageLiteral(returnValue);
            case PIXEL -> new PixelLiteral(returnValue);
            case SCALAR -> new ScalarLiteral(returnValue);
            default -> null;
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
