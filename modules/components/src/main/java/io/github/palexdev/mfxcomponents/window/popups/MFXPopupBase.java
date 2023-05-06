package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.skins.base.IMFXPopupSkin;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.utils.PositionUtils;
import io.github.palexdev.mfxcore.utils.fx.LayoutUtils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;

import java.lang.ref.WeakReference;

/**
 * We fellow Java developers well know that this language doesn't support multiple inheritance (what a shame).
 * However, this still doesn't prevent us from 'emulating' it using a technique called 'composition over inheritance'
 * <p>
 * This class is exactly this. It defines common properties and behaviors for MaterialFX popups that are forced to already
 * extend one of the JavaFX's classes. At the time of writing this, {@link MFXPopup} and {@link MFXTooltip} share this
 * same API. Some of the methods specified by the {@link IMFXPopup} interface will delegate to this.
 * <p></p>
 * Here's the list of properties and behaviors defined by this class:
 * <p> 1) The popup's content property
 * <p> 2) The popup's content bounds, see {@link IMFXPopup#contentBoundsProperty()}
 * <p> 3) The hover state, see {@link IMFXPopup#hoverProperty()}
 * <p> 4) The animations on/off switch property
 * <p> 5) The offset to shift the popup's position, see {@link IMFXPopup#offsetProperty()}
 * <p> 6) The stylesheets that will be added directly on the popup's root node, see {@link IMFXPopup#getStylesheets()}
 * <p> 7) Keeps references to the popup's owner, the chosen anchor at which to show the popup and the popup's skin (needed for animations)
 * <p></p>
 * For ease of use, MaterialFX popups offer a new way to show them, it's enough to give a Node or Window and then a
 * {@link Pos} (referred to as 'anchor') at which the popup will be positioned. There are a series of methods which are
 * responsible for computing the position starting from these two parameters. There's also a repositioning method that can
 * be used to correctly position the popup when the content bounds change, or when the owner position/sizes change
 * (at the time of writing, this must be implemented by the user), for example it's even possible to make a stay at the
 * chosen anchor as a Window or Node moves, the result is pretty good too! This reposition method needs the reference to
 * the owner, this is why it's stored here in a {@link WeakReference}. For Tooltips the situation is slightly different
 * as they need an owner to show, it's not optional as for standard popups.
 */
public class MFXPopupBase {
    //================================================================================
    // Properties
    //================================================================================
    private final NodeProperty content = new NodeProperty();
    private final ReadOnlyObjectWrapper<Bounds> contentBounds = new ReadOnlyObjectWrapper<>(LayoutUtils.emptyBounds()) {
        @Override
        protected void invalidated() {
            onContentBoundsChanged();
        }
    };
    private final ReadOnlyBooleanWrapper hover = new ReadOnlyBooleanWrapper(false);
    private final BooleanProperty animated = new SimpleBooleanProperty(true);
    private final PositionProperty offset = new PositionProperty(Position.origin());

    private final ObservableList<String> stylesheets = FXCollections.observableArrayList();

    private WeakReference<Node> owner;
    private Pos anchor;
    private IMFXPopupSkin skin;

    //================================================================================
    // Methods
    //================================================================================

    /**
     * This is responsible for computing the position of a popup, relative to an owner Node and given the anchor
     * at which it will be shown.
     * <p>
     * This will delegate to {@link #computePosition(Size, Point2D, Pos)}. The size is retrieved from the owner's bounds,
     * the origin is retrieved using {@link Node#localToScreen(double, double)}.
     */
    protected Position computePosition(Pos anchor) {
        Node owner = getOwner();
        Size bounds = (owner != null) ?
                Size.of(owner.getLayoutBounds().getWidth(), owner.getLayoutBounds().getHeight()) :
                Size.empty();
        Point2D origin = (owner != null) ?
                owner.localToScreen(0, 0) :
                Point2D.ZERO;
        return computePosition(bounds, origin, anchor);
    }

    /**
     * Given the size of a Node or Window, its position <b>on the screen</b>, and the anchor at which show the popup,
     * computes both the x and y coordinates.
     * <p>
     * Delegates to: {@link #computeX(Size, Point2D, Pos)} and {@link #computeY(Size, Point2D, Pos)}.
     *
     * @param bounds the Node or Window sizes
     * @param origin the Node or Window position on the screen
     * @param anchor the anchor at which show the popup
     */
    protected Position computePosition(Size bounds, Point2D origin, Pos anchor) {
        double x = computeX(bounds, origin, anchor);
        double y = computeY(bounds, origin, anchor);
        return Position.of(x, y);
    }

    /**
     * Given the size of a Node or Window, its position <b>on the screen</b>, and the anchor at which show the popup,
     * computes x coordinate.
     * <p>
     * <p> 1) For LEFT: {@code x = originX - contentWidth - xOffset}
     * <p> 2) For RIGHT: {@code x = originX + ownerWidth + xOffset}
     * <p> 3) For CENTER: {@code x = originX + (ownerWidth - contentWidth) / 2} (offset is ignored here!)
     */
    protected double computeX(Size bounds, Point2D origin, Pos anchor) {
        double x;
        double offset = getOffset().getX();
        if (PositionUtils.isLeft(anchor)) {
            x = origin.getX() - getContentBounds().getWidth() - offset;
        } else if (PositionUtils.isRight(anchor)) {
            x = origin.getX() + bounds.getWidth() + offset;
        } else {
            x = origin.getX() + (bounds.getWidth() - getContentBounds().getWidth()) / 2;
        }
        return x;
    }

    /**
     * Given the size of a Node or Window, its position <b>on the screen</b>, and the anchor at which show the popup,
     * computes x coordinate.
     * <p>
     * <p> 1) For TOP: {@code y = originY - contentHeight - yOffset}
     * <p> 2) For BOTTOM: {@code y = originY + ownerHeight + yOffset}
     * <p> 3) For CENTER: {@code y = originY + (ownerHeight - contentHeight) / 2} (offset is ignored here!)
     */
    protected double computeY(Size bounds, Point2D origin, Pos anchor) {
        double y;
        double offset = getOffset().getY();
        if (PositionUtils.isTop(anchor)) {
            y = origin.getY() - getContentBounds().getHeight() - offset;
        } else if (PositionUtils.isBottom(anchor)) {
            y = origin.getY() + bounds.getHeight() + offset;
        } else {
            y = origin.getY() + (bounds.getHeight() - getContentBounds().getHeight()) / 2;
        }
        return y;
    }

    /**
     * This is responsible for re-computing the popup position using {@link #computePosition(Pos)}.
     * <p></p>
     * If the owner ({@link #getOwner()}) is null, returns null. If no anchor was specified before,
     * defaults to {@link Pos#BOTTOM_CENTER}.
     */
    protected Position reposition() {
        if (getOwner() == null) return null;
        if (anchor == null) setAnchor(Pos.BOTTOM_CENTER);
        return computePosition(anchor);
    }

    /**
     * Empty. This can be overridden inline as needed, to perform actions when the popup's content bounds change.
     * This is the perfect place to reposition the popup when showing for the first time.
     */
    protected void onContentBoundsChanged() {
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public Node getContent() {
        return content.get();
    }

    /**
     * @see IMFXPopup#contentProperty()
     */
    public NodeProperty contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public Bounds getContentBounds() {
        return contentBounds.get();
    }

    /**
     * @see IMFXPopup#contentBoundsProperty()
     */
    public ReadOnlyObjectWrapper<Bounds> contentBoundsProperty() {
        return contentBounds;
    }

    protected void setContentBounds(Bounds contentBounds) {
        this.contentBounds.set(contentBounds);
    }

    public boolean isHover() {
        return hover.get();
    }

    /**
     * @see IMFXPopup#hoverProperty()
     */
    public ReadOnlyBooleanProperty hoverProperty() {
        return hover;
    }

    protected void setHover(boolean hover) {
        this.hover.set(hover);
    }

    public boolean isAnimated() {
        return animated.get();
    }

    /**
     * @see IMFXPopup#animatedProperty()
     */
    public BooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public Position getOffset() {
        return offset.get();
    }

    /**
     * @see IMFXPopup#offsetProperty()
     */
    public PositionProperty offsetProperty() {
        return offset;
    }

    public void setOffset(Position offset) {
        this.offset.set(offset);
    }

    /**
     * @see IMFXPopup#getStylesheets()
     */
    public ObservableList<String> getStylesheets() {
        return stylesheets;
    }

    /**
     * @return the {@link WeakReference} object containing the popup's owner reference
     */
    protected WeakReference<Node> getOwnerRef() {
        return owner;
    }

    /**
     * Sets the {@link WeakReference} object containing the popup's owner reference.
     */
    protected void setOwnerRef(WeakReference<Node> ownerRef) {
        this.owner = ownerRef;
    }

    /**
     * Unwraps the {@link WeakReference} used to store the popup's owner reference.
     * If the {@link WeakReference} is null, returns null.
     */
    public Node getOwner() {
        return (owner != null) ? owner.get() : null;
    }

    /**
     * Sets the {@link WeakReference} used to store the popup's owner reference to a new {@link WeakReference} object
     * built on the given owner reference.
     */
    protected void setOwner(Node owner) {
        this.owner = new WeakReference<>(owner);
    }

    /**
     * @return the last 'anchor' at which the popup was shown
     */
    public Pos getAnchor() {
        return anchor;
    }

    /**
     * Sets the anchor at which the popup will be shown.
     */
    public void setAnchor(Pos anchor) {
        this.anchor = anchor;
    }

    /**
     * The popup' skin instance. If not already build (after the first time it has been shown), returns null.
     */
    public IMFXPopupSkin getSkin() {
        return skin;
    }

    /**
     * Sets the popup' skin instance.
     */
    protected void setSkin(IMFXPopupSkin skin) {
        this.skin = skin;
    }
}
