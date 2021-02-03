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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Convenience class for creating icons wrapped in a StackPane.
 * <p>
 * The size is equal and fixed both for height and width.
 */
public class MFXIconWrapper extends StackPane {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-icon-wrapper";
    private final RippleGenerator rippleGenerator = new RippleGenerator(this);

    //================================================================================
    // Constructors
    //================================================================================
    public MFXIconWrapper() {
        initialize();
    }

    public MFXIconWrapper(Node node, double size) {
        super.getChildren().setAll(node);
        setPrefSize(size, size);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds a ripple generator to the icon. It is an optional.
     */
    public MFXIconWrapper addRippleGenerator() {
        if (!getChildren().contains(rippleGenerator)) {
            super.getChildren().add(0, rippleGenerator);
        }
        return this;
    }

    protected void initialize() {
        getStyleClass().add(STYLE_CLASS);
        setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    /**
     * Returns the icon node instance.
     */
    public Node getIcon() {
        try {
            if (getChildren().size() > 1) {
                return getChildren().get(1);
            } else {
                return getChildren().get(0);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Sets the icon node.
     */
    public void setIcon(Node node) {
        if (getChildren().size() > 1) {
            super.getChildren().set(1, node);
        } else if (!getChildren().isEmpty() && (getChildren().get(0) instanceof RippleGenerator)){
            super.getChildren().add(0, node);
        } else {
            super.getChildren().set(0, node);
        }
    }

    /**
     * Removes the icon node.
     */
    public void removeIcon() {
        if (getChildren().size() > 1) {
            super.getChildren().remove(1);
        }
    }

    /**
     * Sets the size of the container.
     */
    public void setSize(double size) {
        setPrefSize(size, size);
    }

    /**
     * @return the RippleGenerator instance.
     */
     public RippleGenerator getRippleGenerator() {
        return rippleGenerator;
     }

    /**
     * @return an unmodifiable list of the StackPane children
     */
    @Override
    public ObservableList<Node> getChildren() {
        return FXCollections.unmodifiableObservableList(super.getChildren());
    }
}
