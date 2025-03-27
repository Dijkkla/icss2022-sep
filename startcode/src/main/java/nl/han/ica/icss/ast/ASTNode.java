package nl.han.ica.icss.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.han.ica.icss.checker.SemanticError;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
public abstract class ASTNode {

    private SemanticError error = null;

    /*
     This method is used in the GUI to create an appropriate label
     in the tree visualisation.
      */
    public abstract String getNodeLabel();

    /*
     Different AST nodes use different attributes to store their children.
     This method provides a unified interface.
     */
    public List<ASTNode> getChildren() {
        return new ArrayList<>();
    }

    /*
    By implementing this method in a subclass you can easily create AST nodes
      incrementally.
    */
    public ASTNode addChild(ASTNode child) {
        return this;
    }

    /*
     * By implementing this method you can easily make transformations that prune the AST.
     */
    public ASTNode removeChild(ASTNode child) {
        return this;
    }

    public void setError(String description) {
        this.error = new SemanticError(description);
    }

    public boolean hasError() {
        return error != null;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        toString(result);
        return result.toString();
    }

    private void toString(StringBuilder builder) {
        builder.append("[");
        builder.append(getNodeLabel());
        builder.append("|");
        for (ASTNode child : getChildren()) {
            child.toString(builder);
        }
        builder.append("]");
    }
}
