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

package io.github.palexdev.materialfx.notifications;

import io.github.palexdev.materialfx.beans.CustomBounds;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.TransitionPositionBean;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.effects.ConsumerTransition;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.base.AbstractMFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.notifications.base.INotificationSystem;
import io.github.palexdev.materialfx.utils.AnimationUtils.ParallelBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import static io.github.palexdev.materialfx.utils.PositionUtils.*;

/**
 * Implementation of an {@link AbstractMFXNotificationSystem} which makes use of a {@link MFXNotificationCenter}
 * to show the notifications.
 */
public class MFXNotificationCenterSystem extends AbstractMFXNotificationSystem {
	//================================================================================
	// Instance
	//================================================================================
	private final static MFXNotificationCenterSystem instance = new MFXNotificationCenterSystem();

	public static MFXNotificationCenterSystem instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final MFXNotificationCenter center;
	private boolean openOnNew = true;
	private boolean centerInit = false;

	//================================================================================
	// Constructors
	//================================================================================
	private MFXNotificationCenterSystem() {
		super();
		center = new MFXNotificationCenter();
		popup.setContent(center);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * This method must be called before the notification system can be used.
	 * <p>
	 * Also calls {@link #dispose()} before initializing.
	 */
	@Override
	public MFXNotificationCenterSystem initOwner(Window owner) {
		dispose();
		super.owner = owner;
		owner.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, onClose);
		center.setOpacity(0.0);
		init();
		if (!dummyStage.isShowing()) dummyStage.show();
		popup.show(dummyStage);
		return this;
	}

	/**
	 * Initializes the popup's position for the specified {@link NotificationPos}.
	 */
	@Override
	protected void init() {
		PositionBean positionBean = computePosition();
		popup.setX(positionBean.getX());
		popup.setY(positionBean.getY());

		if (!centerInit) {
			center.setOnIconClicked(event -> {
				if (!isShowing() && !center.getNotifications().isEmpty()) {
					show();
				}
			});
			center.showingProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue) close();
			});

			center.popupHoverProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue) {
					closeAfterTransition.stop();
				} else if (closeAutomatically) {
					closeAfterTransition.playFromStart();
				}
			});
			center.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> closeAfterTransition.stop());
			center.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
				if (closeAutomatically) closeAfterTransition.playFromStart();
			});
			centerInit = true;
		}
	}

	/**
	 * If the notification system is closing, exits and {@link #scheduleReopen(INotification)} is called.
	 * <p>
	 * Adds the notification to the {@link MFXNotificationCenter} then if the notification system is set
	 * to close automatically starts the close {@link PauseTransition}.
	 * <p>
	 * If the notification center is in "Do not disturb mode" exits immediately otherwise
	 * shows the popup (the content is still hidden tough so it's not really open), then shown
	 * the bell icon and if {@link #isOpenOnNew()} is true, {@link #show()} is called.
	 *
	 * @throws IllegalStateException if the notification system has not been initialized
	 */
	@Override
	public INotificationSystem publish(INotification notification) {
		if (notification == null) return this;

		if (owner == null) {
			throw new IllegalStateException("The NotificationSystem has not been initialized!");
		} else if (isClosing()) {
			scheduleReopen(notification);
		} else {
			center.getNotifications().add(notification);
			if (closeAutomatically) {
				closeAfterTransition.playFromStart();
			}

			if (!isShowing() && !center.isDoNotDisturb()) {
				init();
				popup.show(dummyStage);

				if (animated) {
					TimelineBuilder.build().show(250, center).getAnimation().play();
				} else {
					center.setOpacity(1.0);
				}
				if (openOnNew) {
					show();
				}
			}
		}
		return this;
	}

	/**
	 * Sets the showing property to true, computes the popup position as a {@link TransitionPositionBean},
	 * then positions the popup (animated or not), and tells the notification center to open.
	 */
	@Override
	protected void show() {
		showing.set(true);
		TransitionPositionBean positionBean = computePosition();
		double x = positionBean.getX();
		double y = positionBean.getY();
		double deltaX = positionBean.deltaX();
		double deltaY = positionBean.deltaY();

		if (animated) {
			ParallelBuilder.build()
					.add(ConsumerTransition.of(frac -> popup.setX(x - deltaX * frac), 250, Interpolators.INTERPOLATOR_V2))
					.add(ConsumerTransition.of(frac -> popup.setY(y - deltaY * frac), 250, Interpolators.INTERPOLATOR_V2))
					.setOnFinished(event -> center.setShowing(true))
					.getAnimation()
					.play();
		} else {
			popup.setX(x - deltaX);
			popup.setY(y - deltaY);
			center.setShowing(true);
		}
	}

	/**
	 * Sets the closing property to true, immediately closes the notification center,
	 * computes the popup's position as a {@link TransitionPositionBean} and then sets the popup's
	 * coordinates (animated or not).
	 * At the end always hides the popup, and resets the showing/closing properties.
	 */
	@Override
	protected void close() {
		closing.set(true);
		center.setShowing(false);

		TransitionPositionBean positionBean = computePosition();
		double x = popup.getX();
		double y = popup.getY();
		double deltaX = positionBean.deltaX();
		double deltaY = positionBean.deltaY();

		if (animated) {
			ParallelBuilder.build()
					.setDelay(100)
					.hide(400, center)
					.add(ConsumerTransition.of(frac -> popup.setX(x + deltaX * frac), 250, Interpolators.INTERPOLATOR_V2))
					.add(ConsumerTransition.of(frac -> popup.setY(y + deltaY * frac), 250, Interpolators.INTERPOLATOR_V2))
					.setOnFinished(event -> {
						popup.hide();
						closing.reset();
						showing.reset();
					})
					.getAnimation().play();
		} else {
			PauseBuilder.build().setDuration(30).setOnFinished((event) -> {
				popup.setX(x + deltaX);
				popup.setY(y + deltaY);
				center.setOpacity(0);
				popup.hide();
				showing.reset();
				closing.reset();
			}).getAnimation().play();
		}
	}

	/**
	 * Adds a one-time listener to the closing property so that when it becomes
	 * false the notification that could not be shown will be sent to {@link #publish(INotification)} again.
	 */
	@Override
	protected void scheduleReopen(INotification notification) {
		ExecutionUtils.executeWhen(
				closing,
				(oldValue, newValue) -> publish(notification),
				false,
				(oldValue, newValue) -> !newValue,
				true
		);
	}

	/**
	 * Computes the position of the popup as a {@link TransitionPositionBean} to be used in animations too.
	 */
	@Override
	protected TransitionPositionBean computePosition() {
		double x;
		double y;
		double endX;
		double endY;

		Rectangle2D screenBounds = screen.getVisualBounds();
		CustomBounds centerBounds = getBounds();
		double counterWidth = centerBounds.getMaxX() - centerBounds.getMinX();

		if (isTop(position)) {
			y = screenBounds.getMinY() + spacing.getTop();
			endY = y;
		} else {
			y = screenBounds.getMaxY() - centerBounds.getMaxY() - spacing.getBottom();
			endY = screenBounds.getMaxY() - centerBounds.getHeight() - spacing.getBottom();
		}

		if (isCenter(position)) {
			x = (screenBounds.getMaxX() / 2) - (counterWidth / 2);
			endX = x;
		} else if (isRight(position)) {
			x = screenBounds.getMaxX() - counterWidth - spacing.getRight();
			endX = screenBounds.getMaxX() - centerBounds.getMaxX() - spacing.getRight();
		} else {
			x = screenBounds.getMinX() + spacing.getLeft();
			endX = centerBounds.getMinX() + spacing.getLeft();
		}
		return TransitionPositionBean.of(x, y, endX, endY);
	}

	@Override
	public INotificationSystem dispose() {
		if (super.owner != null) {
			super.owner.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, onClose);
			super.owner = null;
		}
		if (dummyStage.isShowing()) {
			dummyStage.close();
		}
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Computes the bounds of the notification center.
	 * Custom bounds are needed since we need to take into account the bell icon AND the popup.
	 */
	private CustomBounds getBounds() {
		Bounds bounds = center.getLayoutBounds();
		double width = center.getPopupWidth();
		double height = bounds.getHeight() + center.getPopupHeight() + center.getPopupSpacing();
		double minX = (width / 2) - (bounds.getWidth() / 2);
		double maxX = minX + bounds.getWidth();
		return new CustomBounds(
				minX,
				bounds.getMinY(),
				maxX,
				bounds.getMaxY(),
				width,
				height
		);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Returns the instance of the notification center used by this notification system.
	 * <p></p>
	 * <b>WARN:</b> do not mess around too much with it since the notification system is highly
	 * dependent on the notification center. For this reason I also warn you that
	 * this method could be deprecated and removed in the future, maybe replaced by delegates methods
	 * to expose only a few features.
	 */
	public MFXNotificationCenter getCenter() {
		return center;
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * Overridden to also enable/disable the notification center's animations.
	 */
	@Override
	public AbstractMFXNotificationSystem setAnimated(boolean animated) {
		this.animated = animated;
		center.setAnimated(animated);
		return this;
	}

	/**
	 * Specifies if the notification center should be opened when a new notification is sent.
	 */
	public boolean isOpenOnNew() {
		return openOnNew;
	}

	public MFXNotificationCenterSystem setOpenOnNew(boolean openOnNew) {
		this.openOnNew = openOnNew;
		return this;
	}
}
