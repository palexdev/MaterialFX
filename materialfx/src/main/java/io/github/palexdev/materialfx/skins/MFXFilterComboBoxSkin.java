package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXFlowlessListView;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessListCell;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.ComboSelectionModelMock;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.*;
import javafx.collections.MapChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.function.Predicate;

// TODO implement StringConverter (low priority)

/**
 * This is the implementation of the Skin associated with every MFXFilterComboBox.
 */
public class MFXFilterComboBoxSkin<T> extends SkinBase<MFXFilterComboBox<T>> {
    //================================================================================
    // Properties
    //================================================================================
    private final HBox container;
    private final Label valueLabel;
    private final double minWidth = 120;

    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXFlowlessListView<T> listView;
    private final EventHandler<MouseEvent> popupHandler;

    private final Line unfocusedLine;
    private final Line focusedLine;

    private final HBox searchContainer;
    private final FilteredList<T> filteredList;
    private MFXTextField searchField;

    private Timeline arrowAnimation;

    private int retryCount;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterComboBoxSkin(MFXFilterComboBox<T> comboBox) {
        super(comboBox);

        unfocusedLine = new Line();
        unfocusedLine.getStyleClass().add("unfocused-line");
        unfocusedLine.setManaged(false);
        unfocusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        unfocusedLine.strokeProperty().bind(comboBox.unfocusedLineColorProperty());
        unfocusedLine.setSmooth(true);
        unfocusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));

        focusedLine = new Line();
        focusedLine.getStyleClass().add("focused-line");
        focusedLine.setManaged(false);
        focusedLine.strokeWidthProperty().bind(comboBox.lineStrokeWidthProperty());
        focusedLine.strokeProperty().bind(comboBox.lineColorProperty());
        focusedLine.setSmooth(true);
        focusedLine.endXProperty().bind(comboBox.widthProperty().subtract(1));
        focusedLine.setScaleX(0.0);

        filteredList = new FilteredList<>(comboBox.getItems());

        valueLabel = buildLabel();

        MFXFontIcon fontIcon = new MFXFontIcon("mfx-caret-down", 12);
        icon = new MFXIconWrapper(fontIcon, 24).addRippleGenerator();
        icon.setManaged(false);
        icon.getStylesheets().addAll(comboBox.getUserAgentStylesheet());
        NodeUtils.makeRegionCircular(icon, 10);

        container = new HBox(20, valueLabel);
        container.setAlignment(Pos.CENTER_LEFT);

        searchContainer = new HBox(10);
        searchContainer.setId("search-node");
        searchContainer.setPadding(new Insets(0, 5, 0, 5));
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setManaged(false);

        listView = new MFXFlowlessListView<>() {
            {
                setCellFactory(item -> new MFXFlowlessListCell<>(this, item) {
                    @Override
                    public void updateIndex(int index) {
                        setIndex(index);
                        if (containsEqualsBoth() && !isSelected()) {
                            setSelected(true);
                            return;
                        }
                        if (containsNotEqualsIndex()) {
                            listView.getSelectionModel().updateIndex(getData(), index);
                            setSelected(true);
                        }
                    }
                });
            }
        };
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());
        popup = buildPopup();
        popupHandler = event -> {
            if (popup.isShowing() && !NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), comboBox)) {
                buildAndPlayLinesAnimation(false);
                reset();
            }
        };

        if (comboBox.getComboStyle() == Styles.ComboBoxStyles.STYLE1) {
            getChildren().addAll(container, icon, unfocusedLine, focusedLine);
        } else {
            getChildren().addAll(container, icon);
        }

        setBehavior();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Calls the methods which define the control behavior.
     */
    private void setBehavior() {
        comboBehavior();
        selectionBehavior();
        popupBehavior();
        listBehavior();
        iconBehavior();
    }

    //================================================================================
    // Behavior
    //================================================================================

    /**
     * Specifies the behavior for comboStyleProperty change, mouse pressed events and focus change.
     */
    private void comboBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        // STYLE
        comboBox.comboStyleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Styles.ComboBoxStyles.STYLE2) {
                getChildren().removeAll(unfocusedLine, focusedLine);
            } else {
                getChildren().addAll(unfocusedLine, focusedLine);
            }
        });

        // MOUSE PRESSED
        comboBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            comboBox.requestFocus();

            if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0) {
                forceRipple();
                show();
            }
        });

        // FOCUS
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            boolean fieldCondition = searchField != null && searchField.isFocused();
            if (fieldCondition) {
                return;
            }

            if (comboBox.isAnimateLines()) {
                buildAndPlayLinesAnimation(newValue);
                return;
            }

            if (newValue) {
                focusedLine.setScaleX(1.0);
            } else {
                focusedLine.setScaleX(0.0);
            }
        });
    }

    /**
     * Specifies the behavior for selectedValue, listview selection, combo box selection and filtered list change.
     */
    private void selectionBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();
        ComboSelectionModelMock<T> selectionModel = comboBox.getSelectionModel();

        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                comboBox.setSelectedValue(newValue);
            }
        });

        listView.getSelectionModel().selectedItemsProperty().addListener((MapChangeListener<? super Integer, ? super T>) change -> {
            T item = change.getValueAdded();
            if (item != null) {
                selectionModel.selectItem(item);
            }
        });

        comboBox.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setValueLabel(newValue);
            } else {
                valueLabel.setText("");
                valueLabel.setGraphic(null);
            }
        });
    }

    /**
     * Specifies the behavior for maxPopupHeight and maxPopupWidth properties, also adds the
     * {@link #popupHandler} to the scene to close the popup in case it is open and the mouse is not
     * pressed on the combo box.
     */
    private void popupBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        comboBox.maxPopupHeightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == -1) {
                listView.maxHeightProperty().unbind();
            } else {
                listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
            }
        });
        comboBox.maxPopupWidthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == -1) {
                listView.maxWidthProperty().unbind();
            } else {
                listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());
            }
        });

        comboBox.skinProperty().addListener((observable, oldValue, newValue) -> comboBox.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler));
        comboBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
            }
            if (newValue != null) {
                newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
            }
        });

        popup.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            ComboSelectionModelMock<T> selectionModelMock = comboBox.getSelectionModel();
            IListSelectionModel<T> listSelectionModel = listView.getSelectionModel();
            if (selectionModelMock.getSelectedItem() != null) {
                listSelectionModel.select(selectionModelMock.getSelectedIndex(), selectionModelMock.getSelectedItem(), null);
            }
        });
    }

    /**
     * Specifies the be behavior for the listview, binds its sizes to maxPopupHeight and maxPopupWidth
     * properties and resets the control when the mouse is pressed.
     *
     * @see #reset()
     */
    private void listBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        listView.maxHeightProperty().bind(comboBox.maxPopupHeightProperty());
        listView.maxWidthProperty().bind(comboBox.maxPopupWidthProperty());
        listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                reset();
            }
        });
    }

    /**
     * Specifies the behavior of the caret icon, sets up the ripple generator and
     * the popup handling when the mouse is pressed.
     */
    private void iconBehavior() {
        RippleGenerator rg = icon.getRippleGenerator();
        rg.setRippleRadius(8);
        rg.setInDuration(Duration.millis(350));

        icon.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            rg.setGeneratorCenterX(icon.getWidth() / 2);
            rg.setGeneratorCenterY(icon.getHeight() / 2);
            rg.createRipple();
        });

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (!popup.isShowing()) {
                show();
            } else {
                reset();
            }
        });
        icon.getIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (popup.isShowing()) {
                popup.hide();
                forceRipple();
                getSkinnable().requestFocus();
                event.consume();
            }
        });
    }

    /**
     * This method builds the label used to display the selected value of the combo box.
     */
    protected Label buildLabel() {
        Label label = new Label("");
        label.setMinWidth(snappedLeftInset() + minWidth + snappedRightInset());
        label.setMouseTransparent(true);

        return label;
    }

    /**
     * This method build the combo box popup and initializes the listview.
     */
    protected PopupControl buildPopup() {
        PopupControl popupControl = new PopupControl();

        listView.setItems(filteredList);
        popupControl.getScene().setRoot(listView);
        popupControl.setOnShowing(event -> buildAnimation(true).play());
        popupControl.setOnHiding(event -> {
            buildAnimation(false).play();
            if (containsEditor()) {
                container.getChildren().remove(searchContainer);
            }
        });

        return popupControl;
    }

    /**
     * Shows the popup.
     */
    private void show() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        if (!containsEditor()) {
            showEditor();
        }
        Point2D point = NodeUtils.pointRelativeTo(
                comboBox,
                listView,
                HPos.CENTER,
                VPos.BOTTOM,
                comboBox.getPopupXOffset(),
                comboBox.getPopupYOffset(),
                false
        );
        popup.show(comboBox, snapPositionX(point.getX()), snapPositionY(point.getY()));
    }

    /**
     * Closes the popup and resets the control.
     * <p>
     * Removes the filter text field, clears the predicate of the filteredList,
     * sets the value label to visible and requests the focus.
     */
    private void reset() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        popup.hide();
        container.getChildren().remove(searchContainer);
        filteredList.setPredicate(null);
        valueLabel.setVisible(true);
        listView.setItems(filteredList);
        comboBox.requestFocus();
    }

    /**
     * This method is quite similar to the one used in MFXLabel.
     * <p>
     * This method build the text field used to filter the listview. When text changes
     * the filteredList is updated with a new predicate.
     * <p>
     * By default when the text field loses the focus the control is reset.
     * <p>
     * The text field is unmanaged so its position is calculated by {@link #computeEditorPosition()}
     * <p>
     * When the popup is shown and the text field is added to the scene the text field is not focused,
     * to change this behavior and force it to be focused you can use {@link MFXFilterComboBox#setForceFieldFocusOnShow(boolean)}
     * and set it to true.
     *
     * @see #reset()
     */
    private void showEditor() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        MFXFontIcon searchIcon = new MFXFontIcon("mfx-search", 16);

        valueLabel.setVisible(false);
        searchField = new MFXTextField("");
        comboBox.editorFocusedProperty().bind(searchField.focusedProperty());
        searchField.setPromptText("Search...");
        searchField.setId("search-field");
        searchField.getStylesheets().setAll(comboBox.getUserAgentStylesheet());
        searchField.setUnfocusedLineColor(Color.TRANSPARENT);
        searchField.setLineColor(Color.TRANSPARENT);
        searchField.setFocusTraversable(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(getPredicate(searchField));
            listView.setItems(filteredList);
        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && popup.isShowing()) {
                reset();
            }
        });

        searchContainer.getChildren().setAll(searchIcon, searchField);
        container.getChildren().add(searchContainer);
        computeEditorPosition();
        if (comboBox.isForceFieldFocusOnShow()) {
            forceFieldFocus();
        }
    }

    /*
     * Responsible for showing the editor correctly, handles its size and location.
     */
    private void computeEditorPosition() {
        double posX = container.getBoundsInParent().getMinX();
        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();
        searchContainer.resizeRelocate(posX, 0, containerWidth, containerHeight);
    }

    /**
     * The only way to force the focus on the text field is to poll its state by running
     * a PauseTransition every n milliseconds (by default 100). If at the end of the transition the text field
     * is not focused yet the transition is played again.
     * <p>
     * Since this may produce an unexpected behavior by running indefinitely for example I set a max
     * retry count of 10.
     */
    protected void forceFieldFocus() {
        PauseTransition transition = new PauseTransition(Duration.millis(100));
        transition.setOnFinished(event -> {
            if (searchField != null && !searchField.isFocused() && retryCount < 10) {
                retryCount++;
                searchField.requestFocus();
                transition.playFromStart();
            }
        });
        transition.play();
    }

    private void forceRipple() {
        RippleGenerator rg = icon.getRippleGenerator();
        rg.setGeneratorCenterX(icon.getWidth() / 2);
        rg.setGeneratorCenterY(icon.getHeight() / 2);
        rg.createRipple();
    }

    /**
     * Checks if the editor is already shown.
     */
    private boolean containsEditor() {
        Node editor = container.getChildren().stream()
                .filter(node -> node.getId() != null && node.getId().equals("search-node"))
                .findFirst()
                .orElse(null);
        return editor != null;
    }

    /**
     * Return the Predicate to filter the popup items based on the search field.
     */
    private Predicate<T> getPredicate(MFXTextField textField) {
        String searchText = textField.getText().trim();
        if (searchText.isEmpty()) {
            return null;
        }
        return getPredicate(searchText);
    }

    /**
     * Return the Predicate to filter the popup items based on the given search text.
     */
    private Predicate<T> getPredicate(String searchText) {
        String[] words = searchText.split(" ");
        return value ->
        {
            String displayText = value.toString().toLowerCase();
            return Arrays.stream(words).allMatch(word -> StringUtils.containsIgnoreCase(displayText, word));
        };
    }

    /**
     * Sets the label text according to the combo box selected item.
     * <p>
     * If the item is instance of {@code Labeled} then whe check if the item has a graphic != null
     * and use the item text. If that's not the case then we call toString on the item.
     */
    private void setValueLabel(T item) {
        if (item instanceof Labeled) {
            Labeled nodeItem = (Labeled) item;
            if (nodeItem.getGraphic() != null) {
                valueLabel.setGraphic(new MFXSnapshotWrapper(nodeItem.getGraphic()).getGraphic());
            }
            valueLabel.setText(nodeItem.getText());
        } else {
            valueLabel.setText(item.toString());
        }
    }

    /**
     * Builds the animation for the combo box arrow.
     */
    private Timeline buildAnimation(boolean isShowing) {
        KeyFrame kf0 = new KeyFrame(Duration.millis(150),
                new KeyValue(icon.rotateProperty(), (isShowing ? 180 : 0))
        );
        arrowAnimation = new Timeline(kf0);
        return arrowAnimation;
    }

    /**
     * Builds the focus animation.
     */
    private void buildAndPlayLinesAnimation(boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(350), focusedLine);
        if (focused) {
            scaleTransition.setFromX(0.0);
            scaleTransition.setToX(1.0);
        } else {
            scaleTransition.setFromX(1.0);
            scaleTransition.setToX(0.0);
        }
        scaleTransition.setInterpolator(MFXAnimationFactory.getInterpolatorV2());
        scaleTransition.play();
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return leftInset + minWidth + rightInset;
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset), 30);
    }

    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (arrowAnimation != null) {
            arrowAnimation = null;
        }
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        double iconWidth = icon.getPrefWidth();
        double iconHeight = icon.getPrefHeight();
        double center = ((snappedTopInset() + snappedBottomInset()) / 2.0) + ((contentHeight - iconHeight) / 2.0);
        icon.resizeRelocate(contentWidth - iconWidth, center, iconWidth, iconHeight);
        focusedLine.relocate(0, contentHeight);
        unfocusedLine.relocate(0, contentHeight);
    }
}
