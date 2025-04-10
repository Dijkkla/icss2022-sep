package nl.han.ica.icss.gui;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@SuppressWarnings("restriction")
public class InputPane extends BorderPane {
    private final TextArea content;
    private final Label title;

    public InputPane() {
        super();

        title = new Label("Input (ICSS):");
        content = new TextArea();
        title.setPadding(new Insets(5, 5, 5, 5));

        this.setTop(title);
        this.setCenter(content);
    }

    public String getText() {
        return content.getText();
    }

    public void setText(String text) {
        this.content.setText(text);
    }

    public void setText(File file) {
        try {
            this.setText(Files.readString(file.toPath(), Charset.defaultCharset()));
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
