package io.github.palexdev.materialfx.filter;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.*;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import io.github.palexdev.materialfx.controls.enums.Styles;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.effects.ripple.RippleClipType;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This dialog provides a graphical way of filtering a given list of T items based
 * on the conditions specified by the added evaluation boxes.
 *
 * @see MFXEvaluationBox
 */
public class MFXFilterDialog<T> extends MFXDialog {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-filter-dialog";
    private final String STYLESHEET = MFXResourcesLoader.load("css/MFXFilterDialog.css");

    private final MFXIconWrapper closeIcon;
    private final MFXLabel label;
    private final MFXButton filterButton;
    private final MFXButton addAnd;
    private final MFXButton addOr;
    private final MFXButton clear;
    private final ObservableList<MFXEvaluationBox> evaluationBoxes = FXCollections.observableArrayList();

    //================================================================================
    // Constructors
    //================================================================================
    public MFXFilterDialog() {
        setPrefSize(720, 400);
        setTitle("Filter Dialog");

        MFXFontIcon closeFontIcon = new MFXFontIcon("mfx-x-circle", 18, Color.web("#4D4D4D"));
        closeFontIcon.colorProperty().bind(Bindings.createObjectBinding(
                () -> closeFontIcon.isHover() ? Color.web("#EF6E6B") : Color.web("#4D4D4D"),
                closeFontIcon.hoverProperty()
        ));

        closeIcon = new MFXIconWrapper(closeFontIcon, 20);
        closeIcon.setManaged(false);

        label = new MFXLabel();
        label.setId("headerLabel");
        label.setLabelStyle(Styles.LabelStyles.STYLE2);
        label.setPrefSize(USE_COMPUTED_SIZE, 32);
        label.setMaxSize(Double.MAX_VALUE, USE_PREF_SIZE);
        label.setPadding(new Insets(10, 0, 0, 0));
        label.textProperty().bind(titleProperty());
        label.setLeadingIcon(new MFXFontIcon("mfx-filter-alt", 16));
        label.setMouseTransparent(true);
        label.getStylesheets().setAll(STYLESHEET);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(10.0));

        MFXListView<MFXEvaluationBox> listView = new MFXListView<>();
        listView.setDepthLevel(DepthLevel.LEVEL0);
        listView.setHideScrollBars(true);
        listView.setItems(evaluationBoxes);
        listView.setCellFactory(box -> {
            MFXListCell<MFXEvaluationBox> cell = new MFXListCell<>() {
                @Override
                protected void setupRippleGenerator() {
                }
            };
            cell.addEventHandler(MouseEvent.MOUSE_PRESSED, Event::consume);
            cell.setHoverColor(Color.WHITE);
            cell.setSelectedColor(Color.WHITE);
            return cell;
        });
        listView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        HBox buttonsBox = new HBox(30);
        buttonsBox.setPrefHeight(60);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getStylesheets().add(STYLESHEET);

        filterButton = new MFXButton("Filter");
        addAnd = new MFXButton("Add \"AND\"");
        addOr = new MFXButton("Add \"OR\"");
        clear = new MFXButton("Clear");

        filterButton.setPrefSize(110, 32);
        addAnd.setPrefSize(110, 32);
        addOr.setPrefSize(110, 32);
        clear.setPrefSize(110, 32);

        filterButton.getRippleGenerator().setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(10).build(filterButton));
        addAnd.getRippleGenerator().setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(10).build(addAnd));
        addOr.getRippleGenerator().setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(10).build(addOr));
        clear.getRippleGenerator().setClipSupplier(() -> new RippleClipTypeFactory(RippleClipType.ROUNDED_RECTANGLE).setArcs(10).build(clear));

        stackPane.getChildren().add(listView);
        buttonsBox.getChildren().addAll(filterButton, addAnd, addOr, clear);

        setTop(label);
        setCenter(stackPane);
        setBottom(buttonsBox);

        setCloseButtons(closeFontIcon);
        getChildren().add(closeIcon);
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        addFilterBox(EvaluationMode.AND);
        setBehavior();
    }

    /**
     * Sets the buttons behavior
     */
    private void setBehavior() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> requestFocus());

        addAnd.setOnAction(event -> addFilterBox(EvaluationMode.AND));
        addOr.setOnAction(event -> addFilterBox(EvaluationMode.OR));
        clear.setOnAction(event -> evaluationBoxes.clear());
    }

    /**
     * Filters the given list and returns an observable filtered list.
     * <p></p>
     * Calls {@link #filter(String)} on each item for filtering.
     * <p></p>
     * <b>N.B:</b> The evaluation is done by calling the item's toString method or, if the item implements {@link IFilterable},
     * by calling {@link IFilterable#toFilterString()}. If the toString method is not overridden
     * or does not contain any useful information for filtering it won't work.
     */
    public ObservableList<T> filter(List<T> list) {
        return list.stream()
                .filter(item -> {
                    if (item instanceof IFilterable) {
                        IFilterable fData = (IFilterable) item;
                        return filter(fData.toFilterString());
                    } else {
                        return filter(item.toString());
                    }
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * Tests all the evaluation boxes conditions on the given string.
     */
    private boolean filter(String filterString) {
        Boolean expression = null;
        for (MFXEvaluationBox box : evaluationBoxes) {
            if (expression == null) {
                expression = box.test(filterString);
                continue;
            }

            boolean tmp;
            if (box.getMode() == EvaluationMode.AND) {
                tmp = expression && box.test(filterString);
            } else {
                tmp = expression || box.test(filterString);
            }
            expression = tmp;
        }

        return expression != null ? expression : false;
    }

    /**
     * Adds a new {@link MFXEvaluationBox} with the specified {@link EvaluationMode} to the dialog.
     */
    private void addFilterBox(EvaluationMode mode) {
        MFXEvaluationBox evaluationBox = new MFXEvaluationBox(mode);
        evaluationBox.getRemoveIcon().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> evaluationBoxes.remove(evaluationBox));
        evaluationBoxes.add(evaluationBox);
    }

    /**
     * @return the filter button instance
     */
    public MFXButton getFilterButton() {
        return filterButton;
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        double ciSize = closeIcon.getSize();
        double ciX = snapPositionX(getWidth() - 27);
        double ciY = snapPositionY(ciSize / 2.0);
        closeIcon.resizeRelocate(ciX, ciY, ciSize, ciSize);
    }
}
