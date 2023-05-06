package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.skins.MFXPopupSkin;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;

/**
 * Public API for all MaterialFX popups, extends {@link MFXStyleable}.
 *
 * @see MFXPopupSkin
 */
public interface IMFXPopup extends MFXStyleable {
    Node getContent();

    /**
     * Specifies the popup's content.
     */
    NodeProperty contentProperty();

    void setContent(Node content);

    Bounds getContentBounds();

    /**
     * The layout bounds of its content. This a read-only property, automatically managed by the default skin,
     * can be used by implementations to correctly compute the popup position.
     */
    ReadOnlyObjectProperty<Bounds> contentBoundsProperty();

    boolean isHover();

    /**
     * MaterialFX popups should offer the possibility of checking whether the mouse is on them. For example this
     * allows tooltips to stay open if the mouse is on the content. This is also a read-only property, and it's managed
     * by the default skin.
     */
    ReadOnlyBooleanProperty hoverProperty();

    boolean isAnimated();

    /**
     * Ideally MaterialFX popups should be animated for a pleasant user experience, but users should be allowed to decide
     * whether to keep animations enabled or disabled as per their needs.
     */
    BooleanProperty animatedProperty();

    void setAnimated(boolean animated);

    Position getOffset();

    /**
     * For ease of use, popups should allow users to specify an x delta and a y delta that can be applied to the final
     * computed position to shift the popup as needed. This can be useful for example when showing a popup next to a Node
     * or a Window, and we want some spacing/gap in between.
     * <p>
     * It's implemented through a single property, {@link PositionProperty}.
     * Implementations are free to decide how and when to use these offsets.
     */
    PositionProperty offsetProperty();

    void setOffset(Position offset);

    /**
     * This API is intended to be used with {@link PopupControl}s, which already have their internal ways of styling
     * the popup (the styleable parent override). However, MaterialFX popups should offer a list of stylesheets that will
     * be added directly on the popup's content. The pros of this are that the user is not forced to override the styleable
     * parent method inline if he doesn't want to, and he can style the popup directly without any other node in between.
     */
    ObservableList<String> getStylesheets();

    /**
     * MaterialFX popups may end up overriding the {@code hide()} method to perform some other actions
     * (like animation hooks for example).
     * For such reasons, the API offers an alternative method to hide the popup, implementations are free to
     * define its behavior. Usually it just calls {@code super.hide()}.
     */
    void close();
}