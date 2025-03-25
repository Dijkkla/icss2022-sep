package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

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
        variableTypes.addFirst(new HashMap<>());
        stylerule.body.forEach(child -> {
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
        //TODO: add
    }

    private void checkIfClause(IfClause ifClause) {
        //TODO: add
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        variableTypes.getFirst().put(
                variableAssignment.name.name,
                checkExpression(variableAssignment.expression));
    }

    private ExpressionType checkExpression(Expression expression) {
        if (expression instanceof Operation operation) {
            return checkOperation(operation);
        } else if (expression instanceof Literal literal) {
            return checkLiteral(literal);
        } else if (expression instanceof VariableReference variableReference) {
            return checkVariableReference(variableReference);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkVariableReference(VariableReference variableReference) {
        for (int scope = 0; scope < variableTypes.getSize(); scope++) {
            System.out.println(variableTypes.get(scope).get(variableReference.name));
            if (variableTypes.get(scope).get(variableReference.name) != null) {
                return variableTypes.get(scope).get(variableReference.name);
            }
        }
        variableReference.setError("Variable has not been initialized");
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkLiteral(Literal literal) {
        return literal instanceof BoolLiteral ? ExpressionType.BOOL
                : literal instanceof ColorLiteral ? ExpressionType.COLOR
                : literal instanceof PercentageLiteral ? ExpressionType.PERCENTAGE
                : literal instanceof PixelLiteral ? ExpressionType.PIXEL
                : literal instanceof ScalarLiteral ? ExpressionType.SCALAR
                : ExpressionType.UNDEFINED;
    }

    private ExpressionType checkOperation(Operation operation) {
        if (checkExpression(operation.lhs) == ExpressionType.COLOR || checkExpression(operation.rhs) == ExpressionType.COLOR
                || checkExpression(operation.lhs) == ExpressionType.BOOL || checkExpression(operation.rhs) == ExpressionType.BOOL) {
            operation.setError("Operations may not use bool literals or color literals ");
            return ExpressionType.UNDEFINED;
        }
        if (checkExpression(operation.lhs) == ExpressionType.UNDEFINED || checkExpression(operation.rhs) == ExpressionType.UNDEFINED) {
            return ExpressionType.UNDEFINED;
        }
        if (operation instanceof MultiplyOperation) {
            return checkMultiplyOperation(operation);
        } else if (operation instanceof AddOperation || operation instanceof SubtractOperation) {
            return checkAddOrSubtractOperation(operation);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkMultiplyOperation(Operation operation) {
        if (checkExpression(operation.lhs) != ExpressionType.SCALAR && checkExpression(operation.rhs) != ExpressionType.SCALAR) {
            operation.setError("Multiply operations must have at least 1 scalar literal");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs) != ExpressionType.SCALAR ? checkExpression(operation.lhs)
                : checkExpression(operation.rhs);
    }

    private ExpressionType checkAddOrSubtractOperation(Operation operation) {
        if (checkExpression(operation.lhs) == checkExpression(operation.rhs)) {
            operation.setError("Add and subtract operations must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression(operation.lhs);
    }


}
