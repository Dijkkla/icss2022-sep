package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import nl.han.ica.icss.checker.SemanticError;

import java.util.ArrayList;
import java.util.List;

@Setter
@EqualsAndHashCode
public class AST {
    //The root of the tree
    public Stylesheet root;

    public AST() {
        root = new Stylesheet();
    }

    public AST(Stylesheet stylesheet) {
        root = stylesheet;
    }

    public List<SemanticError> getErrors() {
        List<SemanticError> errors = new ArrayList<>();
        collectErrors(errors, root);
        return errors;
    }

    private void collectErrors(List<SemanticError> errors, ASTNode node) {
        if (node.hasError()) {
            errors.add(node.getError());
        }
        for (ASTNode child : node.getChildren()) {
            collectErrors(errors, child);
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
