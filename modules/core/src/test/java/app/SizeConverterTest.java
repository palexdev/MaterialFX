/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package app;

import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableSizeProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableSizeProperty.SizeConverter;
import io.github.palexdev.mfxcore.utils.fx.ColorUtils;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;

import static io.github.palexdev.mfxcore.utils.RandomUtils.random;

public class SizeConverterTest extends Application {

	public void start(Stage stage) {
		CustomPane pane = new CustomPane();
		pane.setId("custom");
		pane.size.addListener((observable, oldValue, newValue) -> System.out.println(newValue));

		Rectangle rt = new Rectangle();
		rt.widthProperty().bind(Bindings.createDoubleBinding(
				() -> pane.getSize().getWidth(),
				pane.sizeProperty()
		));
		rt.heightProperty().bind(Bindings.createDoubleBinding(
				() -> pane.getSize().getHeight(),
				pane.sizeProperty()
		));
		Button button = new Button("Set Style");
		button.setOnAction(event -> {
			pane.setStyle(String.format("-fx-size: \"%d %d\"", random.nextInt(100, 400), random.nextInt(100, 400)));
			rt.setFill(ColorUtils.getRandomColor());
		});
		VBox box = new VBox(30, rt, button);
		box.setAlignment(Pos.CENTER);

		pane.getChildren().add(box);
		stage.setScene(new Scene(pane, 600, 600));
		stage.show();
	}

	private static class CustomPane extends Pane {
		private final StyleableSizeProperty size = new StyleableSizeProperty(
				StyleableProperties.SIZE,
				this,
				"size",
				Size.of(100, 100)
		);

		public Size getSize() {
			return size.get();
		}

		public StyleableSizeProperty sizeProperty() {
			return size;
		}

		public void setSize(Size size) {
			this.size.set(size);
		}

		@Override
		public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
			return getClassCssMetaData();
		}

		private static class StyleableProperties {
			private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

			private static final CssMetaData<CustomPane, Size> SIZE = new CssMetaData<>(
					"-fx-size", SizeConverter.getInstance(), Size.of(100, 100)
			) {
				@Override
				public boolean isSettable(CustomPane styleable) {
					return !styleable.sizeProperty().isBound();
				}

				@Override
				public StyleableProperty<Size> getStyleableProperty(CustomPane styleable) {
					return styleable.sizeProperty();
				}
			};

			static {
				cssMetaDataList = StyleUtils.cssMetaDataList(
						Pane.getClassCssMetaData(),
						SIZE
				);
			}
		}

		public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
			return StyleableProperties.cssMetaDataList;
		}
	}
}
