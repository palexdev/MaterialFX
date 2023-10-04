package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.skins.MFXPopupSkin;
import io.github.palexdev.mfxcomponents.skins.base.IMFXPopupSkin;
import io.github.palexdev.mfxcomponents.window.MFXPlainContent;
import io.github.palexdev.mfxcomponents.window.MFXRichContent;
import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.beans.Size;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.PauseBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.stage.Window;
import javafx.util.Duration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Specialized type of popup usually used to show short and concise hints on UI elements.
 * Extends {@link PopupControl} and implements {@link IMFXPopup}, makes use of:
 * <p> - {@link MFXPopupBase} for common properties and behaviors
 * <p> - {@link MFXPopupSkin} as its default skin
 * <p> - Since {@link IMFXPopup} also extends {@link MFXStyleable}, the default style class is set to be '.mfx-tooltip'
 * <p></p>
 * Aside from the common properties and behaviors, and the architecture inherited by {@link PopupControl}, {@code MFXTooltip}
 * wants to make the user experience better by being more stable and reliable tha JavaFX tooltips, while also being more
 * customizable and flexible.
 * <p></p>
 * There are many changes in {@code MFXTooltip}, here are the most notable ones:
 * <p> 1) Following M3 guidelines, it's recommended to show at most one tooltip at once, this is the default behavior of
 * {@code MFXTooltip}, can be overridden by setting the {@link #keepOpenOnShowRequestProperty()}
 * <p> 2) Allows fine turning of the show and close delays!
 * <p> 3) Offers a series of 'install' methods that make it easy to set them on any Node
 * <p> 4) Following the {@link MFXPopup} implementation, this also offers methods such as {@link #open()},
 * {@link #show(Pos)}, {@link #reposition()}
 * <p> 5) It's important to also mention that strong design decisions were taken for {@link MFXTooltip}.
 * Tooltips are meant to be shown next to an owner Node, for any other situation a generic popup should be used instead.
 * For this reason, most of the original show methods have been overridden to throw an exception!
 */
public class MFXTooltip extends PopupControl implements IMFXPopup {
    //================================================================================
    // Static Properties
    //================================================================================
    private static final ObjectProperty<WeakReference<MFXTooltip>> showing = new SimpleObjectProperty<>(new WeakReference<>(null));

    //================================================================================
    // Properties
    //================================================================================
    private final MFXPopupBase base = new MFXPopupBase() {
        @Override
        protected void onContentBoundsChanged() {
            Bounds bounds = getContentBounds();
            if (bounds.getWidth() > 0 && bounds.getHeight() > 0)
                MFXTooltip.this.reposition();
        }
    };
    private final BooleanProperty keepOpenOnShowRequest = new SimpleBooleanProperty(false);

    private final ObjectProperty<Duration> inDelay = new SimpleObjectProperty<>(M3Motion.LONG2);
    private final ObjectProperty<Duration> outDelay = new SimpleObjectProperty<>(M3Motion.EXTRA_LONG4);
    private PauseTransition delayer;
    private PauseTransition countdown;

    private boolean installed = false;
    private boolean indirect = false;
    private When<?> hoverWhen;
    private When<?> ownerHoverWhen;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTooltip(Node owner) {
        assert owner != null;
        setOwner(owner);
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
        showing.addListener(i -> handleShowingChanged());
    }

    /**
     * Builds and adds the needed listeners and handlers on the current set owner Node, as well as building the
     * {@link PauseTransition}s responsible for showing/closing the tooltip after the specified amounts of time.
     *
     * @throws NullPointerException if the set owner Node is null
     * @see #inDelayProperty()
     * @see #outDelayProperty()
     */
    public MFXTooltip install() {
        if (installed) return this;
        Node owner = getOwner();
        if (owner == null)
            throw new NullPointerException("Tooltip's owner cannot be null!");

        if (!indirect) {
            if (owner instanceof MFXControl) return install(((MFXControl<?>) owner));
            if (owner instanceof MFXLabeled) return install(((MFXLabeled<?>) owner));
        }
        indirect = false;

        delayer = PauseBuilder.build()
            .setOnFinished(e -> open())
            .getAnimation();
        delayer.durationProperty().bind(inDelayProperty());
        countdown = PauseBuilder.build()
            .setOnFinished(e -> hide())
            .getAnimation();
        countdown.durationProperty().bind(outDelayProperty());

        hoverWhen = When.onChanged(hoverProperty())
            .then((o, n) -> {
                if (n) {
                    countdown.stop();
                } else if (!owner.isHover()) {
                    countdown.playFromStart();
                }
            })
            .listen();
        ownerHoverWhen = When.onChanged(owner.hoverProperty())
            .then((o, n) -> {
                if (n) {
                    countdown.stop();
                    if (Animations.isPlaying(delayer)) return;
                    delayer.playFromStart();
                } else {
                    delayer.stop();
                    if (Animations.isPlaying(countdown)) return;
                    countdown.playFromStart();
                }
            })
            .listen();
        installed = true;
        return this;
    }

    /**
     * Delegates to {@link #install()} but first calls {@link #dispose()} and {@link #setOwner(Node)}, thus
     * allowing to also change the owner at anytime.
     */
    public MFXTooltip install(Node owner) {
        if (installed) dispose();
        setOwner(owner);
        return install();
    }

    protected MFXTooltip install(MFXControl<?> control) {
        if (installed) dispose();
        indirect = true;
        control.setMFXTooltip(this);
        return this;
    }

    protected MFXTooltip install(MFXLabeled<?> labeled) {
        if (installed) dispose();
        indirect = true;
        labeled.setMFXTooltip(this);
        return this;
    }

    /**
     * This method removes and clears all the listeners/handlers/animations built by the installation methods.
     * It's very important to dispose a tooltip when not needed anymore or before changing owner!
     */
    public void dispose() {
        if (!installed) return;
        delayer = null;
        countdown = null;
        if (hoverWhen != null) {
            hoverWhen.dispose();
            hoverWhen = null;
        }
        if (ownerHoverWhen != null) {
            ownerHoverWhen.dispose();
            ownerHoverWhen = null;
        }
        base.setOwnerRef(null);
        installed = false;
    }

    /**
     * Delegates to {@link #show(Pos)} with {@link Pos#BOTTOM_CENTER} as the anchor.
     * <p></p>
     * Side note: it's named 'open' to not conflict with {@link #show()}.
     */
    public void open() {
        Pos anchor = getAnchor();
        if (anchor == null) anchor = Pos.BOTTOM_CENTER;
        show(anchor);
    }

    /**
     * Simply calls {@code super.hide()}, since the method has been overridden here for the close animation to play
     * properly.
     */
    public final void close() {
        super.hide();
    }

    /**
     * Uses {@link #computePosition(Pos)} to compute the position at which the tooltip will be shown, using the given
     * parameters, then delegates to {@link #show(Node, double, double)}.
     *
     * @throws IllegalStateException if the given owner is not in a Scene or if the owner' Scene is not in a Window
     */
    public void show(Pos anchor) {
        Node owner = getOwner();
        if (owner.getScene() == null || owner.getScene().getWindow() == null)
            throw new IllegalStateException("Cannot show the popup. The node must be attached to a scene/window!");
        setAnchor(anchor);
        Position pos = computePosition(anchor);
        super.show(owner, pos.getX(), pos.getY());
    }

    /**
     * Re-computes the tooltip's position using {@link MFXPopupBase#reposition()}, if the result is not a null Position,
     * then proceeds to set both {@link #anchorXProperty()} and {@link #anchorYProperty()} with the new values.
     */
    public void reposition() {
        Position position = base.reposition();
        if (position == null) return;
        setAnchorX(position.getX());
        setAnchorY(position.getY());
    }

    /**
     * As already said in the class documentation. {@link MFXTooltip} follows M3 guidelines which state that it's desirable
     * to show at most one tooltip at a time to not clutter the UI.
     * <p>
     * To implement this, the tooltip has a static property that holds a {@link WeakReference} to the currently showing
     * tooltip, since it's static this is global, meaning that it keeps track of all tooltips in the app.
     * <p>
     * When a tooltip is being shown, this static property is updated and this method is automatically triggered.
     * By default, if {@link #keepOpenOnShowRequestProperty()} is false, causes all other tooltips to close.
     */
    protected void handleShowingChanged() {
        MFXTooltip showing = getShowing();
        boolean keepOpen = isKeepOpenOnShowRequest();
        if (showing == null || showing == MFXTooltip.this || keepOpen) return;
        hide();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    // These methods are forbidden, a tooltip automatically handles itself
    @Override
    public void show(Window owner) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void show(Node ownerNode, double anchorX, double anchorY) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void show(Window ownerWindow, double anchorX, double anchorY) {
        throw new UnsupportedOperationException();
    }

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

        setShowing(this);
        retrieveSkin().ifPresent(IMFXPopupSkin::animateIn);
        super.show();
    }

    /**
     * {@inheritDoc}
     * <p></p>
     * Overridden to play the close animation defined in the skin.
     * <p>
     * There's an important difference here from {@link #show()}. When the hide method is called the tooltip is
     * closed immediately, meaning that the animation won't be shown. For this reason, the defined close animation
     * is responsible for calling {@link #close()} once the animation has ended.
     */
    @Override
    public void hide() {
        /*
         * This check is due to a JavaFX bug in WindowStage.setBounds(...) method which doesn't check for the window
         * to be not null before doing anything on it.
         * This seems to happen when the Popup is open, but the main window is being closed.
         * Animations at such stage cannot be used; also, it's not necessary to explicitly hide it since in theory the
         * framework will handle it automatically
         */
        boolean showing = Optional.ofNullable(getOwner())
            .flatMap(n -> Optional.ofNullable(n.getScene()))
            .map(Scene::getWindow)
            .map(Window::isShowing)
            .orElse(true);
        if (!showing) return;

        retrieveSkin().ifPresentOrElse(
            IMFXPopupSkin::animateOut,
            this::close
        );
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXPopupSkin<>(this);
    }

    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-tooltip");
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
    public Node getOwner() {
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
     * This is responsible for retrieving the tooltip's {@link IMFXPopupSkin} instance.
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
    // Getters/Setters
    //================================================================================

    /**
     * @return the {@link MFXPopupBase} instance used by the popup
     */
    protected MFXPopupBase getBase() {
        return base;
    }

    public boolean isKeepOpenOnShowRequest() {
        return keepOpenOnShowRequest.get();
    }

    /**
     * Specifies whether this tooltip should be an exception to rule 'show at most one tooltip at a time'.
     *
     * @see #handleShowingChanged()
     */
    public BooleanProperty keepOpenOnShowRequestProperty() {
        return keepOpenOnShowRequest;
    }

    public void setKeepOpenOnShowRequest(boolean keepOpenOnShowRequest) {
        this.keepOpenOnShowRequest.set(keepOpenOnShowRequest);
    }

    public Duration getInDelay() {
        return inDelay.get();
    }

    /**
     * Specifies the amount of time after which the tooltip is shown.
     * <p>
     * By default {@link M3Motion#LONG2}.
     */
    public ObjectProperty<Duration> inDelayProperty() {
        return inDelay;
    }

    public void setInDelay(Duration inDelay) {
        this.inDelay.set(inDelay);
    }

    public Duration getOutDelay() {
        return outDelay.get();
    }

    /**
     * Specifies the amount of time after which the tooltip is hidden.
     * <p>
     * By default {@link M3Motion#EXTRA_LONG4}.
     */
    public ObjectProperty<Duration> outDelayProperty() {
        return outDelay;
    }

    public void setOutDelay(Duration outDelay) {
        this.outDelay.set(outDelay);
    }

    /**
     * @return whether the popup has been installed before(true/false) or disposed(false)
     */
    public boolean isInstalled() {
        return installed;
    }

    /**
     * @return the instance of the currently shown tooltip
     */
    protected MFXTooltip getShowing() {
        return showing.get().get();
    }

    /**
     * Sets the currently shown tooltip instance.
     */
    protected void setShowing(MFXTooltip showing) {
        MFXTooltip.showing.set(new WeakReference<>(showing));
    }

    //================================================================================
    // Content Specific Methods
    //================================================================================

    /**
     * Convenience method for contents of type {@link MFXPlainContent} or {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXPlainContent#getText()} and {@link MFXRichContent#getText()} respectively.
     * <p></p>
     * Returns an empty string if the content is not of such types.
     */
    public String getText() {
        Node content = getContent();
        if (content instanceof MFXPlainContent) {
            return ((MFXPlainContent) content).getText();
        }
        if (content instanceof MFXRichContent) {
            return ((MFXRichContent) content).getText();
        }
        return "";
    }

    /**
     * Convenience method for contents of type {@link MFXPlainContent} or {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXPlainContent#textProperty()} and {@link MFXRichContent#textProperty()} respectively.
     * <p></p>
     * To avoid {@link NullPointerException}s if the content is not of such types, the result is wrapped in a {@link Optional}.
     */
    public Optional<StringProperty> textProperty() {
        Node content = getContent();
        if (content instanceof MFXPlainContent) {
            return Optional.of(((MFXPlainContent) content).textProperty());
        }
        if (content instanceof MFXRichContent) {
            return Optional.of(((MFXRichContent) content).textProperty());
        }
        return Optional.empty();
    }

    /**
     * Convenience method for contents of type {@link MFXPlainContent} or {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXPlainContent#setText(String)}and {@link MFXRichContent#setText(String)} respectively.
     */
    public void setText(String text) {
        Node content = getContent();
        if (content instanceof MFXPlainContent) {
            ((MFXPlainContent) content).setText(text);
        }
        if (content instanceof MFXRichContent) {
            ((MFXRichContent) content).setText(text);
        }
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#getHeader()}.
     * <p></p>
     * Returns an empty string if the content is not of such type.
     */
    public String getHeader() {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            return ((MFXRichContent) content).getHeader();
        }
        return "";
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#headerProperty()}.
     * <p></p>
     * To avoid {@link NullPointerException}s if the content is not of such type, the result is wrapped in a {@link Optional}.
     */
    public Optional<StringProperty> headerProperty() {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            return Optional.of(((MFXRichContent) content).headerProperty());
        }
        return Optional.empty();
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#setHeader(String)}.
     */
    public void setHeader(String header) {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            ((MFXRichContent) content).setHeader(header);
        }
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#getPrimaryAction()}.
     * <p></p>
     * To avoid {@link NullPointerException}s if the content is not of such type, the result is wrapped in a {@link Optional}.
     */
    public Optional<MFXButton> getPrimaryAction() {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            return Optional.ofNullable(((MFXRichContent) content).getPrimaryAction());
        }
        return Optional.empty();
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#setPrimaryAction(MFXButton)}.
     */
    public void setPrimaryAction(MFXButton btn) {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            ((MFXRichContent) content).setPrimaryAction(btn);
        }
    }

    /**
     * Delegates to {@link #setPrimaryAction(MFXButton)} but with the convenience of accepting a {@link Supplier}.
     */
    public void setPrimaryAction(Supplier<MFXButton> supplier) {
        setPrimaryAction(supplier.get());
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#getSecondaryAction()}.
     * <p></p>
     * To avoid {@link NullPointerException}s if the content is not of such type, the result is wrapped in a {@link Optional}.
     */
    public Optional<MFXButton> getSecondaryAction() {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            return Optional.ofNullable(((MFXRichContent) content).getSecondaryAction());
        }
        return Optional.empty();
    }

    /**
     * Convenience method for contents of type {@link MFXRichContent}.
     * <p>
     * Delegates to {@link MFXRichContent#setSecondaryAction(MFXButton)}.
     */
    public void setSecondaryAction(MFXButton btn) {
        Node content = getContent();
        if (content instanceof MFXRichContent) {
            ((MFXRichContent) content).setSecondaryAction(btn);
        }
    }

    /**
     * Delegates to {@link #setSecondaryAction(MFXButton)} but with the convenience of accepting a {@link Supplier}.
     */
    public void setSecondaryAction(Supplier<MFXButton> supplier) {
        setSecondaryAction(supplier.get());
    }
}
