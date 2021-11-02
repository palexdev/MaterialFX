/*
 * Copyright (C) 2021 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXContextMenuItemSkin;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import java.util.function.Supplier;

/**
 * Even if the {@link MFXContextMenu} builder allows to add any node to the items list, this
 * control is the recommended item to use when building {@code MFXContextMenus}.
 * <p></p>
 * Extends {@code Control} and defines its own skin. It allows specifying not only the text but also
 * the accelerator string and an icon. Each of them has separate properties like: alignment, width and insets/padding.
 * <p></p>
 * Defines a property, {@link #actionProperty()} to allow the user to specify what to do when the mouse is pressed
 * on the item.
 * <p>
 * It also allows to specify a {@link Tooltip} by defining a tooltip supplier property.
 * <p></p>
 * A no-arg constructor is defined because this control is design with fluent api in mind, all the setters
 * return the context menu item.
 * <p></p>
 * An example:
 * <p></p>
 * <pre>
 * {@code
 * MFXContextMenuItem item1 = new MFXContextMenuItem()
 *     .setText("Context Menu Item 1")
 *     .setAccelerator("Shift + C")
 *     .setTooltipSupplier(() -> new Tooltip("Item 1"))
 *     .setAction(event -> System.out.println("Action 1 Executed"));
 * }
 * </pre>
 *
 * <p></p>
 * A little note on the icon, please please don't use big nodes like buttons, it won't work,
 * the context menu item is not made to support such things. The icon should be a small node like
 * font icons.
 * <p></p>
 * A little note on the accelerator, the accelerator property is just a string property it won't add the needed
 * event handler to the control, I think this is the best solution since handlers may also have some complex logic defined
 * by the user (see MFXPasswordFieldSkin for example). A solution could be to give the opportunity of specifying an event handler supplier, but this would also
 * mean that potentially for each menu item an event handler will be added to the node which is not a great idea for performance.
 */
public class MFXContextMenuItem extends Control {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-context-menu-item";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXContextMenuItem.css");

    private final StringProperty text = new SimpleStringProperty("");
    private final StringProperty accelerator = new SimpleStringProperty("");
    private final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
    private final DoubleProperty spacing = new SimpleDoubleProperty(0);
    private final DoubleProperty textWidth = new SimpleDoubleProperty(80);
    private final DoubleProperty acceleratorWidth = new SimpleDoubleProperty(50);
    private final ObjectProperty<Pos> textAlignment = new SimpleObjectProperty<>(Pos.CENTER_LEFT);
    private final ObjectProperty<Pos> acceleratorAlignment = new SimpleObjectProperty<>(Pos.CENTER_RIGHT);
    private final ObjectProperty<Insets> textInsets = new SimpleObjectProperty<>(new Insets(5, 0, 5, 10));
    private final ObjectProperty<Insets> acceleratorInsets = new SimpleObjectProperty<>(new Insets(5, 10, 5, 0));
    private final ObjectProperty<Supplier<Tooltip>> tooltipSupplier = new SimpleObjectProperty<>();
    private final ObjectProperty<EventHandler<MouseEvent>> action = new SimpleObjectProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(MouseEvent.MOUSE_PRESSED, get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXContextMenuItem() {
        this("", "");
    }

    public MFXContextMenuItem(String text) {
        this(text, "");
    }

    public MFXContextMenuItem(String text, String accelerator) {
        setText(text);
        setAccelerator(accelerator);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        tooltipSupplier.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                setTooltip(null);
            }
            if (newValue != null) {
                setTooltip(newValue.get());
            }
        });
    }

    public String getText() {
        return text.get();
    }

    /**
     * Specifies the item's text.
     */
    public StringProperty textProperty() {
        return text;
    }

    public MFXContextMenuItem setText(String text) {
        this.text.set(text);
        return this;
    }

    public String getAccelerator() {
        return accelerator.get();
    }

    /**
     * Specifies the item's accelerator.
     */
    public StringProperty acceleratorProperty() {
        return accelerator;
    }

    public MFXContextMenuItem setAccelerator(String accelerator) {
        this.accelerator.set(accelerator);
        return this;
    }

    public Node getIcon() {
        return icon.get();
    }

    /**
     * Specifies the item's icon.
     */
    public ObjectProperty<Node> iconProperty() {
        return icon;
    }

    public MFXContextMenuItem setIcon(Node icon) {
        this.icon.set(icon);
        return this;
    }

    public MFXContextMenuItem setIcon(Supplier<Node> icon) {
        this.icon.set(icon.get());
        return this;
    }

    public double getSpacing() {
        return spacing.get();
    }

    /**
     * Specifies the spacing between the text and the accelerator.
     */
    public DoubleProperty spacingProperty() {
        return spacing;
    }

    public MFXContextMenuItem setSpacing(double spacing) {
        this.spacing.set(spacing);
        return this;
    }

    public double getTextWidth() {
        return textWidth.get();
    }

    /**
     * Specifies the text label width.
     */
    public DoubleProperty textWidthProperty() {
        return textWidth;
    }

    public MFXContextMenuItem setTextWidth(double textWidth) {
        this.textWidth.set(textWidth);
        return this;
    }

    public double getAcceleratorWidth() {
        return acceleratorWidth.get();
    }

    /**
     * Specifies the accelerator label width.
     */
    public DoubleProperty acceleratorWidthProperty() {
        return acceleratorWidth;
    }

    public MFXContextMenuItem setAcceleratorWidth(double acceleratorTextWidth) {
        this.acceleratorWidth.set(acceleratorTextWidth);
        return this;
    }

    public Pos getTextAlignment() {
        return textAlignment.get();
    }

    /**
     * Specifies the text alignment.
     */
    public ObjectProperty<Pos> textAlignmentProperty() {
        return textAlignment;
    }

    public MFXContextMenuItem setTextAlignment(Pos textAlignment) {
        this.textAlignment.set(textAlignment);
        return this;
    }

    public Pos getAcceleratorAlignment() {
        return acceleratorAlignment.get();
    }

    /**
     * Specifies the accelerator text alignment.
     */
    public ObjectProperty<Pos> acceleratorAlignmentProperty() {
        return acceleratorAlignment;
    }

    public MFXContextMenuItem setAcceleratorAlignment(Pos acceleratorAlignment) {
        this.acceleratorAlignment.set(acceleratorAlignment);
        return this;
    }

    public Insets getTextInsets() {
        return textInsets.get();
    }

    /**
     * Specifies the text label padding.
     */
    public ObjectProperty<Insets> textInsetsProperty() {
        return textInsets;
    }

    public MFXContextMenuItem setTextInsets(Insets textInsets) {
        this.textInsets.set(textInsets);
        return this;
    }

    public Insets getAcceleratorInsets() {
        return acceleratorInsets.get();
    }

    /**
     * Specifies the accelerator label padding.
     */
    public ObjectProperty<Insets> acceleratorInsetsProperty() {
        return acceleratorInsets;
    }

    public MFXContextMenuItem setAcceleratorInsets(Insets acceleratorInsets) {
        this.acceleratorInsets.set(acceleratorInsets);
        return this;
    }

    public Supplier<Tooltip> getTooltipSupplier() {
        return tooltipSupplier.get();
    }

    /**
     * Specifies the supplier used to build the item's tooltip.
     * <p>
     * Set it to null to disable it.
     */
    public ObjectProperty<Supplier<Tooltip>> tooltipSupplierProperty() {
        return tooltipSupplier;
    }

    public MFXContextMenuItem setTooltipSupplier(Supplier<Tooltip> tooltipSupplier) {
        this.tooltipSupplier.set(tooltipSupplier);
        return this;
    }

    public EventHandler<MouseEvent> getAction() {
        return action.get();
    }

    /**
     * Specifies the action to perform when the mouse is pressed on the item.
     */
    public ObjectProperty<EventHandler<MouseEvent>> actionProperty() {
        return action;
    }

    public MFXContextMenuItem setAction(EventHandler<MouseEvent> action) {
        this.action.set(action);
        return this;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXContextMenuItemSkin(this);
    }
}
