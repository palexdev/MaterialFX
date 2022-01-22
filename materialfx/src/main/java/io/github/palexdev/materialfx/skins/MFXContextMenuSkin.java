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

import io.github.palexdev.materialfx.beans.PopupPositionBean;
import io.github.palexdev.materialfx.controls.MFXContextMenu;
import io.github.palexdev.materialfx.controls.MFXPopup;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.WindowEvent;

/**
 * Skin associated with every {@link MFXContextMenu} by default.
 * <p>
 * This is very similar to {@link MFXPopupSkin} but there are two containers.
 * The top container is a {@link MFXScrollPane}, because sometimes context menus can contain lots of
 * items, but it is desirable to keep it under a certain height and make it scrollable. Then there's a
 * VBox containing all the items specified by {@link MFXContextMenu#getItems()}, this box is set as the content
 * of the scroll pane.
 * <p></p>
 * A little side note on the popup's {@link MFXPopup#contentProperty()}:
 * <p>
 * {@link MFXContextMenu} is a special case of {@link MFXPopup}. This skin is
 * responsible for setting the popup's content to the aforementioned scroll pane.
 * While it still allows to change the content whenever you want, it's nonsense, so
 * don't do it.
 */
public class MFXContextMenuSkin implements Skin<MFXContextMenu> {
	//================================================================================
	// Properties
	//================================================================================
	private MFXContextMenu contextMenu;
	private final MFXScrollPane scrollPane;
	private final VBox container;
	private final Scale scale;

	private Animation animation;
	private EventHandler<WindowEvent> initHandler;
	private EventHandler<WindowEvent> closeHandler;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXContextMenuSkin(MFXContextMenu contextMenu) {
		this.contextMenu = contextMenu;

		scale = new Scale(0.1, 0.1, 0, 0);
		container = new VBox() {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();

				ObservableList<Node> children = getChildren();
				for (Node child : children) {
					if (child instanceof Line) {
						Line separator = (Line) child;
						separator.setEndX(getWidth() - (snappedRightInset() + snappedLeftInset()));
					}
				}
			}
		};
		Bindings.bindContent(container.getChildren(), contextMenu.getItems());

		scrollPane = new MFXScrollPane(container) {
			@Override
			public String getUserAgentStylesheet() {
				return contextMenu.getUserAgentStylesheet();
			}
		};
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.getTransforms().add(scale);
		contextMenu.setContent(scrollPane);

		initHandler = event -> {
			init();
			if (contextMenu.isAnimated()) {
				animation = contextMenu.getAnimationProvider().apply(scrollPane, scale);
				animation.play();
			} else {
				scale.setX(1);
				scale.setY(1);
				scrollPane.setOpacity(1);
			}
		};

		closeHandler = event -> {
			scrollPane.setOpacity(0.0);
			scale.setX(0.1);
			scale.setY(0.1);
		};

		contextMenu.addEventHandler(WindowEvent.WINDOW_SHOWN, initHandler);
		contextMenu.addEventHandler(WindowEvent.WINDOW_HIDDEN, closeHandler);
		Bindings.bindContent(scrollPane.getStylesheets(), contextMenu.getStyleSheets());
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Positions the popup.
	 */
	protected void init() {
		scrollPane.setOpacity(0.0);

		PopupPositionBean position = contextMenu.getPosition();
		if (position == null) return;

		if (position.getAlignment() != null) {
			double containerW = container.prefWidth(-1);
			double containerH = container.prefHeight(-1);

			HPos hPos = position.getHPos();
			VPos vPos = position.getVPos();
			double xOffset = position.getXOffset();
			double yOffset = position.getYOffset();

			double px = hPos == HPos.RIGHT ? xOffset : containerW + xOffset;
			double py = vPos == VPos.BOTTOM ? yOffset : containerH + yOffset;

			scale.setPivotX(px);
			scale.setPivotY(py);
		}
		contextMenu.reposition();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public MFXContextMenu getSkinnable() {
		return contextMenu;
	}

	@Override
	public Node getNode() {
		return scrollPane;
	}

	@Override
	public void dispose() {
		animation.stop();
		animation = null;
		contextMenu.removeEventHandler(WindowEvent.WINDOW_SHOWN, initHandler);
		contextMenu.removeEventHandler(WindowEvent.WINDOW_HIDDEN, closeHandler);
		initHandler = null;
		closeHandler = null;
		contextMenu = null;
	}
}
