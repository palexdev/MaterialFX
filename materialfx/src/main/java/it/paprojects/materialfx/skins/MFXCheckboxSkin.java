package it.paprojects.materialfx.skins;

import it.paprojects.materialfx.controls.MFXCheckbox;
import it.paprojects.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.skin.CheckBoxSkin;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXCheckbox}.
 */
public class MFXCheckboxSkin extends CheckBoxSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final AnchorPane rippleContainer;
    private final StackPane box;
    private final StackPane mark;

    private final double rippleContainerWidth = 30;
    private final double rippleContainerHeight = 30;
    private final double boxWidth = 26;
    private final double boxHeight = 26;

    private final double labelOffset = 2;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckboxSkin(MFXCheckbox control) {
        super(control);

        // Contains the ripple generator and the box
        rippleContainer = new AnchorPane();
        rippleContainer.setPrefSize(rippleContainerWidth, rippleContainerHeight);
        rippleContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rippleContainer.getStyleClass().setAll("ripple-container");

        // To make ripple container appear like a Circle
        Circle circle = new Circle();
        circle.setCenterX(rippleContainerWidth / 2);
        circle.setCenterY(rippleContainerHeight / 2);
        circle.setRadius(rippleContainerWidth * 0.6);
        rippleContainer.setClip(circle);

        // Contains the mark which is a SVG path defined in css
        box = new StackPane();
        box.setPrefSize(boxWidth, boxHeight);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.getStyleClass().setAll("box");
        box.setBorder(new Border(new BorderStroke(
                control.getUncheckedColor(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(2),
                new BorderWidths(2.2)
        )));
        box.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT,
                new CornerRadii(2),
                Insets.EMPTY
                )));

        mark = new StackPane();
        mark.getStyleClass().setAll("mark");
        box.getChildren().add(mark);

        rippleContainer.getChildren().add(box);

        updateChildren();
        updateMarkType(control);
        setListeners(control);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: markType, selected, indeterminate, checked and unchecked coloros properties.
     * @param control The MFXCheckbox associated to this skin
     */
    private void setListeners(MFXCheckbox control) {
        control.markTypeProperty().addListener(
                (observable, oldValue, newValue) -> updateMarkType(control));

        control.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateColors(control)
        );

        control.indeterminateProperty().addListener(
                (observable, oldValue, newValue) -> updateColors(control)
        );

        control.checkedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors(control)
        );

        control.uncheckedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors(control)
        );
    }

    /**
     * This method is called whenever one of the following properties changes:
     * {@code selectedProperty}, {@code indeterminateProperty}, {@code checkedColor} and {@code uncheckedColor} properties
     * @param control The MFXCheckbox associated to this skin
     * @see NodeUtils
     */
    private void updateColors(MFXCheckbox control) {
        final BorderStroke borderStroke = box.getBorder().getStrokes().get(0);
        if (control.isIndeterminate()) {
            NodeUtils.updateBackground(box, control.getCheckedColor(), new Insets(4));
        } else if (control.isSelected()) {
            NodeUtils.updateBackground(box, control.getCheckedColor(), Insets.EMPTY);
            box.setBorder(new Border(new BorderStroke(
                    control.getCheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        } else {
            NodeUtils.updateBackground(box, Color.TRANSPARENT);
            box.setBorder(new Border(new BorderStroke(
                    control.getUncheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        }
    }

    /**
     * This method is called whenever the {@code markType} property changes.
     * @param control The MFXCheckbox associated to this skin
     */
    private void updateMarkType(MFXCheckbox control) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(control.getMarkType().getSvhPath());
        mark.setShape(svgPath);
    }

    /**
     * Centers the box in the ripple container
     */
    private void centerBox() {
        final double offsetPercentage = 3;
        final double vInset = ((rippleContainerHeight - boxHeight) / 2) * offsetPercentage;
        final double hInset = ((rippleContainerWidth - boxWidth) / 2) * offsetPercentage;
        AnchorPane.setTopAnchor(box, vInset);
        AnchorPane.setRightAnchor(box, hInset);
        AnchorPane.setBottomAnchor(box, vInset);
        AnchorPane.setLeftAnchor(box, hInset);
    }

    public Pane getRippleContainer() {
        return rippleContainer;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippleContainer != null) {
            getChildren().remove(1);
            getChildren().add(rippleContainer);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) +
                snapSizeX(rippleContainer.minWidth(-1));
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) +
                snapSizeX(rippleContainer.prefWidth(-1));
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width - rippleContainer.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + rippleContainer.minHeight(-1) + bottomInset) + topInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computePrefHeight(width - rippleContainer.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + rippleContainer.prefHeight(-1) + bottomInset);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        final CheckBox checkBox = getSkinnable();

        final double boxWidth = snapSizeX(rippleContainer.prefWidth(-1));
        final double boxHeight = snapSizeY(rippleContainer.prefHeight(-1));
        final double computeWidth = Math.max(checkBox.prefWidth(-1), checkBox.minWidth(-1));
        final double labelWidth = Math.min( computeWidth - boxWidth, w - snapSizeX(boxWidth));
        final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double xOffset = NodeUtils.computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
        final double yOffset = NodeUtils.computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + y;

        layoutLabelInArea(xOffset + boxWidth + labelOffset, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
        rippleContainer.resize(boxWidth, boxHeight);
        positionInArea(rippleContainer, xOffset, yOffset, boxWidth, maxHeight, 0, checkBox.getAlignment().getHpos(), checkBox.getAlignment().getVpos());

        centerBox();
    }
}


