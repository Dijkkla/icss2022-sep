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
        stylesheet.getChildren().forEach(child -> {
            if (child instanceof VariableAssignment variableAssignment) {
                checkVariableAssignment(variableAssignment);
            } else if (child instanceof Stylerule stylerule) {
                checkStylerule(stylerule);
            }
        });
    }

    private void checkStylerule(Stylerule stylerule) {
        //TODO: implement
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        if (variableAssignment.getChildren().get(1) instanceof Operation operation) {
            checkOperation(operation);
        }
        //TODO: add to variableTypes
    }

    private ExpressionType checkExpression(Expression expression) {
        if (expression instanceof Operation operation) {
            return checkOperation(operation);
        } else if (expression instanceof Literal literal) {
            return checkLiteral(literal);
        }
        //TODO: add VariableReference check
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkLiteral(Literal literal) {
        return literal instanceof BoolLiteral ? ExpressionType.BOOL : literal instanceof ColorLiteral ? ExpressionType.COLOR : literal instanceof PercentageLiteral ? ExpressionType.PERCENTAGE : literal instanceof PixelLiteral ? ExpressionType.PIXEL : literal instanceof ScalarLiteral ? ExpressionType.SCALAR : ExpressionType.UNDEFINED;
    }

    private ExpressionType checkOperation(Operation operation) {
        if (operation.getChildren().stream()
                .map(expression -> checkExpression((Expression) expression))
                .anyMatch(expressionType -> expressionType == ExpressionType.BOOL || expressionType == ExpressionType.COLOR)
        ) {
            operation.setError("Operations may not use bool literals or color literals ");
            return ExpressionType.UNDEFINED;
        }
        if (operation.getChildren().stream().map(expression -> checkExpression((Expression) expression)).anyMatch(expressionType -> expressionType == ExpressionType.UNDEFINED)) {
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
        if (operation.getChildren().stream().map(expression -> checkExpression((Expression) expression)).noneMatch(expressionType -> expressionType == ExpressionType.SCALAR)) {
            operation.setError("Multiply operations must have at least 1 scalar literal");
            return ExpressionType.UNDEFINED;
        }
        return operation.getChildren().stream().map(expression -> checkExpression((Expression) expression)).filter(expressionType -> expressionType != ExpressionType.SCALAR).findFirst().orElse(ExpressionType.SCALAR);
    }

    private ExpressionType checkAddOrSubtractOperation(Operation operation) {
        if (operation.getChildren().stream().map(expression -> checkExpression((Expression) expression)).filter(expressionType -> expressionType != ExpressionType.UNDEFINED).distinct().count() > 1) {
            operation.setError("Add and subtract operations must have matching literals");
            return ExpressionType.UNDEFINED;
        }
        return checkExpression((Expression) operation.getChildren().get(0));
    }


}
