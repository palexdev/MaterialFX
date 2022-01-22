/*
 * Copyright (C) 2022 Parisi Alessandro
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

package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.BoundLabel;
import io.github.palexdev.materialfx.controls.MFXContextMenu;
import io.github.palexdev.materialfx.controls.MFXContextMenuItem;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

/**
 * Skin associated with every {@link MFXContextMenuItem} by default.
 * <p></p>
 * This skin is composed of a top container, which is a {@link HBox},
 * a {@link MFXIconWrapper} to contain the icon and two labels to show
 * the item's text and the accelerator.
 */
public class MFXContextMenuItemSkin extends SkinBase<MFXContextMenuItem> {
	//================================================================================
	// Properties
	//================================================================================
	private final HBox container;
	private final MFXIconWrapper icon;
	private final BoundLabel label;
	private final Label accelerator;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXContextMenuItemSkin(MFXContextMenuItem item) {
		super(item);

		label = new BoundLabel(item);
		label.graphicProperty().unbind();
		label.setGraphic(null);

		icon = new MFXIconWrapper(null, 24);
		icon.setIcon(item.getGraphic());
		icon.iconProperty().bind(item.graphicProperty());

		accelerator = new Label();
		accelerator.getStyleClass().add("accelerator");
		accelerator.textProperty().bind(item.acceleratorProperty());
		accelerator.setAlignment(Pos.CENTER_RIGHT);
		accelerator.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(accelerator, Priority.ALWAYS);

		container = new HBox(10, icon, label, accelerator);
		container.setAlignment(Pos.CENTER_LEFT);

		if (item.getTooltipSupplier() != null) {
			Tooltip tooltip = item.getTooltipSupplier().get();
			if (tooltip != null) item.setTooltip(tooltip);
		}

		addListeners();
		getChildren().setAll(container);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXContextMenuItem item = getSkinnable();

		item.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			Scene scene = item.getScene();
			Window window = scene.getWindow();
			if (window instanceof MFXContextMenu) {
				window.hide();
			}
		});

		item.tooltipSupplierProperty().addListener((observable, oldValue, newValue) -> {
			item.setTooltip(null);
			if (newValue != null) {
				Tooltip tooltip = item.getTooltipSupplier().get();
				if (tooltip != null) item.setTooltip(tooltip);
			}
		});

		item.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() != MouseButton.PRIMARY) return;
			item.fireEvent(new ActionEvent());
		});
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return leftInset +
				icon.getSize() +
				Math.abs(label.snappedLeftInset()) + label.prefWidth(-1) + Math.abs(label.snappedRightInset()) +
				accelerator.snappedLeftInset() + accelerator.prefWidth(-1) + accelerator.snappedRightInset() +
				rightInset;
	}
}
