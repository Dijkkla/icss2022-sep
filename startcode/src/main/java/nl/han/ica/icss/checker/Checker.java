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
    private final Map<OperationType, List<ExpressionType>> operationTypes = Map.of(
            OperationType.ADD, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
            OperationType.SUBTRACT, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
            OperationType.MULTIPLY, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR),
            OperationType.DIVIDE, List.of(ExpressionType.PERCENTAGE, ExpressionType.PIXEL, ExpressionType.SCALAR)
    );
    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

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
        if (checkExpression(declaration.expression) != ExpressionType.UNDEFINED
                && !declarationTypes.get(declaration.property.name).contains(checkExpression(declaration.expression))) {
            declaration.setError("Property \"" + declaration.property.name + "\" may only use " + declarationTypes.get(declaration.property.name));
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
        if (operation.getChildren().removeIf(child ->
                checkExpression(((Expression) child)) != ExpressionType.UNDEFINED
                        && !operationTypes.get(operation.operationType).contains(((Expression) child).expressionType)
        )) {
            operation.setError(operation.operationType + " operation may only use " + operationTypes.get(operation.operationType));
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) == ExpressionType.UNDEFINED || checkExpression(operation.rhs) == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }
        return switch (operation.operationType) {
            case MULTIPLY -> checkMultiplyOrDivideOperation(operation);
            case ADD, SUBTRACT -> checkAddOrSubtractOperation(operation);
            default -> {
                operation.setError("Unknown operation");
                yield ExpressionType.UNDEFINED;
            }
        };
    }

    private ExpressionType checkMultiplyOrDivideOperation(Operation operation) {
        if (checkExpression(operation.lhs) != ExpressionType.SCALAR && checkExpression(operation.rhs) != ExpressionType.SCALAR) {
            operation.setError("Multiply and divide operations must have at least 1 SCALAR");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs) != ExpressionType.SCALAR ? checkExpression(operation.lhs)
                : checkExpression(operation.rhs);
    }

    private ExpressionType checkAddOrSubtractOperation(Operation operation) {
        if (checkExpression(operation.lhs) != checkExpression(operation.rhs)) {
            operation.setError("Add and subtract operations must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }


}
