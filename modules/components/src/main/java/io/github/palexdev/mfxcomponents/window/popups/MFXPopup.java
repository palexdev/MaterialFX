package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.stage.Window;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public class MFXPopup extends PopupControl implements IMFXPopup {
    //================================================================================
    // Properties
    //================================================================================
    final MFXPopupBase base = new MFXPopupBase() {
        @Override
        protected void onContentBoundsChanged() {
            Bounds bounds = getContentBounds();
            if (bounds.getWidth() > 0 && bounds.getHeight() > 0)
                MFXPopup.this.reposition();
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXPopup() {
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setAutoFix(true);
        setAutoHide(true);
        setHideOnEscape(true);
        skinProperty().addListener(i -> setSkin(null));
    }

    public void show(Node owner) {
        show(owner, Pos.BOTTOM_CENTER);
    }

    public final void close() {
        super.hide();
    }

    public void show(Node owner, Pos anchor) {
        assert owner != null;
        if (owner.getScene() == null || owner.getScene().getWindow() == null)
            throw new IllegalStateException("Cannot show the popup. The node must be attached to a scene/window!");
        setOwner(owner);
        setAnchor(anchor);
        Position pos = computePosition(anchor);
        show(owner, pos.getX(), pos.getY());
    }

    public void reposition() {
        Position position = base.reposition();
        setAnchorX(position.getX());
        setAnchorY(position.getY());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected void show() {
        if (getContent() == null)
            throw new IllegalStateException("Popup content is null!");
        if (!isAutoHide() && isShowing()) {
            close();
            return;
        }
        retrieveSkin().ifPresent(MFXPopupSkin::animateIn);
        super.show();
    }

    @Override
    public void hide() {
        /*
         * This check is due to a JavaFX bug in WindowStage.setBounds(...) method which doesn't check for the window
         * to be not null before doing anything on it.
         * This seems to happen when the Popup is open but the main window is being closed.
         * Animations at such stage cannot be used
         */
        boolean showing = Optional.ofNullable(getOwner())
            .map(o -> o.getScene().getWindow())
            .map(Window::isShowing)
            .orElse(true);
        if (!showing) {
            super.hide();
            return;
        }

        retrieveSkin().ifPresentOrElse(
            MFXPopupSkin::animateOut,
            super::hide
        );
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXPopupSkin<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-popup");
    }

    //================================================================================
    // Delegate Methods
    //================================================================================
    public Position computePosition(Pos anchor) {
        return base.computePosition(anchor);
    }

    public double computeX(Node owner, Point2D origin, Pos anchor) {
        return base.computeX(owner, origin, anchor);
    }

    public double computeY(Node owner, Point2D origin, Pos anchor) {
        return base.computeY(owner, origin, anchor);
    }

    public Node getContent() {
        return base.getContent();
    }

    public NodeProperty contentProperty() {
        return base.contentProperty();
    }

    public void setContent(Node content) {
        base.setContent(content);
    }

    public Bounds getContentBounds() {
        return base.getContentBounds();
    }

    public ReadOnlyObjectProperty<Bounds> contentBoundsProperty() {
        return base.contentBoundsProperty();
    }

    @Override
    public boolean isHover() {
        return base.isHover();
    }

    @Override
    public ReadOnlyBooleanProperty hoverProperty() {
        return base.hoverProperty();
    }

    protected void setContentBounds(Bounds contentBounds) {
        base.setContentBounds(contentBounds);
    }

    public boolean isAnimated() {
        return base.isAnimated();
    }

    public BooleanProperty animatedProperty() {
        return base.animatedProperty();
    }

    public void setAnimated(boolean animated) {
        base.setAnimated(animated);
    }

    public Position getOffset() {
        return base.getOffset();
    }

    public PositionProperty offsetProperty() {
        return base.offsetProperty();
    }

    public void setOffset(Position offset) {
        base.setOffset(offset);
    }

    public ObservableList<String> getStylesheets() {
        return base.getStylesheets();
    }

    protected WeakReference<Node> getOwnerRef() {
        return base.getOwnerRef();
    }

    protected Node getOwner() {
        return base.getOwner();
    }

    protected void setOwner(Node owner) {
        base.setOwner(owner);
    }

    protected Pos getAnchor() {
        return base.getAnchor();
    }

    protected void setAnchor(Pos anchor) {
        base.setAnchor(anchor);
    }

    protected Optional<MFXPopupSkin<?>> retrieveSkin() {
        if (base.getSkin() == null) {
            try {
                base.setSkin(((MFXPopupSkin<?>) getSkin()));
            } catch (Exception ex) {
                base.setSkin(null);
            }
        }
        return Optional.ofNullable(base.getSkin());
    }

    protected void setSkin(MFXPopupSkin<?> skin) {
        base.setSkin(skin);
    }

    //================================================================================
    // Getters
    //================================================================================
    protected MFXPopupBase getBase() {
        return base;
    }
}
