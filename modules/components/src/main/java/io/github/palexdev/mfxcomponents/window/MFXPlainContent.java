package io.github.palexdev.mfxcomponents.window;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.controls.Text;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * A simple pane to display some text. Ideal for simple tooltips.
 */
public class MFXPlainContent extends StackPane implements MFXStyleable {
    //================================================================================
    // Properties
    //================================================================================
    private final StringProperty text = new SimpleStringProperty();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPlainContent() {
        this("");
    }

    public MFXPlainContent(String text) {
        setText(text);

        Text lText = new Text();
        lText.textProperty().bind(textProperty());

        getStyleClass().setAll(defaultStyleClasses());
        super.getChildren().add(lText);
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public ObservableList<Node> getChildren() {
        return getChildrenUnmodifiable();
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("plain");
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public String getText() {
        return text.get();
    }

    /**
     * Specifies the text to display.
     */
    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }
}
