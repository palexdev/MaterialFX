package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * This little control provides a graphical way of evaluating a condition on
 * a given string with a specific predicate. The computed boolean can also be chained with
 * other conditions as an AND or an OR, specified by {@link EvaluationMode}.
 * <p></p>
 * The control is made of:
 * <p> - An icon that should be used by the control's parent to remove it from the children list
 * <p> - A text field that provides one of the strings to test
 * <p> - A combo box that contains the predicate to apply
 * <p></p>
 * An usage example would be:
 * <p></p>
 * Let's say I have a string {@code s1 = AbcdE} and a string {@code s2 = "cde"} provided by the text field.
 * <p>
 * If the selected predicate is "Contains" then {@link #test(String)} will check if s1 contains s2 and will return false.
 * <p>
 * If the selected predicate is "Contains Ignore Case" then {@link #test(String)} will check if s1 contains s2 ignoring case and will return true.
 *
 * <p></p>
 * <b>N.B: </b> Since "Contains Any" and "Contains All" are advanced functions the field text is cleared when one of those functions is selected
 * in the combo box and a prompt text that shows a small example on how to format the string is set.
 *
 * @see BiPredicate
 */
public class MFXEvaluationBox extends HBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx/evaluation-box";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXEvaluationBox.css");

    private final EvaluationMode mode;
    private final Map<String, BiPredicate<String, String>> biPredicates = new LinkedHashMap<>();
    private final MFXFontIcon removeIcon;
    private final MFXTextField inputField;
    private final MFXComboBox<String> predicatesCombo;

    //================================================================================
    // Constructor
    //================================================================================
    public MFXEvaluationBox(EvaluationMode mode) {
        setPrefWidth(600);
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(20);
        setPadding(new Insets(15, 10, 15, 10));

        this.mode = mode;

        Label modeLabel = new Label(mode.name());
        modeLabel.setId("modeLabel");
        modeLabel.setPrefSize(40, 40);
        modeLabel.setMaxHeight(Double.MAX_VALUE);
        modeLabel.setPadding(new Insets(3));
        modeLabel.setAlignment(Pos.CENTER);

        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);

        Label predicateLabel = new Label("Evaluation Predicate:");
        predicatesCombo = new MFXComboBox<>();
        predicatesCombo.setComboStyle(Styles.ComboBoxStyles.STYLE2);
        predicatesCombo.setPrefSize(180, 27);
        predicatesCombo.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);

        inputField = new MFXTextField();
        inputField.setPromptText("Input String...");
        inputField.setAnimateLines(false);
        inputField.setLineColor(Color.web("#4d4d4d"));
        inputField.setLineStrokeWidth(1);
        inputField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(inputField, Priority.ALWAYS);
        HBox.setMargin(inputField, new Insets(0, 10, 2, 0));
        inputField.getStylesheets().add(STYLESHEET);

        removeIcon = new MFXFontIcon("mfx-x-circle", 16, Color.web("#4D4D4D"));
        removeIcon.colorProperty().bind(Bindings.createObjectBinding(
                () -> removeIcon.isHover() ? Color.web("#EF6E6B") : Color.web("#4D4D4D"),
                removeIcon.hoverProperty()
        ));

        box.getChildren().addAll(predicateLabel, predicatesCombo);
        getChildren().addAll(removeIcon, modeLabel, box, inputField);

        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        biPredicates.put("Contains", String::contains);
        biPredicates.put("Contains Ignore Case", StringUtils::containsIgnoreCase);
        biPredicates.put("Contains Any", StringUtils::containsAny);
        biPredicates.put("Contains All", StringUtils::containsAll);
        biPredicates.put("Starts With", String::startsWith);
        biPredicates.put("Start With Ignore Case", StringUtils::startsWithIgnoreCase);
        biPredicates.put("Ends With", String::endsWith);
        biPredicates.put("Ends With Ignore Case", StringUtils::endsWithIgnoreCase);
        biPredicates.put("Equals", String::equals);
        biPredicates.put("Equals Ignore Case", String::equalsIgnoreCase);

        predicatesCombo.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Contains Any") || newValue.equals("Contains All")) {
                inputField.setPromptText("Eg. \"A, B, C, DEF GHI, E, F...\"");
                inputField.clear();
            } else {
                inputField.setPromptText("");
            }
        });

        predicatesCombo.setItems(FXCollections.observableArrayList(biPredicates.keySet()));
        predicatesCombo.getSelectionModel().selectFirst();
    }

    /**
     * Applies the selected predicate (provided by the combo box) to the given
     * string and the text provided by the text field.
     */
    public Boolean test(String testString) {
        if (getPredicate() != null) {
            return biPredicates.get(getPredicate()).test(testString, inputField.getText());
        }
        return false;
    }

    /**
     * @return the evaluation mode of this control
     */
    public EvaluationMode getMode() {
        return mode;
    }

    /**
     * @return the currently selected predicate
     */
    public String getPredicate() {
        return predicatesCombo.getSelectedValue();
    }

    /**
     * @return the remove icon instance
     */
    public MFXFontIcon getRemoveIcon() {
        return removeIcon;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
