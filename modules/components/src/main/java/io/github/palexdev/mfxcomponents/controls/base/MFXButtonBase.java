package io.github.palexdev.mfxcomponents.controls.base;

import io.github.palexdev.mfxcomponents.behaviors.MFXButtonBehaviorBase;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import io.github.palexdev.mfxcore.base.properties.EventHandlerProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.util.List;

/**
 * Base class for MaterialFX components that are buttons. Extends {@link MFXLabeled} as most buttons are also accompanied
 * by text, but in the end it also depends on the component' skin.
 * <p></p>
 * Implements the most basic properties and behaviors of each button, such as the {@link #onActionProperty()} and the
 * {@link #fire()} method.
 * <p>
 * Such components expect behaviors of type {@link MFXButtonBehaviorBase}.
 * <p></p>
 * Since it's the base class to implement any kind of button, the selector is CSS is set by default to: '.mfx-button.base'.
 */
public abstract class MFXButtonBase<B extends MFXButtonBehaviorBase<?>> extends MFXLabeled<B> {
    //================================================================================
    // Properties
    //================================================================================
    private final EventHandlerProperty<ActionEvent> onAction = new EventHandlerProperty<>() {
        @Override
        protected void invalidated() {
            setEventHandler(ActionEvent.ACTION, get());
        }
    };

    //================================================================================
    // Constructors
    //================================================================================
    public MFXButtonBase() {
        initialize();
    }

    public MFXButtonBase(String text) {
        super(text);
        initialize();
    }

    public MFXButtonBase(String text, Node icon) {
        super(text, icon);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().setAll(defaultStyleClasses());
        setDefaultBehaviorProvider();
        sceneBuilderIntegration();
    }

    /**
     * If not disabled, fires a new {@link ActionEvent}, triggering the {@link EventHandler} specified
     * by the {@link #onActionProperty()}.
     */
    public void fire() {
        if (!isDisabled()) fireEvent(new ActionEvent());
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    public List<String> defaultStyleClasses() {
        return List.of("mfx-button", "base");
    }

    @Override
    protected void sceneBuilderIntegration() {
        SceneBuilderIntegration.ifInSceneBuilder(() -> setText("Button"));
        SceneBuilderIntegration.ifInSceneBuilder(() -> {
            String theme = MFXThemeManager.PURPLE_LIGHT.load();
            When.onChanged(sceneProperty())
                .condition((o, n) -> n != null && !n.getStylesheets().contains(theme))
                .then((o, n) -> n.getStylesheets().add(theme))
                .oneShot()
                .listen();
        });
        // TODO theme integration with SceneBuilder will change once base themes and MFXThemeManager are implemented
    }

    //================================================================================
    // Getters/Setters
    //================================================================================
    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    /**
     * Specifies the action to execute when an {@link ActionEvent} is fired on this button.
     */
    public EventHandlerProperty<ActionEvent> onActionProperty() {
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        this.onAction.set(onAction);
    }
}
