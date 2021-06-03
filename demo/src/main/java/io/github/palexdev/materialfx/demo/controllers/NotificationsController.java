/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.demo.controllers;

import io.github.palexdev.materialfx.controls.MFXDialog;
import io.github.palexdev.materialfx.controls.MFXNotification;
import io.github.palexdev.materialfx.controls.SimpleMFXNotificationPane;
import io.github.palexdev.materialfx.controls.base.AbstractMFXDialog;
import io.github.palexdev.materialfx.controls.enums.DialogType;
import io.github.palexdev.materialfx.controls.factories.MFXDialogFactory;
import io.github.palexdev.materialfx.notifications.NotificationPos;
import io.github.palexdev.materialfx.notifications.NotificationsManager;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Random;

public class NotificationsController {
    private final Random random = new Random(System.currentTimeMillis());

    private final String title = "MaterialFX Notification System";
    private final String dummy =
            "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                    "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                    "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                    "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                    "remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, " +
                    "and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

    @FXML
    void showTopLeft() {
        NotificationPos pos = NotificationPos.TOP_LEFT;
        showNotification(pos);
    }

    @FXML
    void showTopCenter() {
        NotificationPos pos = NotificationPos.TOP_CENTER;
        showNotification(pos);
    }

    @FXML
    void showTopRight() {
        NotificationPos pos = NotificationPos.TOP_RIGHT;
        showNotification(pos);
    }

    @FXML
    void showBottomLeft() {
        NotificationPos pos = NotificationPos.BOTTOM_LEFT;
        showNotification(pos);
    }

    @FXML
    void showBottomCenter() {
        NotificationPos pos = NotificationPos.BOTTOM_CENTER;
        showNotification(pos);
    }

    @FXML
    void showBottomRight() {
        NotificationPos pos = NotificationPos.BOTTOM_RIGHT;
        showNotification(pos);
    }

    private void showNotification(NotificationPos pos) {
        MFXNotification notification = buildNotification();
        NotificationsManager.send(pos, notification);
    }

    private MFXNotification buildNotification() {
        Region template = getRandomTemplate();
        MFXNotification notification = new MFXNotification(template, true, true);
        notification.setHideAfterDuration(Duration.seconds(3));

        if (template instanceof SimpleMFXNotificationPane) {
            SimpleMFXNotificationPane pane = (SimpleMFXNotificationPane) template;
            pane.setCloseHandler(closeEvent -> notification.hideNotification());
        } else {
            MFXDialog dialog = (MFXDialog) template;
            dialog.setCloseHandler(closeEvent -> notification.hideNotification());
        }

        return notification;
    }

    private Region getRandomTemplate() {
        final int rand = random.nextInt(4);

        switch (rand) {
            case 0:
                FontIcon icon1 = new FontIcon("fas-info-circle");
                icon1.setIconColor(Color.LIGHTBLUE);
                icon1.setIconSize(15);
                return new SimpleMFXNotificationPane(
                        icon1,
                        "Dummy Notification",
                        title,
                        dummy
                );
            case 1:
                FontIcon icon2 = new FontIcon("fas-cocktail");
                icon2.setIconColor(Color.GREEN);
                icon2.setIconSize(15);
                return new SimpleMFXNotificationPane(
                        icon2,
                        "Fast Food",
                        title,
                        "Hello username, your order is on the way!"
                );
            case 2:
                FontIcon icon3 = new FontIcon("fab-whatsapp");
                icon3.setIconColor(Color.GREEN);
                icon3.setIconSize(15);
                return new SimpleMFXNotificationPane(
                        icon3,
                        "Whatsapp Notification",
                        title,
                        "Hi Mark, it's been ages since we last spoke!\nHow are you?"
                );
            case 3:
                AbstractMFXDialog dialog = MFXDialogFactory.buildDialog(DialogType.WARNING, "Warning Dialog as Notification", "Disk space is running low, better watch out...");
                dialog.setVisible(true);
                return dialog;
            default:
                return null;
        }
    }
}
