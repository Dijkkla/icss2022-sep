package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.ast.types.OperationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    private final Map<String, List<ExpressionType>> declarationTypes = Map.of(
            "color", List.of(ExpressionType.COLOR),
            "background-color", List.of(ExpressionType.COLOR),
            "width", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE),
            "height", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE)
    );

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();

        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet stylesheet) {
        variableTypes.addFirst(new HashMap<>());
        stylesheet.body.forEach(child -> {
            if (child instanceof VariableAssignment variableAssignment) {
                checkVariableAssignment(variableAssignment);
            } else if (child instanceof Stylerule stylerule) {
                checkStylerule(stylerule);
            }
        });
        variableTypes.clear();
    }

    private void checkStylerule(Stylerule stylerule) {
        checkBlock(stylerule.body);
    }

    private void checkBlock(List<ASTNode> nodes) {
        variableTypes.addFirst(new HashMap<>());
        nodes.forEach(child -> {
            if (child instanceof Declaration declaration) {
                checkDeclaration(declaration);
            } else if (child instanceof VariableAssignment variableAssignment) {
                checkVariableAssignment(variableAssignment);
            } else if (child instanceof IfClause ifClause) {
                checkIfClause(ifClause);
            }
        });
        variableTypes.removeFirst();
    }

    private void checkDeclaration(Declaration declaration) {
        if (checkExpression(declaration.expression) != ExpressionType.UNDEFINED
                && !declarationTypes.get(declaration.property.name).contains(checkExpression(declaration.expression))) {
            declaration.setError("Property \"" + declaration.property.name + "\" may not have a value of type: " + checkExpression(declaration.expression));
        }
    }

    private void checkIfClause(IfClause ifClause) {
        if (checkExpression(ifClause.conditionalExpression) != ExpressionType.BOOL) {
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
        expression.setExpressionType(switch (expression) {
            case Operation operation -> checkOperation(operation);
            case Literal literal -> checkLiteral(literal);
            case VariableReference variableReference -> checkVariableReference(variableReference);
            default -> ExpressionType.UNDEFINED;
        });
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

    private ExpressionType checkLiteral(Literal literal) {
        return literal.expressionType;
    }

    private ExpressionType checkOperation(Operation operation) {
        if (checkExpression(operation.lhs) == ExpressionType.COLOR || checkExpression(operation.rhs) == ExpressionType.COLOR
                || checkExpression(operation.lhs) == ExpressionType.BOOL || checkExpression(operation.rhs) == ExpressionType.BOOL) {
            operation.setError("Operations may not use BOOL or COLOR");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) == ExpressionType.UNDEFINED || checkExpression(operation.rhs) == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }
        return switch (operation.operationType) {
            case MULTIPLY -> checkMultiplyOperation(operation);
            case ADD, SUBTRACT -> checkAddOrSubtractOperation(operation);
            default -> ExpressionType.UNDEFINED;
        };
    }

    private ExpressionType checkMultiplyOperation(Operation operation) {
        if (checkExpression(operation.lhs) != ExpressionType.SCALAR && checkExpression(operation.rhs) != ExpressionType.SCALAR) {
            operation.setError("Multiply operations must have at least 1 SCALAR");
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
