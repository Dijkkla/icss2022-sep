package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;

import java.io.Serializable;
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
        List<? extends Serializable> operationArguments = operation.getChildren().stream()
                .map(child -> transformExpression((Expression) child))
                .map(literal -> switch (literal.expressionType) {
                    case BOOL -> ((BoolLiteral) literal).value;
                    case COLOR -> ((ColorLiteral) literal).value;
                    case PERCENTAGE -> ((PercentageLiteral) literal).value;
                    case PIXEL -> ((PixelLiteral) literal).value;
                    case SCALAR -> ((ScalarLiteral) literal).value;
                    default -> throw new IllegalStateException("Unexpected value: " + literal.expressionType);
                })
                .toList();
        Object result = switch (operation.operationType) {
            case ADD -> transformAddOperation(operationArguments);
            case SUBTRACT -> transformSubtractOperation(operationArguments);
            case MULTIPLY -> transformMultiplyOperation(operationArguments);
            case DIVIDE -> transformDivideOperation(operationArguments);
            case POWER -> transformPowerOperation(operationArguments);
            case FACTORIAL -> transformFactorialOperation(operationArguments);
            case NOT -> transformNotOperation(operationArguments);
            case EQUALS -> transformEqualsOperation(operationArguments);
            case GREATER_THAN -> transformGreaterThanOperation(operationArguments);
            case AND -> transformAndOperation(operationArguments);
            case OR -> transformOrOperation(operationArguments);
            default -> throw new IllegalStateException("Unexpected value: " + operation.operationType);
        };
        return switch (operation.expressionType) {
            case BOOL -> new BoolLiteral((Boolean) result);
            case COLOR -> new ColorLiteral((String) result);
            case PERCENTAGE -> new PercentageLiteral((Integer) result);
            case PIXEL -> new PixelLiteral((Integer) result);
            case SCALAR -> new ScalarLiteral((Integer) result);
            default -> throw new IllegalStateException("Unexpected value: " + operation.expressionType);
        };
    }

    private boolean transformOrOperation(List<? extends Serializable> operationArguments) {
        return (boolean) operationArguments.get(0) || (boolean) operationArguments.get(1);
    }

    private boolean transformAndOperation(List<? extends Serializable> operationArguments) {
        return (boolean) operationArguments.get(0) && (boolean) operationArguments.get(1);
    }

    private boolean transformGreaterThanOperation(List<? extends Serializable> operationArguments) {
        return (int) operationArguments.get(0) > (int) operationArguments.get(1);
    }

    private boolean transformEqualsOperation(List<? extends Serializable> operationArguments) {
        if (operationArguments.stream().map(Object::getClass).distinct().count() > 1) return false;
        return !(operationArguments.stream().distinct().count() > 1);
    }

    private boolean transformNotOperation(List<? extends Serializable> operationArguments) {
        return !((boolean) operationArguments.getFirst());
    }

    private int transformFactorialOperation(List<? extends Serializable> operationArguments) {
        int start = 1;
        for (int i = 2; i > (int) operationArguments.getFirst(); i++) {
            start *= i;
        }
        return start;
    }

    private int transformPowerOperation(List<? extends Serializable> operationArguments) {
        return (int) Math.pow((int) operationArguments.get(0), (int) operationArguments.get(1));
    }

    private int transformDivideOperation(List<? extends Serializable> operationArguments) {
        return (int) operationArguments.get(0) / (int) operationArguments.get(1);
    }

    private int transformMultiplyOperation(List<? extends Serializable> operationArguments) {
        return (int) operationArguments.get(0) * (int) operationArguments.get(1);
    }

    private int transformSubtractOperation(List<? extends Serializable> operationArguments) {
        return (int) operationArguments.get(0) - (int) operationArguments.get(1);
    }

    private int transformAddOperation(List<? extends Serializable> operationArguments) {
        return (int) operationArguments.get(0) + (int) operationArguments.get(1);
    }
}
