package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.MFXSnapshotWrapper;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.selection.ComboSelectionModelMock;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StringUtils;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
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
import javafx.util.Duration;

import java.util.Arrays;
import java.util.function.Predicate;

public class MFXFilterComboBoxSkin<T> extends SkinBase<MFXFilterComboBox<T>> {
    private final HBox container;
    private final Label valueLabel;
    private final double minWidth = 100;

    private final MFXIconWrapper icon;
    private final PopupControl popup;
    private final MFXListView<T> listView;
    private final EventHandler<MouseEvent> popupHandler;

    private final Line unfocusedLine;
    private final Line focusedLine;

    private final HBox searchContainer;
    private final FilteredList<T> filteredList;
    private MFXTextField searchField;
    private T previousSelected;

    private Timeline arrowAnimation;

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

        listView = new MFXListView<>();
        listView.getStylesheets().add(comboBox.getUserAgentStylesheet());
        popup = buildPopup();
        popupHandler = event -> {
            if (popup.isShowing() && !NodeUtils.inHierarchy(event.getPickResult().getIntersectedNode(), comboBox)) {
                reset();
            }
        };

        if (comboBox.getComboStyle() == Styles.ComboBoxStyles.STYLE1) {
            getChildren().addAll(container, icon, unfocusedLine, focusedLine);
        } else {
            getChildren().addAll(container, icon);
        }

        setListeners();
    }

    private void setListeners() {
        comboBehavior();
        selectionBehavior();
        popupBehavior();
        listBehavior();
        iconBehavior();
    }

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

            if (event.getClickCount() >= 2) {
                if (!containsEditor() && !popup.isShowing()) {
                    show();
                }
            }
        });

        // FOCUS
        comboBox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(newValue || searchField.isFocused()) && popup.isShowing()) {
                popup.hide();
            }

            if (comboBox.isAnimateLines() && !(focusedLine.getScaleX() != 1)) {
                buildAndPlayLinesAnimation(newValue || searchField.isFocused());
                return;
            }

            if (newValue || searchField.isFocused()) {
                focusedLine.setScaleX(1.0);
            } else {
                focusedLine.setScaleX(0.0);
            }
        });
    }

    private void selectionBehavior() {
        MFXFilterComboBox<T> comboBox = getSkinnable();
        ComboSelectionModelMock<T> selectionModel = comboBox.getSelectionModel();

        comboBox.selectedValueProperty().bind(listView.getSelectionModel().selectedItemProperty());
        comboBox.selectedValueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setValueLabel(newValue);
            } else {
                valueLabel.setText("");
                valueLabel.setGraphic(null);
            }
        });
        listView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> selectionModel.selectedIndexProperty().set(newValue.intValue()));
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectionModel.selectedItemProperty().set(newValue));
        selectionModel.selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() != oldValue.intValue()) {
                if (newValue.intValue() == -1) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue.intValue());
                }
            }
        });
       selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                if (newValue == null) {
                    listView.getSelectionModel().clearSelection();
                } else {
                    listView.getSelectionModel().select(newValue);
                }
            }
        });

        filteredList.addListener((InvalidationListener) invalidated -> {
            if (selectionModel.getSelectedItem() != null) {
                previousSelected = selectionModel.getSelectedItem();
            }
            listView.setItems(filteredList);
            selectionModel.selectItem(previousSelected);
        });
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null && !selectionModel.isClearRequested()) {
                selectionModel.selectItem(previousSelected);
            }
        });
    }

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

        comboBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != oldValue) {
                if (oldValue != null) {
                    oldValue.removeEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                }
                if (newValue != null) {
                    newValue.addEventFilter(MouseEvent.MOUSE_PRESSED, popupHandler);
                }
            }
        });
    }

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

    private void iconBehavior() {
        RippleGenerator rg = icon.getRippleGenerator();
        rg.setRippleRadius(8);

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            rg.setGeneratorCenterX(icon.getWidth() / 2);
            rg.setGeneratorCenterY(icon.getHeight() / 2);
            rg.createRipple();
        });

        icon.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (!containsEditor() && !popup.isShowing()) {
                show();
            } else {
                reset();
            }
        });
    }

    private Label buildLabel() {
        Label label = new Label("");
        label.setMinWidth(snappedLeftInset() + minWidth + snappedRightInset());
        label.setMouseTransparent(true);

        return label;
    }

    private PopupControl buildPopup() {
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

    private void show() {
        MFXFilterComboBox<T> comboBox = getSkinnable();

        showEditor();
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

    private void reset() {
        popup.hide();
        container.getChildren().remove(searchContainer);
        filteredList.setPredicate(null);
        valueLabel.setVisible(true);
    }

    /**
     * If {@link MFXLabel#editableProperty()} is set to true shows the editor,
     * ESCAPE hides the editor canceling any change, ENTER hides the editor and confirms
     * the changes, if the editor looses focus (for example by clicking on another component that requests the focus)
     * the editor is hidden and any changes are confirmed.
     */
    private void showEditor() {
        MFXFontIcon searchIcon = new MFXFontIcon("mfx-search", 14);

        valueLabel.setVisible(false);
        searchField = new MFXTextField("");
        searchField.setPromptText("Search...");
        searchField.setId("search-field");
        searchField.setUnfocusedLineColor(Color.TRANSPARENT);
        searchField.setLineColor(Color.TRANSPARENT);
        searchField.setFocusTraversable(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(getPredicate(searchField)));
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && popup.isShowing()) {
                reset();
            }
        });

        searchContainer.getChildren().setAll(searchIcon, searchField);
        container.getChildren().add(searchContainer);
        computeEditorPosition();
        forceFieldFocus();
    }

    /*
     * Responsible for showing the editor correctly, handles its size and location.
     * <p>
     * Note that when the editor with is computed we set that same width as the textNode's prefWidth as well,
     * this is done so the trailing icon position is automatically managed by the container. When the editor is removed
     * the textNode's prefWidth is set to USE_COMPUTED_SIZE.
     */
    private void computeEditorPosition() {
        double posX = container.getBoundsInParent().getMinX();
        double containerWidth = container.getWidth();
        double containerHeight = container.getHeight();
        searchContainer.resizeRelocate(posX, 0, containerWidth, containerHeight);
    }

    private void forceFieldFocus() {
        PauseTransition transition = new PauseTransition(Duration.millis(200));
        transition.setOnFinished(event -> {
            if (searchField != null && !searchField.isFocused()) {
                searchField.requestFocus();
                transition.playFromStart();
            }
        });
        transition.play();
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
        return Math.max(super.computeMinHeight(width, topInset, rightInset, bottomInset, leftInset), topInset + icon.getHeight() + bottomInset);
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
