package io.github.palexdev.mfxcomponents.window.popups;

import io.github.palexdev.mfxcore.base.beans.Position;
import io.github.palexdev.mfxcore.base.properties.NodeProperty;
import io.github.palexdev.mfxcore.base.properties.PositionProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxeffects.animations.Animations;
import io.github.palexdev.mfxeffects.animations.Animations.PauseBuilder;
import io.github.palexdev.mfxeffects.animations.motion.M3Motion;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import javafx.util.Duration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

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
    private EventHandler<MouseEvent> mouseEnter;
    private EventHandler<MouseEvent> mouseExit;
    private When<?> hoverWhen;

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

        skinProperty().addListener(i -> setSkin(null));
        showing.addListener(i -> handleShowingChanged());
    }

    public MFXTooltip install() {
        if (installed) return this;
        Node owner = getOwner();
        delayer = PauseBuilder.build()
            .setOnFinished(e -> open())
            .getAnimation();
        delayer.durationProperty().bind(inDelayProperty());
        countdown = PauseBuilder.build()
            .setOnFinished(e -> hide())
            .getAnimation();
        countdown.durationProperty().bind(outDelayProperty());

        mouseEnter = e -> {
            countdown.stop();
            if (isShowing()) return;
            if (Animations.isPlaying(delayer)) return;
            delayer.playFromStart();
        };
        mouseExit = e -> {
            if (Animations.isPlaying(countdown)) return;
            countdown.playFromStart();
        };

        hoverWhen = When.onChanged(hoverProperty())
            .then((o, n) -> {
                if (n) {
                    countdown.stop();
                } else if (!owner.isHover()) {
                    countdown.playFromStart();
                }
            })
            .listen();

        owner.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEnter);
        owner.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExit);
        installed = true;
        return this;
    }

    public void dispose() {
        if (!installed) return;
        Node owner = getOwner();
        delayer = null;
        countdown = null;
        if (owner != null) {
            owner.removeEventFilter(MouseEvent.MOUSE_ENTERED, mouseEnter);
            owner.removeEventFilter(MouseEvent.MOUSE_EXITED, mouseExit);
        }
        mouseEnter = null;
        mouseExit = null;
        hoverWhen.dispose();
        hoverWhen = null;
        getOwnerRef().clear();
        setOwner(null);
    }

    public void open() {
        Pos anchor = getAnchor();
        if (anchor == null) anchor = Pos.BOTTOM_CENTER;
        show(anchor);
    }

    public final void close() {
        super.hide();
    }

    public void show(Pos anchor) {
        Node owner = getOwner();
        if (owner.getScene() == null || owner.getScene().getWindow() == null)
            throw new IllegalStateException("Cannot show the popup. The node must be attached to a scene/window!");
        setAnchor(anchor);
        Position pos = computePosition(anchor);
        super.show(owner, pos.getX(), pos.getY());
    }

    public void reposition() {
        Position position = base.reposition();
        setAnchorX(position.getX());
        setAnchorY(position.getY());
    }

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

    @Override
    protected void show() {
        if (getContent() == null)
            throw new IllegalStateException("Popup content is null!");
        if (!isAutoHide() && isShowing()) {
            close();
            return;
        }

        setShowing(this);
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
        return List.of("mfx-tooltip");
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

    public Node getOwner() {
        return base.getOwner();
    }

    protected void setOwner(Node owner) {
        base.setOwner(owner);
    }

    public Pos getAnchor() {
        return base.getAnchor();
    }

    public void setAnchor(Pos anchor) {
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
    // Getters/Setters
    //================================================================================
    protected MFXPopupBase getBase() {
        return base;
    }

    public boolean isKeepOpenOnShowRequest() {
        return keepOpenOnShowRequest.get();
    }

    public BooleanProperty keepOpenOnShowRequestProperty() {
        return keepOpenOnShowRequest;
    }

    public void setKeepOpenOnShowRequest(boolean keepOpenOnShowRequest) {
        this.keepOpenOnShowRequest.set(keepOpenOnShowRequest);
    }

    public Duration getInDelay() {
        return inDelay.get();
    }

    public ObjectProperty<Duration> inDelayProperty() {
        return inDelay;
    }

    public void setInDelay(Duration inDelay) {
        this.inDelay.set(inDelay);
    }

    public Duration getOutDelay() {
        return outDelay.get();
    }

    public ObjectProperty<Duration> outDelayProperty() {
        return outDelay;
    }

    public void setOutDelay(Duration outDelay) {
        this.outDelay.set(outDelay);
    }

    MFXTooltip getShowing() {
        return showing.get().get();
    }

    void setShowing(MFXTooltip showing) {
        MFXTooltip.showing.set(new WeakReference<>(showing));
    }
}
