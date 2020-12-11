package io.github.palexdev.materialfx.beans;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class used in {@link MFXComboBox}, workaround for showing the item graphic if is is a node.
 * <p>
 * Makes a screenshot of the graphic node with transparent background.
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