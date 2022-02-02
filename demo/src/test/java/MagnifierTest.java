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

import io.github.palexdev.materialfx.controls.MFXMagnifierPane;
import io.github.palexdev.materialfx.utils.ColorUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class MagnifierTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane bp = new BorderPane();

		ImageView image = new ImageView("http://img-store.mypracticalskills.com/img-store/color-library/rgb-small-28.jpg");
		MFXMagnifierPane mp = new MFXMagnifierPane(image);
		mp.setLensSize(50);
		mp.setMaxSize(300, 300);
		mp.magnifierViewProperty().addListener(invalidated -> mp.updatePickedColor());
		mp.setColorConverter(FunctionalStringConverter.to(ColorUtils::toHSB));
		mp.setPickerPos(VPos.TOP);
		mp.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.SECONDARY) mp.setLensSize(100);
		});

		bp.setCenter(mp);
		Scene scene = new Scene(bp, 800, 800);
		primaryStage.setScene(scene);
		primaryStage.show();

		ScenicView.show(scene);
	}
}
