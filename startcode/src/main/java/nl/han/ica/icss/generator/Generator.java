package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

    StringBuilder stringBuilder = new StringBuilder();

    public String generate(AST ast) {
        generateStylesheet(ast.root);
        return stringBuilder.toString();
    }

    private void generateStylesheet(Stylesheet stylesheet) {
        if (stylesheet.body.stream().anyMatch(node -> node instanceof Stylerule)) {
            stylesheet.body.forEach(stylerule -> generateStylerule((Stylerule) stylerule));
        } else if (stylesheet.body.stream().anyMatch(node -> node instanceof Literal)) {
            stylesheet.body.forEach(literal -> generateLiteral((Literal) literal));
        } else if (!stylesheet.body.isEmpty()) {
            throw new IllegalStateException("AST was not properly transformed, making code generation impossible");
        }
    }

    private void generateLiteral(Literal literal) {
        stringBuilder.append(literal.toString()).append("\n");
    }

    private void generateStylerule(Stylerule stylerule) {
        stylerule.selectors.forEach(selector -> stringBuilder.append(selector.toString()).append(" "));
        stringBuilder.append("{\n");
        stylerule.body.forEach(declaration -> generateDeclaration((Declaration) declaration));
        stringBuilder.append("}\n\n");
    }

    private void generateDeclaration(Declaration declaration) {
        stringBuilder
                .append("\t")
                .append(declaration.property.name)
                .append(": ")
                .append(switch (declaration.expression.expressionType) {
                    case PERCENTAGE -> ((PercentageLiteral) declaration.expression).value + "%";
                    case PIXEL -> ((PixelLiteral) declaration.expression).value + "px";
                    case COLOR -> ((ColorLiteral) declaration.expression).value;
                    default ->
                            throw new IllegalStateException("Unexpected value: " + declaration.expression.expressionType);
                })
                .append(";\n");
    }


}
