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

import io.github.palexdev.materialfx.controls.MFXPaginatedTableView;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.MFXTableRow;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * This is the default skin implementation for {@link MFXPaginatedTableView}.
 * <p></p>
 * Extends {@link MFXTableViewSkin} and just modifies the footer node to add a
 * {@link MFXPagination} control to it, responsible for changing the current page.
 * <p></p>
 * Little side note as a reminder too:
 * <p>
 * The layoutChildren(...) method has been overridden because it's the best place to properly
 * initialize the virtual flow minHeight property, since it's needed to wait until the cellHeight
 * is retrieved. It's also needed to move the viewport to the current page (if not 1), and for
 * some reason it's needed to delay the update with a {@link PauseTransition} otherwise an exception
 * is thrown.
 */
public class MFXPaginatedTableViewSkin<T> extends MFXTableViewSkin<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXPagination pagination;
	private boolean init = false;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPaginatedTableViewSkin(MFXPaginatedTableView<T> tableView, SimpleVirtualFlow<T, MFXTableRow<T>> rowsFlow) {
		super(tableView, rowsFlow);

		rowsFlow.setMinHeight(Region.USE_PREF_SIZE);
		rowsFlow.setMaxHeight(Region.USE_PREF_SIZE);

		pagination = new MFXPagination();
		pagination.pagesToShowProperty().bind(tableView.pagesToShowProperty());
		pagination.maxPageProperty().bind(tableView.maxPageProperty());
		pagination.setCurrentPage(tableView.getCurrentPage());
		tableView.currentPageProperty().bindBidirectional(pagination.currentPageProperty());

		container.getChildren().remove(footer);
		container.getChildren().add(buildFooter());

		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXPaginatedTableView<T> tableView = (MFXPaginatedTableView<T>) getSkinnable();
		tableView.virtualFlowInitializedProperty().addListener((observable, oldValue, newValue) -> {
			if (!init && newValue) {
				rowsFlow.prefHeightProperty().bind(Bindings.createDoubleBinding(
						() -> tableView.getRowsPerPage() * rowsFlow.getCellHeight(),
						tableView.rowsPerPageProperty()
				));

				int current = tableView.getCurrentPage();
				if (current != 1) {
					PauseBuilder.build()
							.setDuration(20)
							.setOnFinished(event -> tableView.goToPage(current))
							.getAnimation().play();
				}

				init = true;
			}
		});
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected StackPane buildFooter() {
		StackPane footer = super.buildFooter();
		if (pagination == null) return footer;
		footer.getChildren().add(pagination);
		StackPane.setAlignment(pagination, Pos.CENTER);
		return footer;
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		double footerWidth = leftInset + footer.prefWidth(-1) + pagination.prefWidth(-1) * 2 + 10 + rightInset;
		return Math.max(footerWidth, super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset));
	}
}
