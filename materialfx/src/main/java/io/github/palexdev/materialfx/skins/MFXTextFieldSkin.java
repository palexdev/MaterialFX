package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.MFXResourcesManager;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.MFXDialogValidator;
import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 * This is the implementation of the {@code Skin} associated with every {@code MFXTextField}.
 */
public class MFXTextFieldSkin extends TextFieldSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final double padding = 11;

    private final Line line;
    private final Line focusLine;
    private final Label validate;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXTextFieldSkin(MFXTextField textField) {
        super(textField);

        line = new Line();
        line.getStyleClass().add("unfocused-line");
        line.setStroke(textField.getUnfocusedLineColor());
        line.setStrokeWidth(textField.getLineStrokeWidth());
        line.setSmooth(true);

        focusLine = new Line();
        focusLine.getStyleClass().add("focused-line");
        focusLine.setStroke(textField.getLineColor());
        focusLine.setStrokeWidth(textField.getLineStrokeWidth());
        focusLine.setSmooth(true);
        focusLine.setOpacity(0.0);

        line.endXProperty().bind(textField.widthProperty());
        focusLine.endXProperty().bind(textField.widthProperty());
        line.setManaged(false);
        focusLine.setManaged(false);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(1, 1);
        stackPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        SVGPath warn = MFXResourcesManager.SVGResources.EXCLAMATION_TRIANGLE.getSvgPath();
        warn.setScaleX((padding - 1) / 100);
        warn.setScaleY((padding  - 1) / 100);
        warn.setFill(Color.RED);
        stackPane.getChildren().add(warn);

        validate = new Label("", stackPane);
        validate.getStyleClass().add("validate-label");
        validate.textProperty().bind(textField.getValidator().validatorMessageProperty());
        validate.setGraphicTextGap(padding);
        validate.setVisible(false);

        getChildren().addAll(line, focusLine, validate);

        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: line, focus, disabled and validator properties.
     * <p>
     * Validator: when the control is not focused, and of course if {@code isValidated} is true,
     * all the conditions in the validator are evaluated and if one is false the {@code validate} label is shown.
     * The label text is bound to the {@code validatorMessage} property so if you want to change it you can do it
     * by getting the instance with {@code getValidator()}.
     * <p>
     * There's also another listener to keep track of validator changes.
     */
    private void setListeners() {
        MFXTextField textField = (MFXTextField) getSkinnable();
        MFXDialogValidator validator = textField.getValidator();

        textField.lineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                focusLine.setStroke(newValue);
            }
        });

        textField.unfocusedLineColorProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(oldValue)) {
                line.setStroke(newValue);
            }
        });

        textField.lineStrokeWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() != oldValue.doubleValue()) {
                line.setStrokeWidth(newValue.doubleValue());
                focusLine.setStrokeWidth(newValue.doubleValue() * 1.3);
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && textField.isValidated()) {
                validate.setVisible(!validator.isValid());
            }

            if (textField.isAnimateLines()) {
                buildAndPlayAnimation(newValue);
                return;
            }

            if (newValue) {
                focusLine.setOpacity(1.0);
            } else {
                focusLine.setOpacity(0.0);
            }
        });

        textField.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (textField.isAnimateLines() && focusLine.getOpacity() != 1.0) {
                buildAndPlayAnimation(true);
                return;
            }

            focusLine.setOpacity(1.0);
        });

        textField.isValidatedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validate.setVisible(false);
            }
        });

        textField.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                validate.setVisible(false);
            }
        });

        validator.addChangeListener((observable, oldValue, newValue) -> {
            if (textField.isValidated()) {
                validate.setVisible(!newValue);
            }
        });

        validate.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> validator.show());
    }

    /**
     * Builds and play the lines animation if {@code animateLines} is true.
     */
    private void buildAndPlayAnimation(boolean focused) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), focusLine);
        if (focused) {
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
        } else {
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
        }
        fadeTransition.play();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        final double size = padding / 2.5;

        focusLine.setTranslateY(h + padding * 0.7);
        line.setTranslateY(h + padding * 0.7);
        validate.resize(w * 1.5, h - size);
        validate.setTranslateY(focusLine.getTranslateY() + size);
    }
}
