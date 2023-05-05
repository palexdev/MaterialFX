package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcore.base.beans.Position;
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
    private MFXPopupSkin<?> skin;

    //================================================================================
    // Methods
    //================================================================================
    protected Position computePosition(Pos anchor) {
        Node owner = getOwner();
        if (owner == null) return Position.origin();
        Point2D origin = owner.localToScreen(0, 0);
        double x = computeX(owner, origin, anchor);
        double y = computeY(owner, origin, anchor);
        return Position.of(x, y);
    }

    protected double computeX(Node owner, Point2D origin, Pos anchor) {
        double x;
        double offset = getOffset().getX();
        Bounds ownerBounds = owner.getLayoutBounds();
        if (PositionUtils.isLeft(anchor)) {
            x = origin.getX() - getContentBounds().getWidth() - offset;
        } else if (PositionUtils.isRight(anchor)) {
            x = origin.getX() + ownerBounds.getWidth() + offset;
        } else {
            x = origin.getX() + (ownerBounds.getWidth() - getContentBounds().getWidth()) / 2; // TODO mention no offset in docs
        }
        return x;
    }

    protected double computeY(Node owner, Point2D origin, Pos anchor) {
        double y;
        double offset = getOffset().getY();
        if (PositionUtils.isTop(anchor)) {
            y = origin.getY() - getContentBounds().getHeight() - offset;
        } else if (PositionUtils.isBottom(anchor)) {
            y = origin.getY() + owner.getLayoutBounds().getHeight() + offset;
        } else {
            y = origin.getY() + (owner.getLayoutBounds().getHeight() - getContentBounds().getHeight()) / 2; // TODO mention no offset in docs
        }
        return y;
    }

    protected Position reposition() {
        if (getOwner() == null) return Position.origin();
        if (anchor == null) setAnchor(Pos.BOTTOM_CENTER);
        return computePosition(anchor);
    }

    protected void onContentBoundsChanged() {
    }

    //================================================================================
    // Getters/Setters
    //================================================================================

    public Node getContent() {
        return content.get();
    }

    public NodeProperty contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public Bounds getContentBounds() {
        return contentBounds.get();
    }

    public ReadOnlyObjectWrapper<Bounds> contentBoundsProperty() {
        return contentBounds;
    }

    protected void setContentBounds(Bounds contentBounds) {
        this.contentBounds.set(contentBounds);
    }

    public boolean isHover() {
        return hover.get();
    }

    public ReadOnlyBooleanProperty hoverProperty() {
        return hover;
    }

    protected void setHover(boolean hover) {
        this.hover.set(hover);
    }

    public boolean isAnimated() {
        return animated.get();
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public Position getOffset() {
        return offset.get();
    }

    public PositionProperty offsetProperty() {
        return offset;
    }

    public void setOffset(Position offset) {
        this.offset.set(offset);
    }

    public ObservableList<String> getStylesheets() {
        return stylesheets;
    }

    protected WeakReference<Node> getOwnerRef() {
        return owner;
    }

    protected Node getOwner() {
        return (owner != null) ? owner.get() : null;
    }

    protected void setOwner(Node owner) {
        this.owner = new WeakReference<>(owner);
    }

    protected Pos getAnchor() {
        return anchor;
    }

    protected void setAnchor(Pos anchor) {
        this.anchor = anchor;
    }

    protected MFXPopupSkin<?> getSkin() {
        return skin;
    }

    protected void setSkin(MFXPopupSkin<?> skin) {
        this.skin = skin;
    }
}
