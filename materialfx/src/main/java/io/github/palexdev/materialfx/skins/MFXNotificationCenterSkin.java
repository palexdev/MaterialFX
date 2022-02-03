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

import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXPopup.MFXPopupEvent;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.enums.NotificationCounterStyle;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.beans.binding.Bindings;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * This is the skin associated with every {@link MFXNotificationCenter}.
 * <p></p>
 * It is composed by an icon, which is a bell, that opens a {@link MFXPopup} to show the notification center.
 */
public class MFXNotificationCenterSkin extends SkinBase<MFXNotificationCenter> {
	//================================================================================
	// Properties
	//================================================================================
	private final MFXIconWrapper bellWrapped;
	private final NotificationsCounter counter;
	private final MFXPopup popup;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXNotificationCenterSkin(MFXNotificationCenter notificationCenter, SimpleVirtualFlow<INotification, MFXNotificationCell> virtualFlow) {
		super(notificationCenter);

		bellWrapped = new MFXIconWrapper("mfx-bell-alt", 36, 56);
		bellWrapped.getIcon().setMouseTransparent(true);
		bellWrapped.getStyleClass().add("notifications-icon");

		counter = new NotificationsCounter();
		counter.setManaged(false);

		MFXTextField headerLabel = MFXTextField.asLabel();
		headerLabel.textProperty().bind(notificationCenter.headerTextPropertyProperty());
		headerLabel.setAlignment(Pos.CENTER_LEFT);
		headerLabel.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(headerLabel, Priority.ALWAYS);

		MFXToggleButton dndToggle = new MFXToggleButton(I18N.getOrDefault("notificationCenter.dnd"));
		dndToggle.setContentDisplay(ContentDisplay.RIGHT);
		dndToggle.setGraphicTextGap(15);
		notificationCenter.doNotDisturbProperty().bindBidirectional(dndToggle.selectedProperty());

		HBox header = new HBox(headerLabel, dndToggle);
		header.getStyleClass().add("header");
		header.setAlignment(Pos.CENTER_LEFT);

		MFXIconWrapper select = new MFXIconWrapper("mfx-variant13-mark", 24, 36).defaultRippleGeneratorBehavior();
		MFXIconWrapper markAsRead = new MFXIconWrapper("mfx-eye", 20, 36).defaultRippleGeneratorBehavior();
		MFXIconWrapper markAsUnread = new MFXIconWrapper("mfx-eye-slash", 20, 36).defaultRippleGeneratorBehavior();
		MFXIconWrapper dismiss = new MFXIconWrapper("mfx-delete", 20, 36).defaultRippleGeneratorBehavior();
		MFXIconWrapper options = new MFXIconWrapper("mfx-bars", 18, 36).defaultRippleGeneratorBehavior();

		select.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.setSelectionMode(!notificationCenter.isSelectionMode()));
		markAsRead.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.markSelectedNotificationsAs(NotificationState.READ));
		markAsUnread.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.markSelectedNotificationsAs(NotificationState.UNREAD));
		dismiss.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.dismissSelected());
		options.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			MFXContextMenu contextMenu = notificationCenter.getMFXContextMenu();
			Bounds toScreen = options.localToScreen(options.getLayoutBounds());
			contextMenu.show(options, toScreen.getMinX(), toScreen.getMinY());
		});

		NodeUtils.makeRegionCircular(select);
		NodeUtils.makeRegionCircular(markAsRead);
		NodeUtils.makeRegionCircular(markAsUnread);
		NodeUtils.makeRegionCircular(dismiss);
		NodeUtils.makeRegionCircular(options);

		HBox actions = new HBox(40, select, markAsRead, markAsUnread, dismiss, options);
		actions.getStyleClass().add("actions");
		actions.setAlignment(Pos.CENTER);

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(header);
		borderPane.setCenter(virtualFlow);
		borderPane.setBottom(actions);
		borderPane.getStyleClass().add("notifications-container");

		borderPane.setMinHeight(Region.USE_PREF_SIZE);
		borderPane.setMaxHeight(Region.USE_PREF_SIZE);
		borderPane.prefWidthProperty().bind(notificationCenter.popupWidthProperty());
		borderPane.prefHeightProperty().bind(notificationCenter.popupHeightProperty());
		BorderPane.setMargin(virtualFlow, InsetsFactory.all(5));

		popup = new MFXPopup(borderPane) {
			@Override
			public Styleable getStyleableParent() {
				return MFXNotificationCenterSkin.this.getSkinnable();
			}
		};
		popup.getStyleClass().add("popup");
		popup.setAnimated(false);
		popup.setConsumeAutoHidingEvents(true);

		getChildren().setAll(bellWrapped, counter);
		addListeners();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void addListeners() {
		MFXNotificationCenter notificationCenter = getSkinnable();

		popup.showingProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && notificationCenter.isShowing()) notificationCenter.setShowing(false);
		});
		bellWrapped.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> notificationCenter.getOnIconClicked().handle(event));
		notificationCenter.showingProperty().addListener((observable, oldValue, newValue) -> managePopup(newValue));
		notificationCenter.addEventFilter(MFXPopupEvent.REPOSITION_EVENT, event -> popup.reposition());
		notificationCenter.popupHoverProperty().bind(Bindings.createBooleanBinding(
				() -> popup.isHover() || notificationCenter.getMFXContextMenu().isShowing(),
				popup.hoverProperty(), notificationCenter.getMFXContextMenu().showingProperty()
		));
	}

	/**
	 * Shows/Hides the popup.
	 */
	protected void managePopup(boolean showing) {
		if (!showing) {
			popup.hide();
			return;
		}
		popup.show(bellWrapped, Alignment.of(HPos.CENTER, VPos.BOTTOM), 0, getSkinnable().getPopupSpacing());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return bellWrapped.prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return bellWrapped.prefHeight(-1);
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

		// That one time I really need math, I don't understand how it works, but it works :)
		double bellCenterX = bellWrapped.getBoundsInParent().getCenterX();
		double bellCenterY = bellWrapped.getBoundsInParent().getCenterY();
		double radius = bellWrapped.getSize() / 2;
		double angle = 45;
		double angleToRadians = Math.PI / 180;
		double size = counter.getSize();
		double counterX = bellCenterX + radius * Math.cos(angle * angleToRadians) - (size / 2);
		double counterY = bellCenterY - radius * Math.sin(angle * angleToRadians) - (size / 2);
		counter.resizeRelocate(counterX, counterY, size, size);
	}

	/**
	 * To keep things clean and organized the {@link MFXNotificationCenter}'s counter has been
	 * written to a separate class.
	 */
	private class NotificationsCounter extends MFXIconWrapper {

		public NotificationsCounter() {
			MFXNotificationCenter notificationCenter = getSkinnable();

			Text counterText = new Text();
			counterText.getStyleClass().add("text");

			counterText.textProperty().addListener(invalidated -> {
				double padding = notificationCenter.getCounterStyle() == NotificationCounterStyle.DOT ? 3 : 7;
				double textW = counterText.prefWidth(-1);
				double textH = counterText.prefHeight(-1);
				double size = textW > textH ? snapSizeX(textW + padding) : snapSizeY(textH + padding);
				setSize(size);
				notificationCenter.requestLayout();
				requestLayout();
			});
			counterText.visibleProperty().bind(Bindings.createBooleanBinding(
					() -> notificationCenter.getCounterStyle() == NotificationCounterStyle.NUMBER,
					notificationCenter.counterStyleProperty()
			));
			counterText.textProperty().bind(notificationCenter.unreadCountProperty().asString());
			counterText.textProperty().bind(Bindings.createStringBinding(
					() -> notificationCenter.getCounterStyle() == NotificationCounterStyle.NUMBER ? Long.toString(notificationCenter.getUnreadCount()) : "",
					notificationCenter.unreadCountProperty(), notificationCenter.counterStyleProperty()
			));
			counterText.setMouseTransparent(true);

			getStyleClass().add("counter");
			visibleProperty().bind(notificationCenter.unreadCountProperty().greaterThan(0));
			setIcon(counterText);
		}
	}
}
