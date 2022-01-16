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
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableColumn.MFXTableColumnEvent;
import io.github.palexdev.materialfx.enums.SortState;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.DragResizer;
import io.github.palexdev.materialfx.utils.DragResizer.Direction;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.PositionUtils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.Comparator;

/**
 * This is the implementation of the {@code Skin} associated with every {@link MFXTableColumn}.
 * <p></p>
 * Simply an HBox with a label, an icon for sorting. It also has support for resizing the column on drag.
 */
public class MFXTableColumnSkin<T> extends SkinBase<MFXTableColumn<T>> {
	//================================================================================
	// Properties
	//================================================================================
	private final HBox container;
	private final BoundLabel label;
	private final MFXIconWrapper sortIcon;
	private final DragResizer dragResizer;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTableColumnSkin(MFXTableColumn<T> column) {
		super(column);

		label = new BoundLabel(column);
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		HBox.setHgrow(label, Priority.ALWAYS);

		sortIcon = new MFXIconWrapper("mfx-caret-up", 14, 18).defaultRippleGeneratorBehavior();
		NodeUtils.makeRegionCircular(sortIcon);

		container = new HBox(label);
		container.setMinWidth(Region.USE_PREF_SIZE);
		positionIcon(column.getAlignment());

		dragResizer = new DragResizer(column, Direction.RIGHT)
				.setWidthConstraintFunction(region ->
						region.snappedLeftInset() +
								region.prefWidth(-1) +
								region.snappedRightInset()
				);
		if (column.isColumnResizable()) dragResizer.makeResizable();

		getChildren().setAll(container);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Specifies the behavior for the following changes/events:
	 * <p> - the column's alignment, to position the icon, {@link #positionIcon(Pos)}
	 * <p> - the column's sort state, to animate the icon, {@link #animateIcon(SortState)}
	 * <p> - the column's resizable property, {@link MFXTableColumn#columnResizableProperty()}, to
	 * install/uninstall the {@link DragResizer}
	 * <p>
	 * It's also responsible for initializing the column if the initial sort state is not {@link SortState#UNSORTED},
	 * done by using a one-shot(no scope :D) listener {@link NodeUtils#waitForSkin(Control, Runnable, boolean, boolean)}.
	 */
	private void addListeners() {
		MFXTableColumn<T> column = getSkinnable();

		column.alignmentProperty().addListener((observable, oldValue, newValue) -> positionIcon(newValue));
		column.sortStateProperty().addListener((observable, oldValue, newValue) -> animateIcon(newValue));
		column.columnResizableProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				dragResizer.uninstall();
			} else {
				dragResizer.makeResizable();
			}
		});

		NodeUtils.waitForSkin(column, () -> {
			SortState sortState = column.getSortState();
			animateIcon(sortState);
			if (sortState == SortState.UNSORTED) return;

			Comparator<T> comparator = (sortState == SortState.DESCENDING) ? column.getComparator().reversed() : column.getComparator();
			column.fireEvent(new MFXTableColumnEvent<>(MFXTableColumnEvent.SORTING_EVENT, column, comparator, sortState));
		}, false, true);
	}

	/**
	 * Responsible for animating the icon according to the given sort state.
	 */
	private void animateIcon(SortState sortState) {
		Timeline animation = new Timeline();
		switch (sortState) {
			case ASCENDING: {
				sortIcon.setVisible(true);
				animation = MFXAnimationFactory.FADE_IN.build(sortIcon, 250);
				break;
			}
			case DESCENDING: {
				sortIcon.setVisible(true);
				KeyFrame kf = new KeyFrame(Duration.millis(150),
						new KeyValue(sortIcon.rotateProperty(), 180)
				);
				animation = new Timeline(kf);
				break;
			}
			case UNSORTED: {
				animation = MFXAnimationFactory.FADE_OUT.build(sortIcon, 250);
				animation.setOnFinished(event -> {
					sortIcon.setVisible(false);
					sortIcon.setRotate(0.0);
				});
				break;
			}
		}
		animation.play();
	}

	/**
	 * Responsible for positioning the icon according to the column's alignment.
	 * <p>
	 * Left if the column is right-aligned. Right if the column is left-aligned.
	 */
	private void positionIcon(Pos alignment) {
		container.getChildren().remove(sortIcon);
		if (PositionUtils.isRight(alignment)) {
			container.getChildren().add(0, sortIcon);
			return;
		}
		container.getChildren().add(sortIcon);
	}
}
