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

package io.github.palexdev.materialfx.builders.control;

import io.github.palexdev.materialfx.builders.base.ControlBuilder;
import io.github.palexdev.materialfx.controls.MFXNotificationCenter;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.enums.NotificationCounterStyle;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.notifications.base.INotification;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class NotificationCenterBuilder extends ControlBuilder<MFXNotificationCenter> {

	//================================================================================
	// Constructors
	//================================================================================
	public NotificationCenterBuilder() {
		this(new MFXNotificationCenter());
	}

	public NotificationCenterBuilder(MFXNotificationCenter notificationCenter) {
		super(notificationCenter);
	}

	public static NotificationCenterBuilder notificationCenter() {
		return new NotificationCenterBuilder();
	}

	public static NotificationCenterBuilder notificationCenter(MFXNotificationCenter notificationCenter) {
		return new NotificationCenterBuilder(notificationCenter);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	public NotificationCenterBuilder addNotifications(INotification... notifications) {
		node.getNotifications().addAll(notifications);
		return this;
	}

	public NotificationCenterBuilder startNotificationsUpdater(long period, TimeUnit timeUnit) {
		node.startNotificationsUpdater(period, timeUnit);
		return this;
	}

	public NotificationCenterBuilder stopNotificationsUpdater() {
		node.stopNotificationsUpdater();
		return this;
	}

	public NotificationCenterBuilder markNotificationsAs(NotificationState state, INotification... notifications) {
		node.markNotificationsAs(state, notifications);
		return this;
	}

	public NotificationCenterBuilder markVisibleNotificationsAs(NotificationState state) {
		node.markVisibleNotificationsAs(state);
		return this;
	}

	public NotificationCenterBuilder markSelectedNotificationsAs(NotificationState state) {
		node.markSelectedNotificationsAs(state);
		return this;
	}

	public NotificationCenterBuilder markAllNotificationsAs(NotificationState state) {
		node.markAllNotificationsAs(state);
		return this;
	}

	public NotificationCenterBuilder dismiss(INotification... notifications) {
		node.dismiss(notifications);
		return this;
	}

	public NotificationCenterBuilder dismissVisible() {
		node.dismissVisible();
		return this;
	}

	public NotificationCenterBuilder dismissSelected() {
		node.dismissSelected();
		return this;
	}

	public NotificationCenterBuilder dismissAll() {
		node.dismissAll();
		return this;
	}

	public NotificationCenterBuilder setSelectionMode(boolean selectionMode) {
		node.setSelectionMode(selectionMode);
		return this;
	}

	public NotificationCenterBuilder setCounterStyle(NotificationCounterStyle counterStyle) {
		node.setCounterStyle(counterStyle);
		return this;
	}

	public NotificationCenterBuilder setHeaderTextProperty(String headerTextProperty) {
		node.setHeaderTextProperty(headerTextProperty);
		return this;
	}

	public NotificationCenterBuilder setDoNotDisturb(boolean doNotDisturb) {
		node.setDoNotDisturb(doNotDisturb);
		return this;
	}

	public NotificationCenterBuilder setPopupSpacing(double popupSpacing) {
		node.setPopupSpacing(popupSpacing);
		return this;
	}

	public NotificationCenterBuilder setPopupWidth(double popupWidth) {
		node.setPopupWidth(popupWidth);
		return this;
	}

	public NotificationCenterBuilder setPopupHeight(double popupHeight) {
		node.setPopupHeight(popupHeight);
		return this;
	}

	public NotificationCenterBuilder setAnimated(boolean animated) {
		node.setAnimated(animated);
		return this;
	}

	public NotificationCenterBuilder setMarkAsReadOnShow(boolean markAsReadOnShow) {
		node.setMarkAsReadOnShow(markAsReadOnShow);
		return this;
	}

	public NotificationCenterBuilder setMarkAsReadOnDismiss(boolean markAsReadOnDismiss) {
		node.setMarkAsReadOnDismiss(markAsReadOnDismiss);
		return this;
	}

	public NotificationCenterBuilder setOnIconClicked(EventHandler<MouseEvent> onIconClicked) {
		node.setOnIconClicked(onIconClicked);
		return this;
	}

	public NotificationCenterBuilder scrollBy(double pixels) {
		node.scrollBy(pixels);
		return this;
	}

	public NotificationCenterBuilder scrollTo(int index) {
		node.scrollTo(index);
		return this;
	}

	public NotificationCenterBuilder scrollToFirst() {
		node.scrollToFirst();
		return this;
	}

	public NotificationCenterBuilder scrollToLast() {
		node.scrollToLast();
		return this;
	}

	public NotificationCenterBuilder scrollToPixel(double pixel) {
		node.scrollToPixel(pixel);
		return this;
	}

	public NotificationCenterBuilder setHSpeed(double unit, double block) {
		node.setHSpeed(unit, block);
		return this;
	}

	public NotificationCenterBuilder setVSpeed(double unit, double block) {
		node.setVSpeed(unit, block);
		return this;
	}

	public NotificationCenterBuilder setCellFactory(Function<INotification, MFXNotificationCell> cellFactory) {
		node.setCellFactory(cellFactory);
		return this;
	}

	public NotificationCenterBuilder enableSmoothScrolling(double speed) {
		node.features().enableSmoothScrolling(speed);
		return this;
	}

	public NotificationCenterBuilder enableSmoothScrolling(double speed, double trackPadAdjustment) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment);
		return this;
	}

	public NotificationCenterBuilder enableSmoothScrolling(double speed, double trackPadAdjustment, double scrollThreshold) {
		node.features().enableSmoothScrolling(speed, trackPadAdjustment, scrollThreshold);
		return this;
	}

	public NotificationCenterBuilder enableBounceEffect() {
		node.features().enableBounceEffect();
		return this;
	}

	public NotificationCenterBuilder enableBounceEffect(double strength, double maxOverscroll) {
		node.features().enableBounceEffect(strength, maxOverscroll);
		return this;
	}
}
