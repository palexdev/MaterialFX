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

import io.github.palexdev.materialfx.controls.MFXTitledPane;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.HeaderPosition;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Default skin implementation used by {@link MFXTitledPane}.
 * <p></p>
 * It consists in three main nodes:
 * <p> - A {@link BorderPane} which is the top container, automatically manages the layout
 * for use making things way easier. It is also very convenient for the {@link MFXTitledPane#headerPosProperty()} feature,
 * see {@link #updatePane()}.
 * <p> - A {@link StackPane} which contains the {@link MFXTitledPane#contentProperty()}
 * <p> - A {@link Rectangle} to clip the above {@code StackPane}
 * <p></p>
 * Since the header position can change, the whole system needs to change as well. What I mean, is:
 * if the header is at the TOP or BOTTOM then we want to work with the content pane's prefHeight,
 * otherwise we want to work with the content pane's prefWidth.
 * This deeply influences both the clip and the expand/collapse code.
 * <p></p>
 * For this reason we use a smart system with three functions:
 * <p> - A {@link Supplier} which gives us the content pane's prefWidth/prefHeight property (sizeSupplier)
 * <p> - A {@link Supplier} which gives us the content's prefWidth/prefHeight (targetSizeSupplier)
 * <p> - A {@link Consumer} which accepts a target prefWidth/prefHeight and sets it on the content pane (setter)
 */
public class MFXTitledPaneSkin extends SkinBase<MFXTitledPane> {
	//================================================================================
	// Properties
	//================================================================================
	private final BorderPane bp;
	private final StackPane contentPane;
	private final Rectangle clip;

	private Node header;
	private Node content;
	private Supplier<DoubleProperty> sizeSupplier;
	private Supplier<Double> targetSizeSupplier;
	private Consumer<Double> setter;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTitledPaneSkin(MFXTitledPane pane) {
		super(pane);

		header = pane.getHeaderSupplier().get();
		content = pane.getContent();

		contentPane = new StackPane();
		contentPane.getStyleClass().add("content-pane");
		if (content != null) contentPane.getChildren().add(content);

		clip = new Rectangle();
		clip();

		bp = new BorderPane();
		bp.setCenter(contentPane);
		updateSuppliers();
		updatePane();
		getChildren().setAll(bp);

		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Adds the following listeners/handlers:
	 * <p> - A listener to the {@link MFXTitledPane#expandedProperty()} to call {@link #expandCollapse()}
	 * <p> - A listener to the {@link MFXTitledPane#collapsibleProperty()} to call {@link #expandCollapse()}
	 * <p> - A listener to the {@link MFXTitledPane#headerSupplierProperty()} to call {@link #updatePane()}
	 * <p> - A listener to the {@link MFXTitledPane#contentProperty()} to update the content pane and call {@link #expandCollapse()}
	 * <p> - A listener to the {@link MFXTitledPane#headerPosProperty()} to call {@link #updateSuppliers()}, {@link #clip()}, {@link #updatePane()} and {@link #expandCollapse()}
	 * <p> - A MouseEvent.MOUSE_PRESSED event handler to acquire focus
	 * <p></p>
	 * There's also a call to {@link ExecutionUtils#executeWhen(ObservableValue, BiConsumer, boolean, BiFunction, boolean)},
	 * which triggers when the content pane is laid out and calls {@link #expandCollapse()} to initialize the control.
	 */
	private void addListeners() {
		MFXTitledPane pane = getSkinnable();

		pane.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> pane.requestFocus());

		pane.expandedProperty().addListener(invalidated -> expandCollapse());
		pane.collapsibleProperty().addListener(invalidated -> expandCollapse());
		pane.headerSupplierProperty().addListener((observable, oldValue, newValue) -> {
			bp.getChildren().remove(header);
			header = newValue.get();
			updatePane();
		});
		pane.contentProperty().addListener((observable, oldValue, newValue) -> {
			content = newValue;
			if (newValue != null) contentPane.getChildren().setAll(newValue);
			expandCollapse();
		});
		pane.headerPosProperty().addListener(invalidated -> {
			updateSuppliers();
			clip();
			updatePane();
			expandCollapse();
		});

		ExecutionUtils.executeWhen(
				contentPane.layoutBoundsProperty(),
				(oldValue, newValue) -> expandCollapse(),
				false,
				(oldValue, newValue) -> newValue.getWidth() != 0 || newValue.getHeight() != 0,
				true
		);
	}

	/**
	 * Responsible for updating the sizes' functions when {@link MFXTitledPane#headerPosProperty()} changes.
	 * <p></p>
	 * Case RIGHT, LEFT:
	 * <p> - Pref Height set to {@link Region#USE_COMPUTED_SIZE}, sizeSupplier set to {@code contentPane::prefWidthProperty},
	 * targetSizeSupplier set to {@code content.prefWidth(-1)} (plus insets), setter set to {@code contentPane::setPrefWidth}
	 * Case TOP, BOTTOM:
	 * <p> - Pref Width set to {@link Region#USE_COMPUTED_SIZE}, sizeSupplier set to {@code contentPane::prefHeightProperty},
	 * targetSizeSupplier set to {@code content.prefHeight(-1)} (plus insets), setter set to {@code contentPane::setPrefHeight}
	 */
	private void updateSuppliers() {
		MFXTitledPane pane = getSkinnable();
		HeaderPosition position = pane.getHeaderPos();
		if (position == HeaderPosition.RIGHT || position == HeaderPosition.LEFT) {
			contentPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
			sizeSupplier = contentPane::prefWidthProperty;
			targetSizeSupplier = () -> contentPane.snappedLeftInset() + content.prefWidth(-1) + contentPane.snappedRightInset();
			setter = contentPane::setPrefWidth;
		} else {
			contentPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
			sizeSupplier = contentPane::prefHeightProperty;
			targetSizeSupplier = () -> contentPane.snappedTopInset() + content.prefHeight(-1) + contentPane.snappedBottomInset();
			setter = contentPane::setPrefHeight;
		}
	}

	/**
	 * Responsible for updating the header position in the {@link BorderPane}
	 * according to {@link MFXTitledPane#headerPosProperty()}.
	 */
	private void updatePane() {
		MFXTitledPane pane = getSkinnable();
		HeaderPosition position = pane.getHeaderPos();

		switch (position) {
			case RIGHT:
				bp.setRight(header);
				contentPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
				break;
			case BOTTOM:
				bp.setBottom(header);
				contentPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE);
				break;
			case LEFT:
				bp.setLeft(header);
				contentPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_COMPUTED_SIZE);
				break;
			case TOP:
			default:
				bp.setTop(header);
				contentPane.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_PREF_SIZE);
		}
	}

	/**
	 * Responsible for expanding/collapsing the content pane according to:
	 * {@link MFXTitledPane#expandedProperty()}, {@link MFXTitledPane#collapsibleProperty()}, {@link MFXTitledPane#animatedProperty()}.
	 * <p></p>
	 * If the content is null the size is set to 0
	 * <p>
	 * If it's not expanded and it's not collapsible the size is set to {@code targetSizeSupplier.get()} and returns immediately.
	 * <p></p>
	 * Otherwise, the target size is computed according to the expand state, and the size is then set directly or by an animation,
	 * the opacity is also animated.
	 */
	private void expandCollapse() {
		if (content == null) {
			setter.accept(0.0);
			return;
		}

		MFXTitledPane pane = getSkinnable();
		boolean isExpanded = pane.isExpanded();
		if (!isExpanded && !pane.isCollapsible()) {
			setter.accept(targetSizeSupplier.get());
			return;
		}

		DoubleProperty property = sizeSupplier.get();
		double targetSize = isExpanded ? targetSizeSupplier.get() : 0;
		double targetOp = isExpanded ? 1.0 : 0.0;
		if (pane.isAnimated()) {
			TimelineBuilder.build()
					.add(KeyFrames.of(pane.getAnimationDuration(), contentPane.opacityProperty(), targetOp, Interpolators.INTERPOLATOR_V1))
					.add(KeyFrames.of(pane.getAnimationDuration(), property, targetSize, Interpolators.INTERPOLATOR_V1))
					.getAnimation()
					.play();
		} else {
			contentPane.setOpacity(targetOp);
			setter.accept(targetSize);
		}
	}

	/**
	 * Responsible for clipping the content pane according to the
	 * {@link MFXTitledPane#headerPosProperty()}.
	 */
	private void clip() {
		MFXTitledPane pane = getSkinnable();
		HeaderPosition position = pane.getHeaderPos();

		contentPane.setClip(null);
		if (position == HeaderPosition.RIGHT || position == HeaderPosition.LEFT) {
			clip.widthProperty().bind(contentPane.prefWidthProperty());
			clip.heightProperty().bind(pane.heightProperty());
		} else {
			clip.widthProperty().bind(pane.widthProperty());
			clip.heightProperty().bind(contentPane.prefHeightProperty());
		}
		contentPane.setClip(clip);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		HeaderPosition position = getSkinnable().getHeaderPos();
		if (position == HeaderPosition.RIGHT || position == HeaderPosition.LEFT) {
			return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
		}
		return super.computeMaxWidth(height, topInset, rightInset, bottomInset, leftInset);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		HeaderPosition position = getSkinnable().getHeaderPos();
		if (position == HeaderPosition.TOP || position == HeaderPosition.BOTTOM) {
			return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
		}
		return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
	}
}
