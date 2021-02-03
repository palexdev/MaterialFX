/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    private Node root;
    private final Callback<Class<?>, Object> controllerFactory;
    private final ToggleButton button;
    private final URL fxmlURL;

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

    public Callback<Class<?>, Object> getControllerFactory() {
        return controllerFactory;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ToggleButton getButton() {
        return button;
    }

    public URL getFxmlURL() {
        return fxmlURL;
    }
}
