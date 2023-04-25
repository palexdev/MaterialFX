/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package app.others.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.Collection;

public class TitledFlowPane extends VBox {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "titled-flow-pane";
	private final Label label;
	private final FlowPane pane;

	//================================================================================
	// Constructors
	//================================================================================
	public TitledFlowPane() {
		this("");
	}

	public TitledFlowPane(String title) {
		this.label = new Label(title);
		this.pane = new FlowPane();
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(30);
		pane.setHgap(30);

		label.getStyleClass().add("title");
		getStyleClass().add(STYLE_CLASS);

		super.getChildren().addAll(label, pane);
		setAlignment(Pos.TOP_CENTER);
		setSpacing(30);
	}

	//================================================================================
	// Methods
	//================================================================================
	public TitledFlowPane add(Node node) {
		pane.getChildren().add(node);
		return this;
	}

	public TitledFlowPane add(Node... nodes) {
		pane.getChildren().addAll(nodes);
		return this;
	}

	public TitledFlowPane add(Collection<Node> nodes) {
		pane.getChildren().addAll(nodes);
		return this;
	}

	public String getTitle() {
		return label.getText();
	}

	public TitledFlowPane setTitle(String title) {
		label.setText(title);
		return this;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}
}
