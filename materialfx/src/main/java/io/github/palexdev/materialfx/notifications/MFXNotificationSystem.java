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

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.TransitionPositionBean;
import io.github.palexdev.materialfx.collections.CircularQueue;
import io.github.palexdev.materialfx.effects.ConsumerTransition;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.NotificationPos;
import io.github.palexdev.materialfx.notifications.base.AbstractMFXNotificationSystem;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.notifications.base.INotificationSystem;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.ParallelBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.PauseBuilder;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import io.github.palexdev.materialfx.utils.ExecutionUtils;
import javafx.animation.PauseTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static io.github.palexdev.materialfx.utils.PositionUtils.*;

/**
 * Simple implementation of an {@link AbstractMFXNotificationSystem} which makes use of
 * a {@link CircularQueue} to keep a history of the shown notifications (by default max size is 100),
 * and a list to keep a reference to queued notifications that can't be shown at the moment of {@link #publish(INotification)}
 * and that will be sent to {@link #scheduleReopen(INotification)} instead.
 */
public class MFXNotificationSystem extends AbstractMFXNotificationSystem {
	//================================================================================
	// Instance
	//================================================================================
	private static final MFXNotificationSystem instance = new MFXNotificationSystem();

	public static MFXNotificationSystem instance() {
		return instance;
	}

	//================================================================================
	// Properties
	//================================================================================
	private final CircularQueue<INotification> notifications = new CircularQueue<>(100);
	private final List<INotification> queued = new ArrayList<>();
	private final Group notificationContainer;

	//================================================================================
	// Constructors
	//================================================================================
	private MFXNotificationSystem() {
		super();
		notificationContainer = new Group();
		notificationContainer.setOpacity(0.0);

		popup.setContent(notificationContainer);
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
	public MFXNotificationSystem initOwner(Window owner) {
		dispose();
		super.owner = owner;
		owner.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, onClose);
		PositionBean positionBean = computePosition();
		popup.show(dummyStage, positionBean.getX(), positionBean.getY());
		return this;
	}

	/**
	 * Initializes the popup's position for the specified {@link NotificationPos}.
	 */
	@Override
	protected void init() {
		PositionBean position = computePosition();
		popup.hoverProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				closeAfterTransition.stop();
			} else if (closeAutomatically) {
				closeAfterTransition.playFromStart();
			}
		});
		popup.show(dummyStage, position.getX(), position.getY());
	}

	/**
	 * If the notification system is showing/closing, exits and {@link #scheduleReopen(INotification)} is called.
	 * <p>
	 * Adds the notification to the notifications queue then if the notification system is set
	 * to close automatically starts the close {@link PauseTransition}.
	 * <p>
	 * Shows the popup (the content is still hidden tough so it's not really open), then forces
	 * the notification container to compute its bounds (should not be necessary though), shown the content
	 * and calls {@link #show()}.
	 *
	 * @throws IllegalStateException if the notification system has not been initialized
	 */
	@Override
	public MFXNotificationSystem publish(INotification notification) {
		if (notification == null) return this;

		if (owner == null) {
			throw new IllegalStateException("The NotificationSystem has not been initialized!");
		} else if (isClosing() || isShowing()) {
			scheduleReopen(notification);
		} else {
			notifications.add(notification);
			if (closeAutomatically) {
				closeAfterTransition.playFromStart();
			}

			if (!isShowing()) {
				init();
				popup.show(dummyStage);

				notificationContainer.getChildren().setAll(notification.getContent());
				notificationContainer.applyCss();
				notificationContainer.layout();

				//popup.setY(screen.getBounds().getWidth());
				if (animated) {
					TimelineBuilder.build().show(400, notificationContainer).getAnimation().play();
				} else {
					notificationContainer.setOpacity(1.0);
				}
				show();
			}
		}
		return this;
	}

	/**
	 * Sets the showing property to true, computes the popup position as a {@link TransitionPositionBean},
	 * then positions the popup (animated or not).
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
					.add(ConsumerTransition.of(frac -> popup.setY(y - deltaY * frac), Duration.millis(400), Interpolators.INTERPOLATOR_V2.toInterpolator()))
					.add(KeyFrames.of(1, event -> popup.setX(x - deltaX)))
					.getAnimation().play();
		} else {
			popup.setX(x - deltaX);
			popup.setY(y - deltaY);
		}
	}

	/**
	 * Sets the closing property to true and hides the notification.
	 * At the end always hides the popup, and resets the showing/closing properties.
	 */
	@Override
	protected void close() {
		closing.set(true);
		if (animated) {
			TimelineBuilder.build()
					.hide(400, notificationContainer)
					.setOnFinished(event -> {
						popup.hide();
						showing.reset();
						closing.reset();
					})
					.getAnimation()
					.play();
		} else {
			PauseBuilder.build().setDuration(30).setOnFinished(event -> {
				notificationContainer.setOpacity(0);
				popup.hide();
				showing.reset();
				closing.reset();
			}).getAnimation().play();
		}
	}

	/**
	 * Adds the notification to the queued notifications list, then adds a one-time listener to the closing property
	 * so that when it becomes false a notification is removed from the queue and then
	 * sent to {@link #publish(INotification)} again.
	 */
	@Override
	protected void scheduleReopen(INotification notification) {
		queued.add(notification);
		ExecutionUtils.executeWhen(
				closing,
				(oldValue, newValue) -> {
					if (!queued.isEmpty()) {
						PauseBuilder.build()
								.setDuration(300)
								.setOnFinished(event -> publish(queued.remove(0)))
								.getAnimation().play();
					}
				},
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
		Bounds containerBounds = notificationContainer.getLayoutBounds();

		if (isTop(position)) {
			y = -containerBounds.getMaxY() - spacing.getTop();
			endY = screenBounds.getMinY() + spacing.getTop();
		} else {
			y = screenBounds.getMaxY() + spacing.getBottom();
			endY = screenBounds.getMaxY() - containerBounds.getMaxY() - spacing.getBottom();
		}

		if (isCenter(position)) {
			x = (screenBounds.getMaxX() / 2) - (containerBounds.getMaxX() / 2);
			endX = x;
		} else if (isRight(position)) {
			x = screenBounds.getMaxX() + spacing.getRight();
			endX = screenBounds.getMaxX() - containerBounds.getMaxX() - spacing.getRight();
		} else {
			x = -containerBounds.getMaxX() - spacing.getLeft();
			endX = screenBounds.getMinX() + spacing.getLeft();
		}
		return TransitionPositionBean.of(x, y, endX, endY);
	}

	@Override
	public INotificationSystem dispose() {
		if (super.owner != null) {
			super.owner.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, onClose);
			super.owner = null;
		}
		return this;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Returns the list of shown notifications.
	 */
	public CircularQueue<INotification> history() {
		return notifications;
	}

	/**
	 * Delegate to {@link CircularQueue#setSize(int)}.
	 */
	public MFXNotificationSystem setHistoryLimit(int size) {
		notifications.setSize(size);
		return this;
	}
}
