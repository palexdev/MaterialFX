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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.collections.TransformableListWrapper;
import io.github.palexdev.materialfx.controls.base.MFXMenuControl;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.enums.NotificationCounterStyle;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.selection.MultipleSelectionModel;
import io.github.palexdev.materialfx.skins.MFXNotificationCenterSkin;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.others.ReusableScheduledExecutor;
import io.github.palexdev.virtualizedfx.beans.NumberRange;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import io.github.palexdev.virtualizedfx.utils.ListChangeHelper;
import io.github.palexdev.virtualizedfx.utils.ListChangeHelper.Change;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

import static io.github.palexdev.materialfx.enums.NotificationCounterStyle.NUMBER;

/**
 * A quite complex but easy to use implementation of a modern notification center.
 * <p></p>
 * For the notifications it uses {@link TransformableListWrapper} as list implementation, this allows
 * not only basic operations such additions, removals and replacements, but also filter and sort operations.
 * <p></p>
 * It's composed by an icon and a popup that contains the list of notifications.
 * <p>
 * A complete list of the features the notification center offers:
 * <p> - Uses a virtual flow to show the notifications and have high performance
 * <p> - Uses {@link MFXNotificationCell} as cells to contain the notifications, those special
 * cells perfectly integrate with the selection mode feature of the notification center to show a checkbox when needed
 * <p> - Has a {@link MultipleSelectionModel} to keep track of the selected notifications
 * <p> - Has a property that keeps track of the number of unread notifications
 * <p> - Has two styles for the unread counter: as a DOT, or as a dot with the NUMBER
 * <p> - Allows to change the header's text
 * <p> - Allows to toggle a "Do not disturb" mode
 * <p> - Has a property that specifies whether the popup is showing or not
 * <p> - Has a property that specifies whether the mouse is on the popup (it's bound, cannot be set, nor unbound)
 * <p> - Allows to specify the space between the bell icon and the popup
 * <p> - Allows to specify the popup's size. Must be greater than 0 and should always be larger than the bell's icon
 * <p> - Has a context menu that opens when right-clicking on the virtual flow, by default it contains
 * items to perform selection, filter and sort actions. It can also be disabled by using {@link MFXMenuControl#setContextMenuDisabled(boolean)}.
 * <p> - Has a flag to specify whether the notification center is animated or not
 * <p> - Has a flag to specify whether visible notifications should be set as READ when the popup is shown
 * <p> - Has a flag to specify whether notifications should be set as READ when they are dismissed
 * <p> - Allows specifying the action to perform when the bell icon is pressed, by default inverts the value of the {@link #showingProperty()}
 * to inform the popup that it should open/hide
 * <p> Has an executor that runs every 60 seconds (by default, can be changed) that updates all the notifications.
 * By update, I mean that {@link INotification#updateElapsed()} is called. The service can be stopped and started
 * whenever desired, by default it is always started.
 * <p></p>
 * As you can see it as a LOT to offer, and to be honest it's not everything I wanted to implement, but I decided to
 * restrain myself for now, as adding any other feature would add more and more complexity.
 */
public class MFXNotificationCenter extends Control implements MFXMenuControl {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-notification-center";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXNotificationCenter.css");

	private final TransformableListWrapper<INotification> notifications = new TransformableListWrapper<>(FXCollections.observableArrayList());

	private final SimpleVirtualFlow<INotification, MFXNotificationCell> virtualFlow;
	private final MultipleSelectionModel<INotification> selectionModel = new MultipleSelectionModel<>(notifications);

	private final BooleanProperty selectionMode = new SimpleBooleanProperty(false);
	private final ReadOnlyLongWrapper unreadCount = new ReadOnlyLongWrapper(0);
	private final LongBinding unreadCountBinding;

	private final ObjectProperty<NotificationCounterStyle> counterStyle = new SimpleObjectProperty<>(NUMBER);
	private final StringProperty headerTextProperty = new SimpleStringProperty(I18N.getOrDefault("notificationCenter.header"));
	private final BooleanProperty doNotDisturb = new SimpleBooleanProperty(false);
	private final BooleanProperty showing = new SimpleBooleanProperty(false);

	private final BooleanProperty popupHover = new SimpleBooleanProperty(false) {
		@Override
		public void unbind() {
		}
	};
	private final DoubleProperty popupSpacing = new SimpleDoubleProperty(10);
	private final DoubleProperty popupWidth = new SimpleDoubleProperty(450);
	private final DoubleProperty popupHeight = new SimpleDoubleProperty(550);

	private MFXContextMenu contextMenu;

	private boolean animated = true;
	private boolean markAsReadOnShow = false;
	private boolean markAsReadOnDismiss = false;

	private EventHandler<MouseEvent> onIconClicked = event -> setShowing(!isShowing());

	private final ReusableScheduledExecutor notificationsUpdater;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXNotificationCenter() {
		virtualFlow = new SimpleVirtualFlow<>(
				notifications,
				notification -> new MFXNotificationCell(this, notification),
				Orientation.VERTICAL
		) {
			@Override
			public String getUserAgentStylesheet() {
				return MFXNotificationCenter.this.getUserAgentStylesheet();
			}
		};

		unreadCountBinding = Bindings.createLongBinding(() ->
						notifications.stream()
								.filter(notification -> notification.getState() == NotificationState.UNREAD)
								.count(),
				notifications
		);

		notificationsUpdater = new ReusableScheduledExecutor(Executors.newScheduledThreadPool(
				1,
				r -> {
					Thread thread = new Thread(r);
					thread.setDaemon(true);
					return thread;
				}
		));
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		getStylesheets().add(STYLESHEET);
		setPrefSize(400, 550);
		defaultContextMenu();

		unreadCount.bind(unreadCountBinding);
		notifications.addListener((ListChangeListener<? super INotification>) change -> {
			if (!selectionModel.getSelection().isEmpty()) {
				if (change.getList().isEmpty()) {
					selectionModel.clearSelection();
				} else {
					Change c = ListChangeHelper.processChange(change, NumberRange.of(0, Integer.MAX_VALUE));
					ListChangeProcessor updater = new ListChangeProcessor(selectionModel.getSelection().keySet());
					c.processReplacement((changed, removed) -> selectionModel.replaceSelection(changed.toArray(new Integer[0])));
					c.processAddition((from, to, added) -> {
						updater.computeAddition(added.size(), from);
						selectionModel.replaceSelection(updater.getIndexes().toArray(new Integer[0]));
					});
					c.processRemoval((from, to, removed) -> {
						updater.computeRemoval(removed, from);
						selectionModel.replaceSelection(updater.getIndexes().toArray(new Integer[0]));
					});
				}
			}
		});

		notifications.predicateProperty().addListener(invalidated -> {
			setSelectionMode(false);
			selectionModel.clearSelection();
		});
		notifications.comparatorProperty().addListener(invalidated -> {
			setSelectionMode(false);
			selectionModel.clearSelection();
		});

		showing.addListener((observable, oldValue, newValue) -> {
			if (newValue && markAsReadOnShow) markVisibleNotificationsAs(NotificationState.READ);
		});

		startNotificationsUpdater(60, TimeUnit.SECONDS);
	}

	/**
	 * Responsible for building and setting the default context menu.
	 */
	protected void defaultContextMenu() {
		MFXContextMenuItem selectAll = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.selectAll"))
				.setOnAction(event -> {
					if (notifications.isEmpty()) return;
					setSelectionMode(true);
					NumberRange<Integer> indexes = NumberRange.of(0, notifications.size() - 1);
					selectionModel.replaceSelection(NumberRange.expandRange(indexes).toArray(Integer[]::new));
				}).get();

		MFXContextMenuItem selectRead = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.selectRead"))
				.setOnAction(event -> {
					if (notifications.isEmpty()) return;

					setSelectionMode(true);
					Integer[] indexes = IntStream.range(0, notifications.size())
							.filter(i -> notifications.get(i).getState() == NotificationState.READ)
							.boxed()
							.toArray(Integer[]::new);
					selectionModel.replaceSelection(indexes);
				}).get();

		MFXContextMenuItem selectUnread = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.selectUnread"))
				.setOnAction(event -> {
					if (notifications.isEmpty()) return;
					setSelectionMode(true);
					Integer[] indexes = IntStream.range(0, notifications.size())
							.filter(i -> notifications.get(i).getState() == NotificationState.UNREAD)
							.boxed()
							.toArray(Integer[]::new);
					selectionModel.replaceSelection(indexes);
				}).get();

		MFXContextMenuItem clearSelection = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.clearSelection"))
				.setOnAction(event -> selectionModel.clearSelection())
				.get();

		MFXContextMenuItem sortByState = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.sortState"))
				.setOnAction(event -> notifications.setComparator(Comparator.comparing(INotification::getState)))
				.get();

		MFXContextMenuItem sortByTime = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.sortTime"))
				.setOnAction(event -> notifications.setComparator(Comparator.comparing(INotification::getTime)))
				.get();

		MFXContextMenuItem reverseSort = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.reverseSort"))
				.setOnAction(event -> {
					if (notifications.getComparator() == null) return;
					Comparator<INotification> comparator = notifications.getComparator();
					notifications.setComparator(comparator.reversed());
				}).get();

		MFXContextMenuItem filterRead = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.filterRead"))
				.setOnAction(event -> notifications.setPredicate(notification -> notification.getState() == NotificationState.READ))
				.get();

		MFXContextMenuItem filterUnread = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.filterUnread"))
				.setOnAction(event -> notifications.setPredicate(notification -> notification.getState() == NotificationState.UNREAD))
				.get();

		MFXContextMenuItem clearFilter = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.clearFilter"))
				.setOnAction(event -> notifications.setPredicate(null))
				.get();

		MFXContextMenuItem clearSort = MFXContextMenuItem.Builder.build()
				.setText(I18N.getOrDefault("notificationCenter.contextMenu.clearSort"))
				.setOnAction(event -> notifications.setComparator(null))
				.get();


		contextMenu = MFXContextMenu.Builder.build(virtualFlow)
				.addSeparator(new Label(I18N.getOrDefault("notificationCenter.contextMenu.selectionSeparator")))
				.addItems(selectAll, selectRead, selectUnread, clearSelection)
				.addSeparator(new Label(I18N.getOrDefault("notificationCenter.contextMenu.sortingSeparator")))
				.addItems(sortByState, sortByTime, reverseSort, clearSort)
				.addSeparator(new Label(I18N.getOrDefault("notificationCenter.contextMenu.filterSeparator")))
				.addItems(filterRead, filterUnread, clearFilter)
				.setPopupStyleableParent(this)
				.installAndGet();
	}

	/**
	 * Starts the notifications updater service to run the update task
	 * periodically, according to the given period and time unit.
	 *
	 * @see INotification#updateElapsed()
	 */
	public void startNotificationsUpdater(long period, TimeUnit timeUnit) {
		notificationsUpdater.scheduleAtFixedRate(
				() -> notifications.forEach(INotification::updateElapsed),
				0,
				period,
				timeUnit
		);
	}

	/**
	 * Immediately stops the notifications updater service.
	 */
	public void stopNotificationsUpdater() {
		notificationsUpdater.cancelNow();
	}

	/**
	 * Sets all the given notifications' state to the given state.
	 * <p>
	 * At the end recomputes the number of unread notifications.
	 */
	public void markNotificationsAs(NotificationState state, INotification... notifications) {
		for (INotification notification : notifications) {
			notification.setNotificationState(state);
		}
		unreadCountBinding.invalidate();
	}

	/**
	 * Sets all the visible notifications' state to the given state.
	 */
	public void markVisibleNotificationsAs(NotificationState state) {
		markNotificationsAs(
				state,
				getCells().values().stream()
						.map(MFXNotificationCell::getNotification)
						.toArray(INotification[]::new)
		);
	}

	/**
	 * Sets all the selected notifications' state to the given state.
	 */
	public void markSelectedNotificationsAs(NotificationState state) {
		markNotificationsAs(state, selectionModel.getSelection().values().toArray(INotification[]::new));
	}

	/**
	 * Sets all the notifications' state to the given state.
	 */
	public void markAllNotificationsAs(NotificationState state) {
		markNotificationsAs(state, notifications.toArray(INotification[]::new));
	}

	/**
	 * Sets all the given notifications' state to READ, then removes them from the notifications list.
	 */
	public void dismiss(INotification... notifications) {
		if (markAsReadOnDismiss) {
			markNotificationsAs(NotificationState.READ, notifications);
		}
		this.notifications.removeAll(notifications);
	}

	/**
	 * Sets all the visible notifications' state to READ, then removes them from the notifications list.
	 */
	public void dismissVisible() {
		dismiss(getCells().values().stream().map(MFXNotificationCell::getNotification).toArray(INotification[]::new));
	}

	/**
	 * Sets all the selected notifications' state to READ, then removes them from the notifications list.
	 */
	public void dismissSelected() {
		dismiss(getSelectionModel().getSelection().values().toArray(INotification[]::new));
	}

	/**
	 * Sets all the notifications' state to READ, then removes them from the notifications list.
	 */
	public void dismissAll() {
		dismiss(notifications.toArray(INotification[]::new));
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the list of notifications
	 */
	public TransformableListWrapper<INotification> getNotifications() {
		return notifications;
	}

	/**
	 * @return the selection model instance used to keep track of selected notifications
	 */
	public MultipleSelectionModel<INotification> getSelectionModel() {
		return selectionModel;
	}

	public boolean isSelectionMode() {
		return selectionMode.get();
	}

	/**
	 * Specifies if the notification center is in selection mode.
	 * <p></p>
	 * By default this mode triggers {@link MFXNotificationCell} to show a checkbox for selection
	 */
	public BooleanProperty selectionModeProperty() {
		return selectionMode;
	}

	public void setSelectionMode(boolean selectionMode) {
		this.selectionMode.set(selectionMode);
	}

	public long getUnreadCount() {
		return unreadCount.get();
	}

	/**
	 * Specifies the number of unread notifications.
	 */
	public ReadOnlyLongProperty unreadCountProperty() {
		return unreadCount.getReadOnlyProperty();
	}

	public NotificationCounterStyle getCounterStyle() {
		return counterStyle.get();
	}

	/**
	 * Specifies the style of the unread counter.
	 */
	public ObjectProperty<NotificationCounterStyle> counterStyleProperty() {
		return counterStyle;
	}

	public void setCounterStyle(NotificationCounterStyle counterStyle) {
		this.counterStyle.set(counterStyle);
	}

	public String getHeaderTextProperty() {
		return headerTextProperty.get();
	}

	/**
	 * Specifies the header's text.
	 */
	public StringProperty headerTextPropertyProperty() {
		return headerTextProperty;
	}

	public void setHeaderTextProperty(String headerTextProperty) {
		this.headerTextProperty.set(headerTextProperty);
	}

	public boolean isDoNotDisturb() {
		return doNotDisturb.get();
	}

	/**
	 * Specifies if the notification center is in "Do not disturb" mode.
	 */
	public BooleanProperty doNotDisturbProperty() {
		return doNotDisturb;
	}

	public void setDoNotDisturb(boolean doNotDisturb) {
		this.doNotDisturb.set(doNotDisturb);
	}

	public boolean isShowing() {
		return showing.get();
	}

	/**
	 * Specifies if the popup is shown/hidden.
	 * <p>
	 * Can also be used to control the popup.
	 */
	public BooleanProperty showingProperty() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing.set(showing);
	}

	public boolean isPopupHover() {
		return popupHover.get();
	}

	/**
	 * Specifies if the mouse is on the popup.
	 * <p></p>
	 * Despite being a Read-Write property, it is bound to the popup's hover property
	 * and cannot be unbound. Attempts to set this will always fail with an exception.
	 */
	public BooleanProperty popupHoverProperty() {
		return popupHover;
	}

	public double getPopupSpacing() {
		return popupSpacing.get();
	}

	/**
	 * Specifies the space between the bell icon and the popup
	 */
	public DoubleProperty popupSpacingProperty() {
		return popupSpacing;
	}

	public void setPopupSpacing(double popupSpacing) {
		this.popupSpacing.set(popupSpacing);
	}

	public double getPopupWidth() {
		return popupWidth.get();
	}

	/**
	 * Specifies the popups' width.
	 */
	public DoubleProperty popupWidthProperty() {
		return popupWidth;
	}

	public void setPopupWidth(double popupWidth) {
		this.popupWidth.set(popupWidth);
	}

	public double getPopupHeight() {
		return popupHeight.get();
	}

	/**
	 * Specifies the popup's height.
	 */
	public DoubleProperty popupHeightProperty() {
		return popupHeight;
	}

	public void setPopupHeight(double popupHeight) {
		this.popupHeight.set(popupHeight);
	}

	/**
	 * @return the currently built {@link MFXContextMenu}
	 */
	@Override
	public MFXContextMenu getMFXContextMenu() {
		return contextMenu;
	}

	/**
	 * Specifies whether the notification center is animated.
	 */
	public boolean isAnimated() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}

	/**
	 * Specifies whether visible notification should be marked as read when the popup is shown.
	 */
	public boolean isMarkAsReadOnShow() {
		return markAsReadOnShow;
	}

	public void setMarkAsReadOnShow(boolean markAsReadOnShow) {
		this.markAsReadOnShow = markAsReadOnShow;
	}

	/**
	 * Specifies whether dismissed notifications should be set as READ.
	 * <p></p>
	 * For this to work use one of the dismiss methods offered by the control.
	 */
	public boolean isMarkAsReadOnDismiss() {
		return markAsReadOnDismiss;
	}

	public void setMarkAsReadOnDismiss(boolean markAsReadOnDismiss) {
		this.markAsReadOnDismiss = markAsReadOnDismiss;
	}

	/**
	 * Specifies the action to perform when the bell icon is clicked.
	 */
	public EventHandler<MouseEvent> getOnIconClicked() {
		return onIconClicked;
	}

	public void setOnIconClicked(EventHandler<MouseEvent> onIconClicked) {
		this.onIconClicked = onIconClicked;
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getCell(int)}.
	 */
	public MFXNotificationCell getCell(int index) {
		return virtualFlow.getCell(index);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getCells()}.
	 */
	public Map<Integer, MFXNotificationCell> getCells() {
		return virtualFlow.getCells();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollBy(double)}.
	 */
	public void scrollBy(double pixels) {
		virtualFlow.scrollBy(pixels);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollTo(int)}.
	 */
	public void scrollTo(int index) {
		virtualFlow.scrollTo(index);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToFirst()}.
	 */
	public void scrollToFirst() {
		virtualFlow.scrollToFirst();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToLast()}.
	 */
	public void scrollToLast() {
		virtualFlow.scrollToLast();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#scrollToPixel(double)}.
	 */
	public void scrollToPixel(double pixel) {
		virtualFlow.scrollToPixel(pixel);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#setHSpeed(double, double)}.
	 */
	public void setHSpeed(double unit, double block) {
		virtualFlow.setHSpeed(unit, block);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#setVSpeed(double, double)}.
	 */
	public void setVSpeed(double unit, double block) {
		virtualFlow.setVSpeed(unit, block);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getVerticalPosition()}.
	 */
	public double getVerticalPosition() {
		return virtualFlow.getVerticalPosition();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#getHorizontalPosition()}.
	 */
	public double getHorizontalPosition() {
		return virtualFlow.getHorizontalPosition();
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#setCellFactory(Function)}.
	 */
	public void setCellFactory(Function<INotification, MFXNotificationCell> cellFactory) {
		virtualFlow.setCellFactory(cellFactory);
	}

	/**
	 * Delegate method for {@link SimpleVirtualFlow#features()}.
	 */
	public SimpleVirtualFlow<INotification, MFXNotificationCell>.Features features() {
		return virtualFlow.features();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXNotificationCenterSkin(this, virtualFlow);
	}
}
