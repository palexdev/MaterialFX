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

import io.github.palexdev.materialfx.controls.base.AbstractMFXListView;
import io.github.palexdev.materialfx.effects.MFXDepthManager;
import io.github.palexdev.materialfx.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.animation.Animation;
import javafx.animation.KeyValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * Implementation of the {@code Skin} used by all list views based on VirtualizedFX.
 */
public class MFXListViewSkin<T> extends SkinBase<AbstractMFXListView<T, ?>> {
	//================================================================================
	// Properties
	//================================================================================
	private final ScrollBar hBar;
	private final ScrollBar vBar;
	private Animation hideBars;
	private Animation showBars;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXListViewSkin(AbstractMFXListView<T, ?> listView, SimpleVirtualFlow<T, ?> virtualFlow) {
		super(listView);
		hBar = virtualFlow.getHBar();
		vBar = virtualFlow.getVBar();
		virtualFlow.getStylesheets().setAll(listView.getUserAgentStylesheet());

		hideBars = AnimationUtils.TimelineBuilder.build()
				.add(
						AnimationUtils.KeyFrames.of(Duration.millis(400),
								new KeyValue(vBar.opacityProperty(), 0.0, MFXAnimationFactory.INTERPOLATOR_V1),
								new KeyValue(hBar.opacityProperty(), 0.0, MFXAnimationFactory.INTERPOLATOR_V1))
				)
				.getAnimation();
		showBars = AnimationUtils.TimelineBuilder.build()
				.add(
						AnimationUtils.KeyFrames.of(Duration.millis(400),
								new KeyValue(vBar.opacityProperty(), 1.0, MFXAnimationFactory.INTERPOLATOR_V1),
								new KeyValue(hBar.opacityProperty(), 1.0, MFXAnimationFactory.INTERPOLATOR_V1))
				)
				.getAnimation();

		if (listView.isHideScrollBars()) {
			vBar.setOpacity(0.0);
			hBar.setOpacity(0.0);
		}
		listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));

		getChildren().setAll(virtualFlow);
		setListeners();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Calls {@link #setScrollBarHandlers()}, adds a listener to the list view's depth property.
	 */
	private void setListeners() {
		AbstractMFXListView<T, ?> listView = getSkinnable();
		setScrollBarHandlers();
		listView.depthLevelProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.equals(oldValue)) {
				listView.setEffect(MFXDepthManager.shadowOf(listView.getDepthLevel()));
			}
		});

		listView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> listView.requestFocus());
	}

	/**
	 * Sets up the scroll bars behavior.
	 */
	private void setScrollBarHandlers() {
		AbstractMFXListView<T, ?> listView = getSkinnable();

		listView.setOnMouseExited(event -> {
			if (listView.isHideScrollBars()) {
				hideBars.setDelay(listView.getHideAfter());

				if (hBar.isPressed()) {
					hBar.pressedProperty().addListener(new ChangeListener<>() {
						@Override
						public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
							if (!newValue) {
								hideBars.play();
							}
							hBar.pressedProperty().removeListener(this);
						}
					});
					return;
				}

				if (vBar.isPressed()) {
					vBar.pressedProperty().addListener(new ChangeListener<>() {
						@Override
						public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
							if (!newValue) {
								hideBars.play();
							}
							vBar.pressedProperty().removeListener(this);
						}
					});
					return;
				}

				hideBars.play();
			}
		});

		listView.setOnMouseEntered(event -> {
			if (hideBars.getStatus().equals(Animation.Status.RUNNING)) {
				hideBars.stop();
			}
			showBars.play();
		});

		listView.hideScrollBarsProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				hideBars.play();
			} else {
				showBars.play();
			}
			if (newValue &&
					hideBars.getStatus() != Animation.Status.RUNNING ||
					vBar.getOpacity() != 0 ||
					hBar.getOpacity() != 0
			) {
				vBar.setOpacity(0.0);
				hBar.setOpacity(0.0);
			}
		});
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return topInset + 350 + bottomInset;
	}

	@Override
	protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return leftInset + 200 + rightInset;
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(width);
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(height);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (hideBars != null) {
			hideBars = null;
		}
		if (showBars != null) {
			showBars = null;
		}
	}
}
