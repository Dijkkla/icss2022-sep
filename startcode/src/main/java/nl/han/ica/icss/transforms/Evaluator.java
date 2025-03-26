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
import java.util.List;

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
        stylesheet.body.removeIf(node -> (node instanceof VariableAssignment));
        variableValues.clear();
    }

    private void transformStylerule(Stylerule stylerule) {
        transformBlock(stylerule.body);
    }

    private ArrayList<ASTNode> transformBlock(ArrayList<ASTNode> nodes) {
        variableValues.addFirst(new HashMap<>());
        List<ASTNode> newNodes = new ArrayList<>();
        nodes.forEach(node -> {
            switch (node) {
                case Declaration declaration -> transformDeclaration(declaration);
                case VariableAssignment variableAssignment -> transformVariableAssignment(variableAssignment);
                case IfClause ifClause -> newNodes.addAll(transformIfClause(ifClause));
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        nodes.removeIf(node -> (node instanceof VariableAssignment || node instanceof IfClause));
        nodes.addAll(newNodes);
        variableValues.removeFirst();
        return nodes;
    }

    private void transformDeclaration(Declaration declaration) {
        declaration.expression = transformExpression(declaration.expression);
    }

    private ArrayList<ASTNode> transformIfClause(IfClause ifClause) {
        if (((BoolLiteral) transformExpression(ifClause.conditionalExpression)).value) {
            return transformBlock(ifClause.body);
        } else {
            return transformElseClause(ifClause.elseClause);
        }
    }

    private ArrayList<ASTNode> transformElseClause(ElseClause elseClause) {
        return transformBlock(elseClause.body);
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment) {
        variableValues.getFirst().put(
                variableAssignment.name.name,
                transformExpression(variableAssignment.expression));
    }

    private Literal transformExpression(Expression expression) {
        return switch (expression) {
            case Operation operation -> transformOperation(operation);
            case Literal literal -> transformLiteral(literal);
            case VariableReference variableReference -> transformVariableReference(variableReference);
            default -> throw new IllegalStateException("Unexpected value: " + expression);
        };
    }

    private Literal transformVariableReference(VariableReference variableReference) {
        for (int scope = 0; scope < variableValues.getSize(); scope++) {
            if (variableValues.get(scope).get(variableReference.name) != null) {
                return variableValues.get(scope).get(variableReference.name);
            }
        }
        throw new RuntimeException("Could not find variable \"" + variableReference.name + "\"");
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
                    default -> throw new IllegalStateException("Unexpected value: " + literal.expressionType);
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
            default -> throw new IllegalStateException("Unexpected value: " + operation.expressionType);
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
