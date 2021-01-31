package io.github.palexdev.materialfx.controls.cell;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.MFXCheckTreeItem;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

// TODO change to MFXCheckbox, after refactor
public class MFXCheckTreeCell<T> extends MFXSimpleTreeCell<T> {
    private final String STYLE_CLASS = "mfx-check-tree-cell";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-checktreecell.css").toString();
    private final CheckBox checkbox;

    private static final PseudoClass CHECKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("checked");
    private static final PseudoClass INDETERMINATE_PSEUDO_CLASS = PseudoClass.getPseudoClass("indeterminate");
    private final BooleanProperty checked = new SimpleBooleanProperty(false);
    private final BooleanProperty indeterminate = new SimpleBooleanProperty(false);

    public MFXCheckTreeCell(MFXCheckTreeItem<T> item) {
        super(item);
        checkbox = new MFXCheckbox("");
        getChildren().add(1, checkbox);
        initialize(item);
    }

    public MFXCheckTreeCell(MFXCheckTreeItem<T> item, double fixedHeight) {
        super(item, fixedHeight);
        checkbox = new MFXCheckbox("");
        getChildren().add(1, checkbox);
        initialize(item);
    }

    private void initialize(MFXCheckTreeItem<T> item) {
        getStyleClass().add(STYLE_CLASS);
        setFixedCellSize(32);

        addListeners();
        checked.bind(item.checkedProperty());
        indeterminate.bind(item.indeterminateProperty());
    }

    private void addListeners() {
        checked.addListener(invalidate -> pseudoClassStateChanged(CHECKED_PSEUDO_CLASS, checked.get()));
        checked.addListener((observable, oldValue, newValue) -> checkbox.setSelected(newValue));
        indeterminate.addListener(invalidate -> pseudoClassStateChanged(INDETERMINATE_PSEUDO_CLASS, indeterminate.get()));
        indeterminate.addListener((observable, oldValue, newValue) -> checkbox.setIndeterminate(newValue));

        checkbox.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RippleGenerator rippleGenerator = (RippleGenerator) checkbox.lookup(".ripple-generator");
                rippleGenerator.setRippleColor(Color.FIREBRICK);
            }
        });
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }
}
