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

package io.github.palexdev.materialfx.effects;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

/**
 * From Google's material design guidelines:
 * <p>
 * Scrims are temporary treatments that can be applied to Material surfaces for the purpose of making content on a surface less prominent.
 * They help direct user attention to other parts of the screen, away from the surface receiving a scrim.
 */
public class MFXScrimEffect {
	//================================================================================
	// Properties
	//================================================================================
	private final Rectangle scrim;

	//================================================================================
	// Constructor
	//================================================================================
	public MFXScrimEffect() {
		scrim = new Rectangle();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds a scrim effect to the specified pane with specified opacity.
	 *
	 * @param pane    The pane to which add the effect
	 * @param opacity The effect opacity/strength
	 */
	public void scrim(Pane pane, double opacity) {
		scrim.widthProperty().bind(pane.widthProperty());
		scrim.heightProperty().bind(pane.heightProperty());
		scrim.setFill(Color.rgb(0, 0, 0, opacity));
		scrim.setBlendMode(BlendMode.SRC_ATOP);

		pane.getChildren().add(0, scrim);
	}

	/**
	 * Same as {@link #scrim(Pane, double)} but the effect is placed at
	 * the end of the children list, covering all the pane's nodes
	 *
	 * @param pane    The pane to which add the effect
	 * @param opacity The effect opacity/strength
	 */
	public void modalScrim(Pane pane, double opacity) {
		scrim.widthProperty().bind(pane.widthProperty());
		scrim.heightProperty().bind(pane.heightProperty());
		scrim.setFill(Color.rgb(0, 0, 0, opacity));
		scrim.setBlendMode(BlendMode.SRC_ATOP);

		pane.getChildren().add(scrim);
	}

	/**
	 * Adds a scrim effect to the specified pane with specified opacity.
	 * It also simulates the modal behavior of {@code Stages}, leaving only the specified
	 * {@code Node} interactive.
	 *
	 * @param parent  The pane to which add the effect
	 * @param child   The node to leave interactive
	 * @param opacity The effect opacity/strength
	 */
	public void modalScrim(Pane parent, Node child, double opacity) {
		scrim.widthProperty().bind(parent.widthProperty());
		scrim.heightProperty().bind(parent.heightProperty());
		scrim.setFill(Color.rgb(0, 0, 0, opacity));
		scrim.setBlendMode(BlendMode.SRC_ATOP);

		/*
		 * Workaround, especially for SceneBuilder
		 * This method adds the scrim effect to the given pane's children list
		 * before the given node to leave interactable so if that node is let's say in position 2
		 * and there are others controls after index 2 they will be interactable.
		 * To fix that and avoid some hassle for developers this piece of code
		 * finds the node to leave interactable and if it is not in the last position of the list
		 * removes and re-adds it, then adds the scrim effect in the second-last position which of course is
		 * (list.size() - 1)
		 */
		ObservableList<Node> children = parent.getChildren();
		children.stream()
				.filter(node -> node.equals(child))
				.findFirst()
				.ifPresent(node -> {
					if (children.indexOf(node) != children.size() - 1) {
						parent.getChildren().remove(node);
						parent.getChildren().add(node);
					}
				});

		parent.getChildren().add(children.size() - 1, scrim);
	}

	/**
	 * Adds a scrim effect to the specified {@code Window}'s root pane with the specified opacity.
	 *
	 * @param window  The desired window
	 * @param opacity The desired opacity
	 */
	public void scrimWindow(Window window, double opacity) {
		Parent root = window.getScene().getRoot();
		if (root instanceof Pane) {
			Pane pane = (Pane) root;
			scrim.widthProperty().bind(pane.widthProperty());
			scrim.heightProperty().bind(pane.heightProperty());
			scrim.setFill(Color.rgb(0, 0, 0, opacity));
			scrim.setBlendMode(BlendMode.SRC_ATOP);
			pane.getChildren().add(scrim);
		}
	}

	/**
	 * Removes the scrim effect from the specified pane.
	 *
	 * @param pane The pane to which remove the effect.
	 */
	public void removeEffect(Pane pane) {
		pane.getChildren().remove(scrim);
		unbindResizing();
	}

	/**
	 * Removes the scrim effect from the specified window.
	 *
	 * @param window The window to which remove the effect.
	 */
	public void removeEffect(Window window) {
		Parent root = window.getScene().getRoot();
		if (root instanceof Pane) {
			removeEffect((Pane) root);
			unbindResizing();
		}
	}

	/**
	 * Removes the bindings to the width and height properties of the
	 * scrim effect when it is removed.
	 */
	private void unbindResizing() {
		scrim.widthProperty().unbind();
		scrim.heightProperty().unbind();
	}

	public Node getScrimNode() {
		return scrim;
	}
}
