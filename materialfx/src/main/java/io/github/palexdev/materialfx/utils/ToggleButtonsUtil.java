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

package io.github.palexdev.materialfx.utils;

import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

/**
 * Utils class for {@code ToggleButtons}.
 */
public class ToggleButtonsUtil {

	private static final EventHandler<MouseEvent> consumeMouseEventFilter = (MouseEvent mouseEvent) -> {
		if (((Toggle) mouseEvent.getSource()).isSelected()) {
			mouseEvent.consume();
		}
	};

	private static void addConsumeMouseEventFilter(Toggle toggle) {
		((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_PRESSED, consumeMouseEventFilter);
		((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_RELEASED, consumeMouseEventFilter);
		((ToggleButton) toggle).addEventFilter(MouseEvent.MOUSE_CLICKED, consumeMouseEventFilter);
	}

	/**
	 * Adds a handler to the given {@code ToggleGroup} to make sure there's always at least
	 * one {@code ToggleButton} selected.
	 *
	 * @param toggleGroup The given ToggleGroup
	 */
	public static void addAlwaysOneSelectedSupport(final ToggleGroup toggleGroup) {
		toggleGroup.getToggles().addListener((ListChangeListener.Change<? extends Toggle> c) -> {
			while (c.next()) {
				for (final Toggle addedToggle : c.getAddedSubList()) {
					addConsumeMouseEventFilter(addedToggle);
				}
			}
		});
		toggleGroup.getToggles().forEach(ToggleButtonsUtil::addConsumeMouseEventFilter);
	}

	/**
	 * Copied from {@link ToggleGroup}. It's a package-private method that is used in Toggles
	 * when the selection state changes and a toggle group is set.
	 */
	public static void clearSelectedToggle(ToggleGroup toggleGroup) {
		Toggle selectedToggle = toggleGroup.getSelectedToggle();
		if (!selectedToggle.isSelected()) {
			for (Toggle toggle : toggleGroup.getToggles()) {
				if (toggle.isSelected()) {
					return;
				}
			}
		}
		toggleGroup.selectToggle(null);
	}
}
