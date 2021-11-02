package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.MFXPopup.MFXPopupEvent;
import io.github.palexdev.materialfx.controls.cell.MFXNotificationCell;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.enums.NotificationCounterStyle;
import io.github.palexdev.materialfx.enums.NotificationState;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.notifications.base.INotification;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.virtualizedfx.flow.simple.SimpleVirtualFlow;
import javafx.beans.binding.Bindings;
import javafx.css.Styleable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * This is the skin associated with every {@link MFXNotificationCenter}.
 * <p></p>
 * It is composed by an icon, which is a bell, that opens a {@link MFXPopup} to show the notification center.
 * <p></p>
 * As of now there's a bug that I can't fix because of JavaFX internal API, if the popup is open and the
 * icon has been clicked again the popup won't open but will stay open. I'll see what I can do... // TODO fix
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

        MFXFontIcon bell = new MFXFontIcon("mfx-bell-alt", 36);
        bellWrapped = new MFXIconWrapper(bell, 56);
        bellWrapped.getStyleClass().add("notifications-icon");

        counter = new NotificationsCounter();
        counter.setManaged(false);

        MFXLabel headerLabel = new MFXLabel();
        headerLabel.textProperty().bind(notificationCenter.headerTextPropertyProperty());
        headerLabel.setAlignment(Pos.CENTER_LEFT);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(headerLabel, Priority.ALWAYS);

        MFXToggleButton dndToggle = new MFXToggleButton("Do not disturb");
        dndToggle.setContentDisplay(ContentDisplay.RIGHT);
        dndToggle.setGraphicTextGap(15);
        notificationCenter.doNotDisturbProperty().bindBidirectional(dndToggle.selectedProperty());

        HBox header = new HBox(headerLabel, dndToggle);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        MFXIconWrapper select = new MFXIconWrapper(new MFXFontIcon("mfx-variant13-mark", 24), 36).defaultRippleGeneratorBehavior();
        MFXIconWrapper markAsRead = new MFXIconWrapper(new MFXFontIcon("mfx-eye", 20), 36).defaultRippleGeneratorBehavior();
        MFXIconWrapper markAsUnread = new MFXIconWrapper(new MFXFontIcon("mfx-eye-slash", 20), 36).defaultRippleGeneratorBehavior();
        MFXIconWrapper dismiss = new MFXIconWrapper(new MFXFontIcon("mfx-delete", 20), 36).defaultRippleGeneratorBehavior();

        select.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.setSelectionMode(!notificationCenter.isSelectionMode()));
        markAsRead.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.markSelectedNotificationsAs(NotificationState.READ));
        markAsUnread.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.markSelectedNotificationsAs(NotificationState.UNREAD));
        dismiss.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> notificationCenter.dismissSelected());

        NodeUtils.makeRegionCircular(select);
        NodeUtils.makeRegionCircular(markAsRead);
        NodeUtils.makeRegionCircular(markAsUnread);
        NodeUtils.makeRegionCircular(dismiss);

        HBox actions = new HBox(50, select, markAsRead, markAsUnread, dismiss);
        actions.getStyleClass().add("actions");
        actions.setAlignment(Pos.CENTER);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(header);
        borderPane.setCenter(virtualFlow);
        borderPane.setBottom(actions);
        borderPane.getStyleClass().add("notifications-container");
        VBox.setVgrow(borderPane, Priority.ALWAYS);

        VBox popupContent = new VBox(borderPane);
        popupContent.setAlignment(Pos.TOP_CENTER);
        popupContent.paddingProperty().bind(Bindings.createObjectBinding(
                () -> InsetsFactory.top(notificationCenter.getPopupSpacing()),
                notificationCenter.popupSpacingProperty()
        ));
        popupContent.setMinHeight(Region.USE_PREF_SIZE);
        popupContent.setMaxHeight(Region.USE_PREF_SIZE);
        popupContent.prefWidthProperty().bind(notificationCenter.popupWidthProperty());
        popupContent.prefHeightProperty().bind(notificationCenter.popupHeightProperty());

        popup = new MFXPopup(popupContent) {
            @Override
            public Styleable getStyleableParent() {
                return MFXNotificationCenterSkin.this.getSkinnable();
            }
        };
        popup.setAnimated(false);
        popup.setConsumeAutoHidingEvents(false); // TODO can't fix as of now, JavaFX 17.0.1
        popup.getStyleClass().addAll(getSkinnable().getStyleClass());

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
        bellWrapped.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            bellWrapped.requestFocus();
            notificationCenter.getOnIconClicked().handle(event);
        });
        notificationCenter.showingProperty().addListener((observable, oldValue, newValue) -> managePopup(newValue));
        notificationCenter.addEventFilter(MFXPopupEvent.REPOSITION_EVENT, event -> popup.reposition());
        notificationCenter.popupHoverProperty().bind(popup.hoverProperty());
    }

    /**
     * Shows/Hides the popup.
     */
    protected void managePopup(boolean showing) {
        if (!showing) {
            popup.hide();
            return;
        }
        popup.show(bellWrapped, HPos.CENTER, VPos.BOTTOM);
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

            getStyleClass().add("counter");
            visibleProperty().bind(notificationCenter.unreadCountProperty().greaterThan(0));
            setIcon(counterText);
        }
    }
}
