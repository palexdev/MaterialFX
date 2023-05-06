package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.skins.MFXPopupSkin;
import io.github.palexdev.mfxcomponents.skins.base.IMFXPopupSkin;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
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

/**
 * Most generic type of popup. Extends {@link PopupControl} and implements {@link IMFXPopup}, makes use of:
 * <p> - {@link MFXPopupBase} for common properties and behaviors
 * <p> - {@link MFXPopupSkin} as its default skin
 * <p> - Since {@link IMFXPopup} also extends {@link MFXStyleable}, the default style class is set to be '.mfx-popup'
 * <p></p>
 * Aside from the common properties and behaviors, and the architecture inherited by {@link PopupControl}, {@code MFXPopup}
 * wants to make the user experience better by offering methods/features that will make its handling super easy.
 * <p>
 * There are 4 new main methods:
 * <p> 1) {@link #show(Node, Pos)}
 * <p> 2) {@link #show(Window, Pos)}
 * <p> 3) {@link #reposition()}
 * <p> 4) {@link #windowReposition()}
 * <p></p>
 * The popup is automatically set to: auto hide, auto fix, and exit on escape.
 */
public class MFXPopup extends PopupControl implements IMFXPopup {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXPopupBase base = new MFXPopupBase() {
        @Override
        protected void onContentBoundsChanged() {
            Bounds bounds = getContentBounds();
            if (bounds.getWidth() > 0 && bounds.getHeight() > 0) {
                if (getOwner() != null) {
                    MFXPopup.this.reposition();
                }
                if (getOwnerWindow() != null) {
                    windowReposition();
                }
            }
        }
    };
    private WeakReference<Window> windowOwner;

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
        skinProperty().addListener(i -> setSkin((IMFXPopupSkin) null));
    }

    /**
     * Delegates to {@link #show(Node, Pos)} with {@link Pos#BOTTOM_CENTER} as anchor.
     */
    public void show(Node owner) {
        show(owner, Pos.BOTTOM_CENTER);
    }

    /**
     * Delegates to {@link #show(Window, Pos)} with {@link Pos#CENTER} as anchor.
     * <p>
     * Side note: it's named 'windowShow' to not conflict with {@link #show(Window)}.
     */
    public void windowShow(Window window) {
        show(window, Pos.CENTER);
    }

    /**
     * Uses {@link #computePosition(Pos)} to compute the position at which the popup will be shown, using the given
     * parameters, then delegates to {@link #show(Node, double, double)}.
     *
     * @throws IllegalArgumentException if the given owner is null
     * @throws IllegalStateException    if the given owner is not in a Scene or if the owner' Scene is not in a Window
     */
    public void show(Node owner, Pos anchor) {
        if (owner == null) throw new IllegalArgumentException("Owner cannot be null!");
        if (owner.getScene() == null || owner.getScene().getWindow() == null)
            throw new IllegalStateException("Cannot show the popup. The node must be attached to a scene/window!");
        windowOwner = null;
        setOwner(owner);
        setAnchor(anchor);
        Position pos = computePosition(anchor);
        show(owner, pos.getX(), pos.getY());
    }

    /**
     * Uses {@link #computePosition(Size, Point2D, Pos)} to compute the position at which the popup will be shown, using the given
     * parameters, then delegates to {@link #show(Window, double, double)}.
     *
     * @throws IllegalArgumentException if the given owner is null
     * @throws IllegalStateException    if the given owner Window is not showing
     */
    public void show(Window window, Pos anchor) {
        if (window == null) throw new IllegalArgumentException("Owner cannot be null!");
        if (!window.isShowing())
            throw new IllegalStateException("The given window is still hidden!");
        base.setOwnerRef(null);
        setWindowOwner(window);
        setAnchor(anchor);
        Size bounds = Size.of(window.getWidth(), window.getHeight());
        Point2D origin = new Point2D(window.getX(), window.getY());
        Position pos = computePosition(bounds, origin, anchor);
        show(window, pos.getX(), pos.getY());
    }

    /**
     * Simply calls {@code super.hide()}, since the method has been overridden here for the close animation to play
     * properly.
     */
    public final void close() {
        super.hide();
    }

    /**
     * Re-computes the popup's position using {@link MFXPopupBase#reposition()}, if the result is not a null Position,
     * then proceeds to set both {@link #anchorXProperty()} and {@link #anchorYProperty()} with the new values.
     */
    public void reposition() {
        Position position = base.reposition();
        if (position == null) return;
        setAnchorX(position.getX());
        setAnchorY(position.getY());
    }

    /**
     * This is the same as {@link #reposition()}, but should be used exclusively when showing the popup with
     * a Window owner.
     */
    public void windowReposition() {
        Window window = getWindowOwner();
        if (window == null) return;
        Size bounds = Size.of(window.getWidth(), window.getHeight());
        Point2D origin = new Point2D(window.getX(), window.getY());
        Position pos = computePosition(bounds, origin, getAnchor());
        setAnchorX(pos.getX());
        setAnchorY(pos.getY());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to play the open animation defined in the skin before calling {@code super.show()}.
     *
     * @throws IllegalStateException if the popup's content has not been set
     */
    @Override
    protected void show() {
        if (getContent() == null)
            throw new IllegalStateException("Popup content is null!");
        if (!isAutoHide() && isShowing()) {
            close();
            return;
        }
        retrieveSkin().ifPresent(IMFXPopupSkin::animateIn);
        super.show();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to play the close animation defined in the skin.
     * <p>
     * There's an important difference here from {@link #show()}. When the hide method is called the popup is
     * closed immediately, meaning that the animation won't be shown. For this reason, the defined close animation
     * is responsible for calling {@link #close()} once the animation has ended.
     */
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
                IMFXPopupSkin::animateOut,
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

    /**
     * Delegates to {@link MFXPopupBase#computePosition(Pos)}.
     */
    public Position computePosition(Pos anchor) {
        return base.computePosition(anchor);
    }

    /**
     * Delegates to {@link MFXPopupBase#computePosition(Size, Point2D, Pos)}.
     */
    public Position computePosition(Size bounds, Point2D origin, Pos anchor) {
        return base.computePosition(bounds, origin, anchor);
    }

    /**
     * Delegates to {@link MFXPopupBase#computeX(Size, Point2D, Pos)}.
     */
    public double computeX(Size bounds, Point2D origin, Pos anchor) {
        return base.computeX(bounds, origin, anchor);
    }

    /**
     * Delegates to {@link MFXPopupBase#computeY(Size, Point2D, Pos)}.
     */
    public double computeY(Size bounds, Point2D origin, Pos anchor) {
        return base.computeY(bounds, origin, anchor);
    }

    /**
     * Delegates to {@link MFXPopupBase#getContent()}.
     */
    public Node getContent() {
        return base.getContent();
    }

    /**
     * Delegates to {@link MFXPopupBase#contentProperty()}.
     */
    public NodeProperty contentProperty() {
        return base.contentProperty();
    }

    /**
     * Delegates to {@link MFXPopupBase#setContent(Node)}.
     */
    public void setContent(Node content) {
        base.setContent(content);
    }

    /**
     * Delegates to {@link MFXPopupBase#getContentBounds()}.
     */
    public Bounds getContentBounds() {
        return base.getContentBounds();
    }

    /**
     * Delegates to {@link MFXPopupBase#contentBoundsProperty()}.
     */
    public ReadOnlyObjectProperty<Bounds> contentBoundsProperty() {
        return base.contentBoundsProperty();
    }

    /**
     * Delegates to {@link MFXPopupBase#setContentBounds(Bounds)}.
     */
    protected void setContentBounds(Bounds contentBounds) {
        base.setContentBounds(contentBounds);
    }

    /**
     * Delegates to {@link MFXPopupBase#isHover()}.
     */
    @Override
    public boolean isHover() {
        return base.isHover();
    }

    /**
     * Delegates to {@link MFXPopupBase#hoverProperty()}.
     */
    @Override
    public ReadOnlyBooleanProperty hoverProperty() {
        return base.hoverProperty();
    }

    /**
     * Delegates to {@link MFXPopupBase#isAnimated()}.
     */
    public boolean isAnimated() {
        return base.isAnimated();
    }

    /**
     * Delegates to {@link MFXPopupBase#animatedProperty()}.
     */
    public BooleanProperty animatedProperty() {
        return base.animatedProperty();
    }

    /**
     * Delegates to {@link MFXPopupBase#setAnimated(boolean)}.
     */
    public void setAnimated(boolean animated) {
        base.setAnimated(animated);
    }

    /**
     * Delegates to {@link MFXPopupBase#getOffset()}.
     */
    public Position getOffset() {
        return base.getOffset();
    }

    /**
     * Delegates to {@link MFXPopupBase#offsetProperty()}.
     */
    public PositionProperty offsetProperty() {
        return base.offsetProperty();
    }

    /**
     * Delegates to {@link MFXPopupBase#setOffset(Position)}.
     */
    public void setOffset(Position offset) {
        base.setOffset(offset);
    }

    /**
     * Delegates to {@link MFXPopupBase#getStylesheets()}.
     */
    public ObservableList<String> getStylesheets() {
        return base.getStylesheets();
    }

    /**
     * Delegates to {@link MFXPopupBase#getOwnerRef()}.
     */
    protected WeakReference<Node> getOwnerRef() {
        return base.getOwnerRef();
    }

    /**
     * Delegates to {@link MFXPopupBase#getOwner()}.
     */
    protected Node getOwner() {
        return base.getOwner();
    }

    /**
     * Delegates to {@link MFXPopupBase#setOwnerRef(WeakReference)}.
     */
    protected void setOwner(Node owner) {
        base.setOwner(owner);
    }

    /**
     * Delegates to {@link MFXPopupBase#getAnchor()}.
     */
    public Pos getAnchor() {
        return base.getAnchor();
    }

    /**
     * Delegates to {@link MFXPopupBase#setAnchor(Pos)}.
     */
    public void setAnchor(Pos anchor) {
        base.setAnchor(anchor);
    }

    /**
     * This is responsible for retrieving the popup's {@link IMFXPopupSkin} instance.
     * Since users are free to use any skin they want, this returns an {@link Optional} to indicate whether the
     * needed skin was found or not.
     */
    protected Optional<IMFXPopupSkin> retrieveSkin() {
        if (base.getSkin() == null) {
            try {
                base.setSkin((IMFXPopupSkin) getSkin());
            } catch (Exception ex) {
                base.setSkin(null);
            }
        }
        return Optional.ofNullable(base.getSkin());
    }

    /**
     * Delegates to {@link MFXPopupBase#setSkin(IMFXPopupSkin)}.
     */
    protected void setSkin(IMFXPopupSkin skin) {
        base.setSkin(skin);
    }

    //================================================================================
    // Getters
    //================================================================================

    /**
     * @return the {@link MFXPopupBase} instance used by the popup
     */
    protected MFXPopupBase getBase() {
        return base;
    }

    /**
     * Unwraps the {@link WeakReference} used to store the popup's Window owner reference.
     * If the {@link WeakReference} is null, returns null.
     */
    public Window getWindowOwner() {
        return (windowOwner != null) ? windowOwner.get() : null;
    }

    /**
     * Sets the {@link WeakReference} used to store the popup's Window owner reference to a new {@link WeakReference} object
     * built on the given owner reference.
     */
    protected void setWindowOwner(Window window) {
        this.windowOwner = new WeakReference<>(window);
    }
}
