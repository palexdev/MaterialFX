package io.github.palexdev.materialfx.beans;

import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.util.Callback;

import java.net.URL;

/**
 * Support bean for {@code MFXHLoader} and {@code MFXVLoader}
 * Basically a wrapper for a {@code Node} which is the root of an fxml file,
 * the controller factory of the fxml file, the toggle button associated with the item
 * which is responsible for the views switching, the {@code URL} of the fxml file,
 * and an index which represents the toggle button position in the children list of the loader.
 */
public class MFXLoadItem {
    //================================================================================
    // Properties
    //================================================================================
    private final int index;
    private final Callback<Class<?>, Object> controllerFactory;
    private final ToggleButton button;
    private final URL fxmlURL;
    private Node root;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXLoadItem(int index, ToggleButton button, URL fxmlURL) {
        this(index, button, fxmlURL, null);
    }

    public MFXLoadItem(int index, ToggleButton button, URL fxmlURL, Callback<Class<?>, Object> controllerFactory) {
        this.index = index;
        this.button = button;
        this.fxmlURL = fxmlURL;
        this.controllerFactory = controllerFactory;
    }

    //================================================================================
    // Methods
    //================================================================================
    public int getIndex() {
        return index;
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    public ToggleButton getButton() {
        return button;
    }

    public URL getFxmlURL() {
        return fxmlURL;
    }
}
