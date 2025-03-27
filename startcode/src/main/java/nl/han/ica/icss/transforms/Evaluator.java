package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator implements Transform {

    private final IHANLinkedList<HashMap<String, Literal>> variableValues = new HANLinkedList<>();

    @Override
    public void apply(AST ast) {
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

    private List<ASTNode> transformBlock(List<ASTNode> nodes) {
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

    private List<ASTNode> transformIfClause(IfClause ifClause) {
        if (((BoolLiteral) transformExpression(ifClause.conditionalExpression)).value) {
            return transformBlock(ifClause.body);
        } else if (ifClause.elseClause != null) {
            return transformElseClause(ifClause.elseClause);
        }
        return List.of();
    }

    private List<ASTNode> transformElseClause(ElseClause elseClause) {
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
        int returnValue = evaluateOperation(operation);
        return switch (operation.expressionType) {
            case PERCENTAGE -> new PercentageLiteral(returnValue);
            case PIXEL -> new PixelLiteral(returnValue);
            case SCALAR -> new ScalarLiteral(returnValue);
            default -> throw new IllegalStateException("Unexpected value: " + operation.expressionType);
        };
    }

    private int evaluateOperation(Operation operation) {
        int[] values = operation.getChildren().stream()
                .mapToInt(expression -> evaluateExpression((Expression) expression))
                .toArray();
        return switch (operation.operationType) {
            case POWER -> evaluatePowerOperation(values[0], values[1]);
            case MULTIPLY -> evaluateMultiplyOperation(values[0], values[1]);
            case DIVIDE -> evaluateDivideOperation(values[0], values[1]);
            case ADD -> evaluateAddOperation(values[0], values[1]);
            case SUBTRACT -> evaluateSubtractOperation(values[0], values[1]);
            default -> throw new IllegalStateException("Unexpected value: " + operation.operationType);
        };
    }

    private int evaluateExpression(Expression expression) {
        if (expression instanceof Operation operation) {
            return evaluateOperation(operation);
        } else {
            Literal literal = transformExpression(expression);
            return switch (literal.expressionType) {
                case PERCENTAGE -> ((PercentageLiteral) literal).value;
                case PIXEL -> ((PixelLiteral) literal).value;
                case SCALAR -> ((ScalarLiteral) literal).value;
                default -> throw new IllegalStateException("Unexpected value: " + literal.expressionType);
            };
        }
    }

    private int evaluatePowerOperation(int lhs, int rhs) {
        return (int) Math.pow(lhs, rhs);
    }

    private int evaluateMultiplyOperation(int lhs, int rhs) {
        return lhs * rhs;
    }

    private int evaluateDivideOperation(int lhs, int rhs) {
        return lhs / rhs;
    }

    private int evaluateAddOperation(int lhs, int rhs) {
        return lhs + rhs;
    }

    private int evaluateSubtractOperation(int lhs, int rhs) {
        return lhs - rhs;
    }


}
