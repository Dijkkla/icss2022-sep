package nl.han.ica.icss.checker;

public record SemanticError(String description) {

    public String toString() {
        return "ERROR: " + description;
    }
}
