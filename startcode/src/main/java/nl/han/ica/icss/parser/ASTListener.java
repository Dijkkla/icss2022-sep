package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private final AST ast = new AST();

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private final IHANStack<ASTNode> currentContainer = new HANStack<>();

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.root = (Stylesheet) currentContainer.pop();
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = (Stylerule) currentContainer.pop();
        currentContainer.peek().addChild(stylerule);
    }

    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSelector = new IdSelector(ctx.getText());
        currentContainer.push(idSelector);
    }

    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSelector = (IdSelector) currentContainer.pop();
        currentContainer.peek().addChild(idSelector);
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSelector = new ClassSelector(ctx.getText());
        currentContainer.push(classSelector);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSelector = (ClassSelector) currentContainer.pop();
        currentContainer.peek().addChild(classSelector);
    }

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSelector = new TagSelector(ctx.getText());
        currentContainer.push(tagSelector);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSelector = (TagSelector) currentContainer.pop();
        currentContainer.peek().addChild(tagSelector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration(ctx.getChild(0).getText());
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = (Declaration) currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterFactorialOperation(ICSSParser.FactorialOperationContext ctx) {
        Operation operation = new FactorialOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitFactorialOperation(ICSSParser.FactorialOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterNotOperation(ICSSParser.NotOperationContext ctx) {
        Operation operation = new NotOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitNotOperation(ICSSParser.NotOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterPowerOperation(ICSSParser.PowerOperationContext ctx) {
        Operation operation = new PowerOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitPowerOperation(ICSSParser.PowerOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterMultiplyOrDivideOperation(ICSSParser.MultiplyOrDivideOperationContext ctx) {
        Operation operation = ctx.MUL() != null ? new MultiplyOperation() : new DivideOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitMultiplyOrDivideOperation(ICSSParser.MultiplyOrDivideOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx) {
        Operation operation = ctx.PLUS() != null ? new AddOperation() : new SubtractOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitAddOrSubtractOperation(ICSSParser.AddOrSubtractOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterCompareOperation(ICSSParser.CompareOperationContext ctx) {
        Operation operation = ctx.EQUALS() != null || ctx.NOT_EQUALS() != null ? new EqualsOperation() : new GreaterThanOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitCompareOperation(ICSSParser.CompareOperationContext ctx) {
        Operation operation = ctx.EQUALS() != null ? (Operation) currentContainer.pop()
                : ctx.NOT_EQUALS() != null ? (Operation) (new NotOperation()).addChild(currentContainer.pop())
                : ctx.GREATER_THAN() != null ? (Operation) currentContainer.pop()
                : ctx.SMALLER_THAN() != null ? (Operation) ((Operation) currentContainer.pop()).switchSides()
                : ctx.GREATER_OR_EQUAL_THAN() != null ? (Operation) (new NotOperation()).addChild(((Operation) currentContainer.pop()).switchSides())
                : ctx.SMALLER_OR_EQUAL_THAN() != null ? (Operation) (new NotOperation()).addChild(currentContainer.pop())
                : null;
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterBooleanOperation(ICSSParser.BooleanOperationContext ctx) {
        Operation operation = ctx.AND() != null ? new AndOperation() : new OrOperation();
        currentContainer.push(operation);
    }

    @Override
    public void exitBooleanOperation(ICSSParser.BooleanOperationContext ctx) {
        Operation operation = (Operation) currentContainer.pop();
        currentContainer.peek().addChild(operation);
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText().toLowerCase());
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLiteral = (ColorLiteral) currentContainer.pop();
        currentContainer.peek().addChild(colorLiteral);
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        PercentageLiteral percentageLiteral = (PercentageLiteral) currentContainer.pop();
        currentContainer.peek().addChild(percentageLiteral);
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLiteral = (PixelLiteral) currentContainer.pop();
        currentContainer.peek().addChild(pixelLiteral);
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ScalarLiteral scalarLiteral = (ScalarLiteral) currentContainer.pop();
        currentContainer.peek().addChild(scalarLiteral);
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        BoolLiteral boolLiteral = (BoolLiteral) currentContainer.pop();
        currentContainer.peek().addChild(boolLiteral);
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = (VariableReference) currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        IfClause ifClause = (IfClause) currentContainer.pop();
        currentContainer.peek().addChild(ifClause);
    }

    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause elseClause = new ElseClause();
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        ElseClause elseClause = (ElseClause) currentContainer.pop();
        currentContainer.peek().addChild(elseClause);
    }
}