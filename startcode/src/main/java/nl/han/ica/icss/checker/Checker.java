package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.ast.types.OperationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checker {

    private final Map<String, List<ExpressionType>> declarationTypes = Map.of(
            "color", List.of(ExpressionType.COLOR),
            "background-color", List.of(ExpressionType.COLOR),
            "width", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE),
            "height", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE)
    );

    private final Map<OperationType, List<ExpressionType>> operationTypes;

    private IHANLinkedList<Map<String, ExpressionType>> variableTypes;

    public Checker() {
        operationTypes = new HashMap<>();
        Object[] operationTypesMapBuilder = {
                OperationType.ADD, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.SUBTRACT, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.MULTIPLY, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.DIVIDE, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.REST, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.POWER, List.of(ExpressionType.SCALAR),
                OperationType.FACTORIAL, List.of(ExpressionType.SCALAR),
                OperationType.NOT, List.of(ExpressionType.BOOL),
                OperationType.EQUALS, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR, ExpressionType.COLOR, ExpressionType.BOOL),
                OperationType.GREATER_THAN, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
                OperationType.AND, List.of(ExpressionType.BOOL),
                OperationType.OR, List.of(ExpressionType.BOOL)
        };
        for (int i = 0; i < operationTypesMapBuilder.length; i += 2) {
            operationTypes.put((OperationType) operationTypesMapBuilder[i], (List<ExpressionType>) operationTypesMapBuilder[i + 1]);
        }
    }

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());
        stylesheet.body.forEach(node -> {
            switch (node) {
                case VariableAssignment variableAssignment -> checkVariableAssignment(variableAssignment);
                case Stylerule stylerule -> checkStylerule(stylerule);
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        variableTypes.clear();
    }

    private void checkStylerule(Stylerule stylerule) {
        checkBlock(stylerule.body);
    }

    private void checkBlock(List<ASTNode> nodes) {
        variableTypes.addFirst(new HashMap<>());
        nodes.forEach(node -> {
            switch (node) {
                case Declaration declaration -> checkDeclaration(declaration);
                case VariableAssignment variableAssignment -> checkVariableAssignment(variableAssignment);
                case IfClause ifClause -> checkIfClause(ifClause);
                default -> throw new IllegalStateException("Unexpected value: " + node);
            }
        });
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration declaration) {
        List<ExpressionType> expressionTypes = declarationTypes.get(declaration.property.name);
        if (expressionTypes == null) {
            declaration.setError("Property \"" + declaration.property.name + "\" is not defined");
        } else if (checkExpression(declaration.expression) != ExpressionType.UNDEFINED
                && !expressionTypes.contains(checkExpression(declaration.expression))) {
            declaration.setError("Property \"" + declaration.property.name + "\" may only use " + expressionTypes);
        }
    }

    private void checkIfClause(IfClause ifClause) {
        if (checkExpression(ifClause.conditionalExpression) != ExpressionType.UNDEFINED
                && checkExpression(ifClause.conditionalExpression) != ExpressionType.BOOL) {
            ifClause.setError("If clause conditional expression must be BOOL");
        }
        checkBlock(ifClause.body);
        if (ifClause.elseClause != null) {
            checkElseClause(ifClause.elseClause);
        }
    }

    private void checkElseClause(ElseClause elseClause) {
        checkBlock(elseClause.body);
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        variableTypes.getFirst().put(
                variableAssignment.name.name,
                checkExpression(variableAssignment.expression));
    }

    private ExpressionType checkExpression(Expression expression) {
        switch (expression) {
            case Operation operation -> expression.setExpressionType(checkOperation(operation));
            case VariableReference variableReference ->
                    expression.setExpressionType(checkVariableReference(variableReference));
            default -> {
            }
        }
        return expression.expressionType;
    }

    private ExpressionType checkVariableReference(VariableReference variableReference) {
        for (int scope = 0; scope < variableTypes.getSize(); scope++) {
            if (variableTypes.get(scope).get(variableReference.name) != null) {
                return variableTypes.get(scope).get(variableReference.name);
            }
        }
        variableReference.setError("Variable \"" + variableReference.name + "\" has not been initialized");
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkOperation(Operation operation) {
        if (operation.lhs == null) {
            operation.setError(operation.operationType + " has no arguments");
        }
        List<ExpressionType> expressionTypes = operationTypes.get(operation.operationType);
        if (operation.getChildren().removeIf(child ->
                checkExpression(((Expression) child)) != ExpressionType.UNDEFINED
                        && !expressionTypes.contains(((Expression) child).expressionType)
        )) {
            operation.setError(operation.operationType + " operation may only use " + expressionTypes);
            return ExpressionType.UNDEFINED;
        }
//        if (checkExpression(operation.lhs) == ExpressionType.UNDEFINED || (operation.rhs != null && checkExpression(operation.rhs) == ExpressionType.UNDEFINED)) {
//            return ExpressionType.UNDEFINED;
//        }
        return switch (operation.operationType) {
            case ADD -> checkAddOperation(operation);
            case SUBTRACT -> checkSubtractOperation(operation);
            case MULTIPLY -> checkMultiplyOperation(operation);
            case DIVIDE -> checkDivideOperation(operation);
            case POWER -> checkPowerOperation(operation);
            case FACTORIAL -> checkFactorialOperation(operation);
            case NOT -> checkNotOperation(operation);
            case EQUALS -> checkEqualsOperation(operation);
            case GREATER_THAN -> checkGreaterThanOperation(operation);
            case AND -> checkAndOperation(operation);
            case OR -> checkOrOperation(operation);
            case REST -> checkRestOperation(operation);
            default -> {
                operation.setError("Unknown operation");
                yield ExpressionType.UNDEFINED;
            }
        };
    }

    private ExpressionType checkRestOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) != checkExpression(operation.rhs)) {
            operation.setError(operation.operationType + " operation must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }

    private ExpressionType checkOrOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.BOOL;
    }

    private ExpressionType checkAndOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.BOOL;
    }

    private ExpressionType checkGreaterThanOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) != checkExpression(operation.rhs)) {
            operation.setError(operation.operationType + " operation must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.BOOL;
    }

    private ExpressionType checkEqualsOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.BOOL;
    }

    private ExpressionType checkNotOperation(Operation operation) {
        return ExpressionType.BOOL;
    }

    private ExpressionType checkFactorialOperation(Operation operation) {
        return ExpressionType.SCALAR;
    }

    private ExpressionType checkPowerOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        return ExpressionType.SCALAR;
    }

    private ExpressionType checkMultiplyOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) != ExpressionType.SCALAR && checkExpression(operation.rhs) != ExpressionType.SCALAR) {
            operation.setError(operation.operationType + " operation must have at least 1 SCALAR");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs) != ExpressionType.SCALAR ? checkExpression(operation.lhs)
                : checkExpression(operation.rhs);
    }

    private ExpressionType checkDivideOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.rhs) != ExpressionType.SCALAR) {
            operation.setError(operation.operationType + " operation must have SCALAR as right hand side expression");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }

    private ExpressionType checkAddOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) != checkExpression(operation.rhs)) {
            operation.setError(operation.operationType + " operation must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }

    private ExpressionType checkSubtractOperation(Operation operation) {
        if (operation.rhs == null) {
            operation.setError(operation.operationType + " must have 2 arguments");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) != checkExpression(operation.rhs)) {
            operation.setError(operation.operationType + " operation must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }


}
