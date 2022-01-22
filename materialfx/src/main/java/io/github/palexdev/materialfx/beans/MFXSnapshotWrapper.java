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

package io.github.palexdev.materialfx.beans;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class used in various controls as a workaround for showing a node two or more times on the scene graph.
 * <p>
 * Makes a screenshot of a node with transparent background.
 * Then {@link #getGraphic()} should be used to get an ImageView node which contains the screenshot.
 * <p></p>
 * A little side note: since it is a screenshot the image may appear a little blurry compared to the real
 * graphic, however it should be acceptable and I believe this is still better than having no graphic at all.
 */
public class MFXSnapshotWrapper {
	private final WritableImage snapshot;

	public MFXSnapshotWrapper(Node node) {
		SnapshotParameters snapshotParameters = new SnapshotParameters();
		snapshotParameters.setFill(Color.TRANSPARENT);
		snapshotParameters.setDepthBuffer(true);
		snapshot = node.snapshot(snapshotParameters, null);
	}

	public Node getGraphic() {
		return new ImageView(snapshot);
	}
}