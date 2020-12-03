package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.skins.MFXScrollPaneSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.function.Function;

/**
 * This is the implementation of a scroll pane following Google's material design guidelines in JavaFX.
 * <p>
 * Extends {@code ScrollPane} and redefines the style class to "mfx-scroll-pane" for usage in CSS.
 */
public class MFXScrollPane extends ScrollPane {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-scroll-pane";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-scrollpane.css").toString();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXScrollPane() {
        initialize();
    }

    public MFXScrollPane(Node content) {
        super(content);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        addListeners();
    }

    //================================================================================
    // Style Properties
    //================================================================================

    /**
     * Specifies the color of the scrollbars' track.
     */
    private final ObjectProperty<Paint> trackColor = new SimpleObjectProperty<>(Color.rgb(132, 132, 132));

    /**
     * Specifies the color of the scrollbars' thumb.
     */
    private final ObjectProperty<Paint> thumbColor = new SimpleObjectProperty<>(Color.rgb(137, 137, 137));

    /**
     * Specifies the color of the scrollbars' thumb when mouse hover.
     */
    private final ObjectProperty<Paint> thumbHoverColor = new SimpleObjectProperty<>(Color.rgb(89, 88, 91));

    public Paint getTrackColor() {
        return trackColor.get();
    }

    public ObjectProperty<Paint> trackColorProperty() {
        return trackColor;
    }

    public void setTrackColor(Paint trackColor) {
        this.trackColor.set(trackColor);
    }

    public Paint getThumbColor() {
        return thumbColor.get();
    }

    public ObjectProperty<Paint> thumbColorProperty() {
        return thumbColor;
    }

    public void setThumbColor(Paint thumbColor) {
        this.thumbColor.set(thumbColor);
    }

    public Paint getThumbHoverColor() {
        return thumbHoverColor.get();
    }

    public ObjectProperty<Paint> thumbHoverColorProperty() {
        return thumbHoverColor;
    }

    public void setThumbHoverColor(Paint thumbHoverColor) {
        this.thumbHoverColor.set(thumbHoverColor);
    }

    /**
     * Adds listeners for colors change and calls setColors().
     */
    private void addListeners() {
        this.trackColor.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                setColors();
            }
        });

        this.thumbColor.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                setColors();
            }
        });

        this.thumbHoverColor.addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                setColors();
            }
        });
    }

    /**
     *  Sets the CSS looked-up colors
     */
    private void setColors() {
        StringBuilder sb = new StringBuilder();
        sb.append("-mfx-track-color: ").append(ColorUtils.rgb((Color) trackColor.get()))
                .append(";\n-mfx-thumb-color: ").append(ColorUtils.rgb((Color) thumbColor.get()))
                .append(";\n-mfx-thumb-hover-color: ").append(ColorUtils.rgb((Color) thumbHoverColor.get()))
                .append(";");
        setStyle(sb.toString());
    }

    //================================================================================
    // Static Methods
    //================================================================================
    private static void customScrolling(ScrollPane scrollPane, DoubleProperty scrollDirection, Function<Bounds, Double> sizeFunc) {
        final double[] frictions = {0.99, 0.1, 0.05, 0.04, 0.03, 0.02, 0.01, 0.04, 0.01, 0.008, 0.008, 0.008, 0.008, 0.0006, 0.0005, 0.00003, 0.00001};
        final double[] pushes = {1};
        final double[] derivatives = new double[frictions.length];

        Timeline timeline = new Timeline();
        final EventHandler<MouseEvent> dragHandler = event -> timeline.stop();
        final EventHandler<ScrollEvent> scrollHandler = event -> {
            if (event.getEventType() == ScrollEvent.SCROLL) {
                int direction = event.getDeltaY() > 0 ? -1 : 1;
                for (int i = 0; i < pushes.length; i++) {
                    derivatives[i] += direction * pushes[i];
                }
                if (timeline.getStatus() == Animation.Status.STOPPED) {
                    timeline.play();
                }
                event.consume();
            }
        };
        if (scrollPane.getContent().getParent() != null) {
            scrollPane.getContent().getParent().addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
            scrollPane.getContent().getParent().addEventHandler(ScrollEvent.ANY, scrollHandler);
        }
        scrollPane.getContent().parentProperty().addListener((o,oldVal, newVal)->{
            if (oldVal != null) {
                oldVal.removeEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                oldVal.removeEventHandler(ScrollEvent.ANY, scrollHandler);
            }
            if (newVal != null) {
                newVal.addEventHandler(MouseEvent.DRAG_DETECTED, dragHandler);
                newVal.addEventHandler(ScrollEvent.ANY, scrollHandler);
            }
        });
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3), (event) -> {
            for (int i = 0; i < derivatives.length; i++) {
                derivatives[i] *= frictions[i];
            }
            for (int i = 1; i < derivatives.length; i++) {
                derivatives[i] += derivatives[i - 1];
            }
            double dy = derivatives[derivatives.length - 1];
            double size = sizeFunc.apply(scrollPane.getContent().getLayoutBounds());
            scrollDirection.set(Math.min(Math.max(scrollDirection.get() + dy / size, 0), 1));
            if (Math.abs(dy) < 0.001) {
                timeline.stop();
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Adds smooth vertical scrolling to the specified scroll pane.
     * <p>
     * <b>Note: not recommended for small scroll panes</b>
     */
    public static void smoothVScrolling(ScrollPane scrollPane) {
        customScrolling(scrollPane, scrollPane.vvalueProperty(), Bounds::getHeight);
    }

    /**
     * Adds smooth horizontal scrolling to the specified scroll pane.
     * <p>
     * <b>Note: not recommended for small scroll panes</b>
     */
    public static void smoothHScrolling(ScrollPane scrollPane) {
        customScrolling(scrollPane, scrollPane.hvalueProperty(), Bounds::getWidth);
    }

    //================================================================================
    // Override Methods
    //================================================================================

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MFXScrollPaneSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

}
