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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXCheckbox}.
 */
public class MFXCheckboxSkin extends SkinBase<MFXCheckbox> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label label;
    private final MFXIconWrapper box;
    private final double boxSize = 27;

    private final AnchorPane rippleContainer;
    private final double rippleContainerSize = 31;
    private final RippleGenerator rippleGenerator;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckboxSkin(MFXCheckbox checkbox) {
        super(checkbox);

        // Contains the ripple generator and the box
        rippleContainer = new AnchorPane();
        rippleContainer.setPrefSize(rippleContainerSize, rippleContainerSize);
        rippleContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rippleContainer.getStyleClass().setAll("ripple-container");
        NodeUtils.makeRegionCircular(rippleContainer);

        rippleGenerator = new RippleGenerator(rippleContainer, new RippleClipTypeFactory());
        rippleGenerator.setRippleRadius(16);
        rippleGenerator.setInDuration(Duration.millis(500));
        rippleGenerator.setAnimateBackground(false);

        // Contains the mark
        MFXFontIcon icon = new MFXFontIcon(checkbox.getMarkType(), checkbox.getMarkSize(), Color.WHITE);
        icon.getStyleClass().add("mark");
        box = new MFXIconWrapper(icon, boxSize);
        box.getStyleClass().add("box");

        box.setBorder(new Border(new BorderStroke(
                checkbox.getUncheckedColor(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(2.5),
                new BorderWidths(1.8)
        )));
        box.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT,
                new CornerRadii(2.5),
                Insets.EMPTY
        )));

        label = new Label();
        label.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        label.textProperty().bind(checkbox.textProperty());


        rippleContainer.getChildren().addAll(rippleGenerator, box);
        container = new HBox(10, rippleContainer, label);
        container.setAlignment(Pos.CENTER_LEFT);
        getChildren().add(container);

        updateMarkType();
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: markType, selected, indeterminate, checked and unchecked coloros properties.
     */
    private void setListeners() {
        MFXCheckbox checkBox = getSkinnable();

        checkBox.markTypeProperty().addListener(
                (observable, oldValue, newValue) -> updateMarkType()
        );

        checkBox.markSizeProperty().addListener(
                (observable, oldValue, newValue) -> ((MFXFontIcon) box.getIcon()).setFont(Font.font(newValue.doubleValue())));

        checkBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkBox.indeterminateProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkBox.checkedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkBox.uncheckedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        /* Listener on control but if the coordinates of the event are greater than then ripple container size
         * then the center of the ripple is set to the width and/or height of container
         */
        checkBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), checkBox)) {
                return;
            }

            rippleGenerator.setGeneratorCenterX(Math.min(event.getX(), rippleContainer.getWidth()));
            rippleGenerator.setGeneratorCenterY(Math.min(event.getY(), rippleContainer.getHeight()));
            rippleGenerator.createRipple();
            checkBox.fire();
        });

        /*
         * Workaround
         * When the control is created the Skin is still null, so if the CheckBox is set
         * to be selected/indeterminate the animation won't be played. To fix this add a listener to the
         * control's skinProperty, when the skin is not null and the CheckBox isSelected/isIndeterminate,
         * play the animation.
         */
        checkBox.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateColors();
            }
        });
    }


    /**
     * This method is called whenever one of the following properties changes:
     * {@code selectedProperty}, {@code indeterminateProperty}, {@code checkedColor} and {@code uncheckedColor} properties
     *
     * @see NodeUtils
     */
    private void updateColors() {
        MFXCheckbox checkbox = getSkinnable();

        final BorderStroke borderStroke = box.getBorder().getStrokes().get(0);
        if (checkbox.isIndeterminate()) {
            NodeUtils.updateBackground(box, checkbox.getCheckedColor(), new Insets(3.2));
            box.getIcon().setVisible(false);
        } else if (checkbox.isSelected()) {
            NodeUtils.updateBackground(box, checkbox.getCheckedColor(), Insets.EMPTY);
            box.getIcon().setVisible(true);
            box.setBorder(new Border(new BorderStroke(
                    checkbox.getCheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        } else {
            NodeUtils.updateBackground(box, Color.TRANSPARENT);
            box.getIcon().setVisible(false);
            box.setBorder(new Border(new BorderStroke(
                    checkbox.getUncheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        }
    }

    /**
     * This method is called whenever the {@code markType} property changes.
     */
    private void updateMarkType() {
        MFXCheckbox checkbox = getSkinnable();

        MFXFontIcon icon = new MFXFontIcon(checkbox.getMarkType(), checkbox.getMarkSize(), Color.WHITE);
        box.setIcon(icon);
    }

    /**
     * Centers the box in the ripple container
     */
    private void centerBox() {
        final double offsetPercentage = 3;
        final double vInset = ((rippleContainerSize - boxSize) / 2) * offsetPercentage;
        final double hInset = ((rippleContainerSize - boxSize) / 2) * offsetPercentage;
        AnchorPane.setTopAnchor(box, vInset);
        AnchorPane.setRightAnchor(box, hInset);
        AnchorPane.setBottomAnchor(box, vInset);
        AnchorPane.setLeftAnchor(box, hInset);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return rippleContainer.getWidth() + label.getWidth() + container.getSpacing();
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return rippleContainer.getHeight();
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return rippleContainer.getWidth() + label.getWidth() + container.getSpacing();
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return rippleContainer.getHeight();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        centerBox();
    }
}
