package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;

public interface IMFXPopup extends MFXStyleable {
    Node getContent();

    NodeProperty contentProperty();

    void setContent(Node content);

    Bounds getContentBounds();

    ReadOnlyObjectProperty<Bounds> contentBoundsProperty();

    boolean isHover();

    ReadOnlyBooleanProperty hoverProperty();

    boolean isAnimated();

    BooleanProperty animatedProperty();

    void setAnimated(boolean animated);

    Position getOffset();

    PositionProperty offsetProperty();

    void setOffset(Position offset);

    ObservableList<String> getStylesheets();

    void close();
}
