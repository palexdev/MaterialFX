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

import io.github.palexdev.materialfx.controls.MFXScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.layout.StackPane;

/**
 * Skin used for {@link MFXScrollPane}, this class' purpose is to
 * fix a bug of ScrollPanes' viewport which makes the content blurry.
 * <p>
 * Luckily achieved without reflection :D
 */
public class MFXScrollPaneSkin extends ScrollPaneSkin {

	public MFXScrollPaneSkin(MFXScrollPane scrollPane) {
		super(scrollPane);
		StackPane viewPort = (StackPane) scrollPane.lookup(".viewport");
		viewPort.setCache(false);
	}
}
