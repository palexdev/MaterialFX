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

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXPagination;
import io.github.palexdev.materialfx.controls.cell.MFXPage;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the default skin implementation for {@link MFXPagination}.
 * <p>
 * The top container and the pane containing the pages are dynamically built
 * and replaced accordingly to the current {@link MFXPagination#orientationProperty()}.
 * <p>
 * The skin is also responsible for building and managing the pages.
 */
public class MFXPaginationSkin extends SkinBase<MFXPagination> {
	//================================================================================
	// Properties
	//================================================================================
	private Pane container;
	private final MFXIconWrapper leftArrow;
	private final MFXIconWrapper rightArrow;
	private Pane bar;

	private final ObservableList<MFXPage> pages = FXCollections.observableArrayList();

	//================================================================================
	// Constructors
	//================================================================================
	public MFXPaginationSkin(MFXPagination pagination) {
		super(pagination);

		leftArrow = new MFXIconWrapper("mfx-chevron-left", 12, 32).defaultRippleGeneratorBehavior();
		rightArrow = new MFXIconWrapper("mfx-chevron-right", 12, 32).defaultRippleGeneratorBehavior();

		NodeUtils.makeRegionCircular(leftArrow);
		NodeUtils.makeRegionCircular(rightArrow);

		leftArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> pagination.setCurrentPage(pagination.getCurrentPage() - 1));
		rightArrow.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> pagination.setCurrentPage(pagination.getCurrentPage() + 1));

		buildBar();
		buildPages();
		Bindings.bindContent(bar.getChildren(), pages);

		container = buildContainer();
		addListeners();
		getChildren().setAll(container);
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Specifies the behavior for the following changes/events:
	 * <p> - rebuilds the pages when the cell factory changes
	 * <p> - updates the pages when the current page changes
	 * <p> - rebuilds the pages when the max page property changes
	 * <p> - rebuilds the pages when the pages algorithm changes
	 * <p> - rebuilds the pages when the ellipse property changes
	 * <p> - updates the layout and updates the cells when the orientation changes
	 * <p> - calls {@link #updateBetweens()} if the {@link MFXPagination#showPopupForTruncatedPagesProperty()} is set to true
	 */
	private void addListeners() {
		MFXPagination pagination = getSkinnable();

		pagination.pageCellFactoryProperty().addListener(invalidated -> {
			pages.clear();
			buildPages();
		});

		pagination.currentPageProperty().addListener(invalidated -> buildPages());
		pagination.maxPageProperty().addListener(invalidated -> {
			buildPages();
			int current = pagination.getCurrentPage();
			int max = pagination.getMaxPage();
			if (max > current) pagination.setCurrentPage(max);
		});
		pagination.indexesSupplierProperty().addListener(invalidated -> buildPages());

		pagination.ellipseStringProperty().addListener(invalidated -> {
			pages.clear();
			buildPages();
		});

		pagination.orientationProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Orientation.HORIZONTAL) {
				leftArrow.setRotate(0);
				rightArrow.setRotate(0);
			} else {
				leftArrow.setRotate(90);
				rightArrow.setRotate(90);
			}
			pages.clear();
			buildBar();
			buildPages();
			container = buildContainer();
			getChildren().setAll(container);
		});

		pagination.showPopupForTruncatedPagesProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) updateBetweens();
		});
	}

	/**
	 * Responsible for building the top container according to the
	 * pagination's orientation.
	 */
	protected Pane buildContainer() {
		MFXPagination pagination = getSkinnable();
		if (pagination.getOrientation() == Orientation.HORIZONTAL) {
			HBox box = new HBox(5, leftArrow, bar, rightArrow);
			box.setAlignment(Pos.CENTER);
			return box;
		} else {
			VBox box = new VBox(5, leftArrow, bar, rightArrow);
			box.setAlignment(Pos.CENTER);
			return box;
		}
	}

	/**
	 * Responsible for building the pages' container according to the
	 * pagination's orientation.
	 */
	protected void buildBar() {
		MFXPagination pagination = getSkinnable();
		if (pagination.getOrientation() == Orientation.HORIZONTAL) {
			bar = new HBox(5);
		} else {
			bar = new VBox(5);
		}
		bar.getStyleClass().add("pages-bar");
	}

	/**
	 * Responsible for building the pages or updating them if re-building them is not needed.
	 * <p>
	 * Also, if the pages list is not empty, and to improve performance, if more or less pages
	 * are needed {@link #supplyPages(int)} is used.
	 */
	protected void buildPages() {
		MFXPagination pagination = getSkinnable();
		if (!pages.isEmpty()) {
			if (pages.size() != pagination.getMaxPage()) supplyPages(pagination.getMaxPage() - pages.size());
			updatePages();
			updateBetweens();
			return;
		}

		supplyPages(Math.min(pagination.getMaxPage(), pagination.getPagesToShow()));
		updatePages();
		updateBetweens();
	}

	/**
	 * Builds additional pages according to the given number.
	 * <p>
	 * If it is less than 0, then pages are removed.
	 */
	protected void supplyPages(int num) {
		MFXPagination pagination = getSkinnable();
		List<MFXPage> list = new ArrayList<>();
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				list.add(pagination.getPageCellFactory().apply((i + 1)));
			}
			pages.addAll(list);
		} else {
			int i = 1;
			int toRemove = Math.abs(num);
			while (toRemove > 0) {
				MFXPage page = pages.get(pages.size() - i);
				list.add(page);
				toRemove--;
				i++;
			}
			pages.removeAll(list);
		}
	}

	/**
	 * Computes the indexes list using {@link MFXPagination#indexesSupplierProperty()}
	 * and updates the built pages using {@link MFXPage#updateItem(Integer)}.
	 */
	protected void updatePages() {
		MFXPagination pagination = getSkinnable();
		List<Integer> indexes = pagination.getIndexesSupplier().get();
		for (int i = 0; i < indexes.size(); i++) {
			MFXPage page = pages.get(i);
			page.updateItem(indexes.get(i));
		}
	}

	/**
	 * All {@link MFXPage}s have a {@link NumberRange} field that specifies the range of truncated pages.
	 * <p>
	 * Truncated pages use this property to show a popup containing the list of hidden pages, making the navigation faster and easier.
	 * <p>
	 * This method is responsible for properly updating such property.
	 */
	protected void updateBetweens() {
		if (!getSkinnable().isShowPopupForTruncatedPages()) return;
		for (int i = 0; i < pages.size(); i++) {
			MFXPage page = pages.get(i);
			if (page.getIndex() == -1) {
				MFXPage pageBefore = pages.get(i - 1);
				MFXPage pageAfter = pages.get(i + 1);
				page.setBetween(NumberRange.of(pageBefore.getIndex() + 1, pageAfter.getIndex() - 1));
			} else {
				page.setBetween(null);
			}
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
