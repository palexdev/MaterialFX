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

package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.cell.base.AbstractMFXListCell;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectExpression;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 * Implementation of an {@link AbstractMFXListCell} which has a combo box
 * for usage in {@link MFXCheckListView}.
 * <p></p>
 * The label used to display the data is built in the constructor
 * only if the given T data is not a Node, otherwise it's null.
 * <p></p>
 * The label's text is bound to the data property and converted to a String
 * using {@link ObjectExpression#asString()}.
 */
public class MFXCheckListCell<T> extends AbstractMFXListCell<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-check-list-cell";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXCheckListCell.css");
	protected final MFXCircleRippleGenerator rippleGenerator = new MFXCircleRippleGenerator(this);

	private final MFXCheckListView<T> listView;
	protected final MFXCheckbox checkbox;
	protected final Label label;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXCheckListCell(MFXCheckListView<T> listView, T data) {
		super(listView, data);
		this.listView = listView;
		checkbox = new MFXCheckbox("");
		checkbox.getStylesheets().setAll(getUserAgentStylesheet());

		if (!(data instanceof Node)) {
			label = new Label();
			label.textProperty().bind(Bindings.createStringBinding(
					() -> listView.getConverter() != null ? listView.getConverter().toString(getData()) : getData().toString(),
					dataProperty(), listView.converterProperty()
			));
			label.getStyleClass().add("data-label");
		} else {
			label = null;
		}

		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Overridden to add the style class, setup the ripple generator and call {@link #render(Object)}
	 * for the first time.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		getStyleClass().add(STYLE_CLASS);
		setupRippleGenerator();
		render(getData());
	}

	/**
	 * Sets up the properties of the ripple generator and adds the mouse pressed filter.
	 */
	protected void setupRippleGenerator() {
		rippleGenerator.setManaged(false);
		rippleGenerator.setRipplePositionFunction(event -> PositionBean.of(event.getX(), event.getY()));
		rippleGenerator.rippleRadiusProperty().bind(widthProperty().divide(2.0));
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (NodeUtils.inHierarchy(event, checkbox)) {
				rippleGenerator.generateRipple(event);
			}
		});
	}

	/**
	 * Responsible for updating the selection state according to the checkbox' state.
	 * <p>
	 * If checked is true then the cell should be selected, otherwise it is deselected.
	 */
	private void updateSelection(boolean checked) {
		int index = getIndex();
		if (checked) {
			listView.getSelectionModel().selectIndex(index);
		} else {
			listView.getSelectionModel().deselectIndex(index);
		}
	}

	//================================================================================
	// Overridden/Implemented Methods
	//================================================================================

	/**
	 * Overridden as the selected state depends on the checkbox' state.
	 */
	@Override
	protected void setBehavior() {
		selected.addListener(invalidated -> pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, selected.get()));
		selected.bind(Bindings.createBooleanBinding(
				() -> {
					boolean contained = listView.getSelectionModel().getSelection().containsKey(index.get());
					checkbox.setSelected(contained);
					return contained;
				},
				listView.getSelectionModel().selectionProperty(), index
		));
		checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> updateSelection(newValue));
	}

	/**
	 * Responsible for rendering the cell's content.
	 * <p>
	 * If the given data type is a Node, it is added to the children list,
	 * otherwise a label is used to display the data.
	 * <p>
	 * At the end adds a ripple generator at index 0.
	 */
	@Override
	protected void render(T data) {
		if (data instanceof Node) {
			getChildren().setAll(rippleGenerator, checkbox, (Node) data);
		} else {
			getChildren().setAll(rippleGenerator, checkbox, label);
		}
	}

	/**
	 * Updates the data property of the cell. If the data is a Node
	 * {@link #render(Object)} is called.
	 * <p>
	 * This is called after {@link #updateIndex(int)}.
	 */
	@Override
	public void updateItem(T item) {
		super.updateItem(item);
		if (item instanceof Node) render(item);
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
