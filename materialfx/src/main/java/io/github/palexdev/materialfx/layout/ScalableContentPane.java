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

package io.github.palexdev.materialfx.layout;

import io.github.palexdev.materialfx.enums.ScaleBehavior;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

public class ScalableContentPane extends Region {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
	private final DoubleProperty minScaleX = new SimpleDoubleProperty(Double.MIN_VALUE);
	private final DoubleProperty maxScaleX = new SimpleDoubleProperty(Double.MAX_VALUE);
	private final DoubleProperty minScaleY = new SimpleDoubleProperty(Double.MIN_VALUE);
	private final DoubleProperty maxScaleY = new SimpleDoubleProperty(Double.MAX_VALUE);
	private final BooleanProperty fitToWidth = new SimpleBooleanProperty(true);
	private final BooleanProperty fitToHeight = new SimpleBooleanProperty(true);
	private final ObjectProperty<ScaleBehavior> scaleBehavior = new SimpleObjectProperty<>(ScaleBehavior.ALWAYS);

	private Scale scale;
	private double scaleWidth;
	private double scaleHeight;
	private boolean aspectScale = true;
	private boolean autoRescale = true;
	private boolean manualReset;

	//================================================================================
	// Constructors
	//================================================================================
	public ScalableContentPane() {
		this(new Pane());
	}

	public ScalableContentPane(Node content) {
		initialize();
		setContent(content);
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		needsLayoutProperty().addListener((observable, oldValue, newValue) -> {
			boolean wCondition = getWidth() <= getPrefWidth();
			boolean hCondition = getHeight() <= getPrefHeight();
			boolean prefWCondition = getPrefWidth() == USE_COMPUTED_SIZE;
			boolean prefHCondition = getPrefHeight() == USE_COMPUTED_SIZE;
			if (newValue && (wCondition || hCondition) || prefWCondition || prefHCondition) computeScale();
		});

		fitToWidth.addListener((observable, oldValue, newValue) -> requestLayout());
		fitToHeight.addListener((observable, oldValue, newValue) -> requestLayout());
		scaleBehavior.addListener((observable, oldValue, newValue) -> requestLayout());
		content.addListener((observable, oldValue, newValue) -> initializeContent());
	}

	private void initializeContent() {
		ChangeListener<? super Bounds> boundsListener = (observable, oldValue, newValue) -> {
			if (isAutoRescale()) {
				Node cnt = getContent();
				if (cnt instanceof Region) {
					Region rgn = (Region) cnt;
					rgn.requestLayout();
				}
				requestLayout();
				setNeedsLayout(false);
			}
		};

		ChangeListener<? super Number> layoutListener = (observable, oldValue, newValue) -> {
			if (isAutoRescale()) {
				Node cnt = getContent();
				if (cnt instanceof Parent) {
					Parent prt = (Parent) cnt;
					prt.requestLayout();
				}
				requestLayout();
				setNeedsLayout(false);
			}
		};

		Node content = getContent();
		if (getContent() instanceof Pane) {
			Pane pane = (Pane) content;
			ListChangeListener<? super Node> childrenListener = c -> {
				while (c.next()) {
					if (c.wasRemoved()) {
						for (Node node : c.getRemoved()) {
							node.boundsInLocalProperty().removeListener(boundsListener);
							node.layoutXProperty().removeListener(layoutListener);
							node.layoutYProperty().removeListener(layoutListener);
						}
					} else if (c.wasAdded()) {
						for (Node node : c.getAddedSubList()) {
							node.boundsInLocalProperty().addListener(boundsListener);
							node.layoutXProperty().addListener(layoutListener);
							node.layoutYProperty().addListener(layoutListener);
						}
					}
				}
			};
			pane.getChildren().addListener(childrenListener);
		}

		scale = Transform.scale(1, 1, 0, 0);
		content.getTransforms().add(scale);
		getChildren().add(content);

		ChangeListener<? super Number> scaleListener = (observable, oldValue, newValue) -> requestScale();
		scale.setOnTransformChanged(event -> {
			requestLayout();
			setNeedsLayout(false);
		});

		minScaleX.addListener(scaleListener);
		minScaleY.addListener(scaleListener);
		maxScaleX.addListener(scaleListener);
		maxScaleY.addListener(scaleListener);
	}

	private void computeScale() {
		double realWidth = getContent().prefWidth(getLayoutBounds().getHeight());
		double realHeight = getContent().prefHeight(getLayoutBounds().getWidth());
		double leftAndRight = getInsets().getLeft() + getInsets().getRight();
		double topAndBottom = getInsets().getTop() + getInsets().getBottom();

		double contentWidth = getLayoutBounds().getWidth() - leftAndRight;
		double contentHeight = getLayoutBounds().getHeight() - topAndBottom;

		scaleWidth = contentWidth / realWidth;
		scaleHeight = contentHeight / realHeight;

		scaleWidth = Math.max(scaleWidth, getMinScaleX());
		scaleWidth = Math.min(scaleWidth, getMaxScaleX());

		scaleHeight = Math.max(scaleHeight, getMinScaleY());
		scaleHeight = Math.min(scaleHeight, getMaxScaleY());

		double resizeScaleW;
		double resizeScaleH;

		if (isAspectScale()) {
			double scaleValue = Math.min(scaleWidth, scaleHeight);

			if (getScaleBehavior() == ScaleBehavior.ALWAYS || manualReset) {
				scale.setX(scaleValue);
				scale.setY(scaleValue);
			} else if (getScaleBehavior() == ScaleBehavior.IF_NECESSARY) {
				if (scaleValue < scale.getX() && getLayoutBounds().getWidth() > 0) {
					scale.setX(scaleValue);
					scale.setY(scaleValue);
				}
			}

		} else if (getScaleBehavior() == ScaleBehavior.ALWAYS || manualReset) {
			scale.setX(scaleWidth);
			scale.setY(scaleHeight);
		} else if (getScaleBehavior() == ScaleBehavior.IF_NECESSARY) {
			if (scaleWidth < scale.getX() && getLayoutBounds().getWidth() > 0) {
				scale.setX(scaleWidth);
			}
			if (scaleHeight < scale.getY() && getLayoutBounds().getHeight() > 0) {
				scale.setY(scaleHeight);
			}
		}

		resizeScaleW = scale.getX();
		resizeScaleH = scale.getY();

		getContent().relocate(getInsets().getLeft(), getInsets().getTop());

		double realContentWidth;
		double realContentHeight;

		if (isFitToWidth()) {
			realContentWidth = contentWidth / resizeScaleW;
		} else {
			realContentWidth = contentWidth / scaleWidth;
		}

		if (isFitToHeight()) {
			realContentHeight = contentHeight / resizeScaleH;
		} else {
			realContentHeight = contentHeight / scaleHeight;
		}

		getContent().resize(realContentWidth, realContentHeight);
	}

	public void requestScale() {
		computeScale();
	}

	public void resetScale() {
		if (manualReset) {
			return;
		}

		manualReset = true;

		try {
			computeScale();
		} finally {
			manualReset = false;
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMinWidth(double height) {
		double result = getInsets().getLeft() + getInsets().getRight();

		// apply content width (including scale)
		result += getContent().prefWidth(height) * getMinScaleX();
		return result;
	}

	@Override
	protected double computeMinHeight(double width) {
		double result = getInsets().getTop() + getInsets().getBottom();

		// apply content width (including scale)
		result += getContent().prefHeight(width) * getMinScaleY();
		return result;
	}

	@Override
	protected double computePrefWidth(double height) {
		double result = getInsets().getLeft() + getInsets().getRight();

		// apply content width (including scale)
		result += getContent().prefWidth(height) * scaleWidth;
		return result;
	}

	@Override
	protected double computePrefHeight(double width) {
		double result = getInsets().getTop() + getInsets().getBottom();

		// apply content width (including scale)
		result += getContent().prefHeight(width) * scaleHeight;
		return result;
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		computeScale();
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Node getContent() {
		return content.get();
	}

	public ObjectProperty<Node> contentProperty() {
		return content;
	}

	public void setContent(Node content) {
		this.content.set(content);
	}

	public double getMinScaleX() {
		return minScaleX.get();
	}

	public DoubleProperty minScaleXProperty() {
		return minScaleX;
	}

	public void setMinScaleX(double minScaleX) {
		this.minScaleX.set(minScaleX);
	}

	public double getMaxScaleX() {
		return maxScaleX.get();
	}

	public DoubleProperty maxScaleXProperty() {
		return maxScaleX;
	}

	public void setMaxScaleX(double maxScaleX) {
		this.maxScaleX.set(maxScaleX);
	}

	public double getMinScaleY() {
		return minScaleY.get();
	}

	public DoubleProperty minScaleYProperty() {
		return minScaleY;
	}

	public void setMinScaleY(double minScaleY) {
		this.minScaleY.set(minScaleY);
	}

	public double getMaxScaleY() {
		return maxScaleY.get();
	}

	public DoubleProperty maxScaleYProperty() {
		return maxScaleY;
	}

	public void setMaxScaleY(double maxScaleY) {
		this.maxScaleY.set(maxScaleY);
	}

	public boolean isFitToWidth() {
		return fitToWidth.get();
	}

	public BooleanProperty fitToWidthProperty() {
		return fitToWidth;
	}

	public void setFitToWidth(boolean fitToWidth) {
		this.fitToWidth.set(fitToWidth);
	}

	public boolean isFitToHeight() {
		return fitToHeight.get();
	}

	public BooleanProperty fitToHeightProperty() {
		return fitToHeight;
	}

	public void setFitToHeight(boolean fitToHeight) {
		this.fitToHeight.set(fitToHeight);
	}

	public ScaleBehavior getScaleBehavior() {
		return scaleBehavior.get();
	}

	public ObjectProperty<ScaleBehavior> scaleBehaviorProperty() {
		return scaleBehavior;
	}

	public void setScaleBehavior(ScaleBehavior scaleBehavior) {
		this.scaleBehavior.set(scaleBehavior);
	}

	public Scale getScale() {
		return scale;
	}

	public boolean isAspectScale() {
		return aspectScale;
	}

	public void setAspectScale(boolean aspectScale) {
		this.aspectScale = aspectScale;
	}

	public boolean isAutoRescale() {
		return autoRescale;
	}

	public void setAutoRescale(boolean autoRescale) {
		this.autoRescale = autoRescale;
	}
}
