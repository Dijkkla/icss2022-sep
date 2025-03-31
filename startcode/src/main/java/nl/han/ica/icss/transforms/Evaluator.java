package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.io.Serializable;
import java.util.*;

public class Evaluator implements Transform {

    private final IHANLinkedList<Map<String, Literal>> variableValues = new HANLinkedList<>();

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
        stylesheet.body.removeIf(node -> !(node instanceof Stylerule));
        variableValues.clear();
    }

    private void transformStylerule(Stylerule stylerule) {
        stylerule.body = transformBlock(stylerule.body);
    }

    private List<ASTNode> transformBlock(List<ASTNode> nodes) {
        variableValues.addFirst(new HashMap<>());
        List<ASTNode> newNodes = new ArrayList<>();
        nodes.forEach(node -> {
            switch (node) {
                case Declaration declaration -> newNodes.add(transformDeclaration(declaration));
                case VariableAssignment variableAssignment -> transformVariableAssignment(variableAssignment);
                case IfClause ifClause -> newNodes.addAll(transformIfClause(ifClause));
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        variableValues.removeFirst();
        return newNodes;
    }

    private ASTNode transformDeclaration(Declaration declaration) {
        declaration.expression = transformExpression(declaration.expression);
        return declaration;
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
        boolean noPreexistingVariable = true;
        Literal newVariableValue;
        if (variableAssignment.expression instanceof VariableAssignment nestedVariableAssignment) {
            transformVariableAssignment(nestedVariableAssignment);
            newVariableValue = transformVariableReference(nestedVariableAssignment.name);
        } else {
            newVariableValue = transformExpression((Expression) variableAssignment.expression);
        }
        for (int scope = 0; scope < variableValues.getSize(); scope++) {
            Literal variableValue = variableValues.get(scope).get(variableAssignment.name.name);
            if (variableValue != null) {
                variableValues.get(scope).put(
                        variableAssignment.name.name,
                        newVariableValue);
                noPreexistingVariable = false;
            }
        }
        if (noPreexistingVariable) {
            variableValues.getFirst().put(
                    variableAssignment.name.name,
                    newVariableValue);
        }
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
            Literal variableValue = variableValues.get(scope).get(variableReference.name);
            if (variableValue != null) {
                return variableValue;
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
            case ADD -> transformAddOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case SUBTRACT ->
                    transformSubtractOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case MULTIPLY ->
                    transformMultiplyOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case DIVIDE -> transformDivideOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case POWER -> transformPowerOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case FACTORIAL -> transformFactorialOperation((int) operationArguments.getFirst());
            case NOT -> transformNotOperation(((boolean) operationArguments.getFirst()));
            case EQUALS ->
                    transformEqualsOperation(operation.lhs.expressionType, operation.rhs.expressionType, operationArguments.get(0), operationArguments.get(1));
            case GREATER_THAN ->
                    transformGreaterThanOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            case AND -> transformAndOperation((boolean) operationArguments.get(0), (boolean) operationArguments.get(1));
            case OR -> transformOrOperation((boolean) operationArguments.get(0), (boolean) operationArguments.get(1));
            case REMAINDER -> transformRestOperation((int) operationArguments.get(0), (int) operationArguments.get(1));
            default -> throw new IllegalStateException("Unexpected value: " + operation.operationType);
        };
        return switch (operation.expressionType) {
            case BOOL -> new BoolLiteral((boolean) result);
            case COLOR -> new ColorLiteral((String) result);
            case PERCENTAGE -> new PercentageLiteral((int) result);
            case PIXEL -> new PixelLiteral((int) result);
            case SCALAR -> new ScalarLiteral((int) result);
            default -> throw new IllegalStateException("Unexpected value: " + operation.expressionType);
        };
    }

    private int transformRestOperation(int lhs, int rhs) {
        return lhs % rhs;
    }

    private boolean transformOrOperation(boolean lhs, boolean rhs) {
        return lhs || rhs;
    }

    private boolean transformAndOperation(boolean lhs, boolean rhs) {
        return lhs && rhs;
    }

    private boolean transformGreaterThanOperation(int lhs, int rhs) {
        return lhs > rhs;
    }

    private boolean transformEqualsOperation(ExpressionType lhsType, ExpressionType rhsType, Serializable lhs, Serializable rhs) {
        return lhsType == rhsType && Objects.equals(lhs, rhs);
    }

    private boolean transformNotOperation(boolean arg) {
        return !arg;
    }

    private int transformFactorialOperation(int arg) {
        int x = 1;
        for (int i = 2; i <= arg; i++) {
            x *= i;
        }
        return x;
    }

    private int transformPowerOperation(int lhs, int rhs) {
        return (int) Math.pow(lhs, rhs);
    }

    private int transformDivideOperation(int lhs, int rhs) {
        return lhs / rhs;
    }

    private int transformMultiplyOperation(int lhs, int rhs) {
        return lhs * rhs;
    }

    private int transformSubtractOperation(int lhs, int rhs) {
        return lhs - rhs;
    }

    private int transformAddOperation(int lhs, int rhs) {
        return lhs + rhs;
    }
}
