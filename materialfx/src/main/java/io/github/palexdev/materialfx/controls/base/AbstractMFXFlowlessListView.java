/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.controls.base;

import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Function;

/**
 * Base class for all list views based on Flowless, defines common properties and behavior.
 *
 * @param <T> the type of data within the ListView
 */
public abstract class AbstractMFXFlowlessListView<T, C extends AbstractMFXFlowlessListCell<T>, S extends IListSelectionModel<T>> extends Control implements IListView<T, C, S> {
    //================================================================================
    // Properties
    //================================================================================
    private static final StyleablePropertyFactory<AbstractMFXFlowlessListView<?, ?, ?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    protected final ObservableList<T> items = FXCollections.observableArrayList();
    protected final ObjectProperty<Function<T, C>> cellFactory = new SimpleObjectProperty<>();
    protected final ObjectProperty<S> selectionModel = new SimpleObjectProperty<>();

    //================================================================================
    // Constructors
    //================================================================================
    public AbstractMFXFlowlessListView() {
        this(FXCollections.observableArrayList());
    }

    public AbstractMFXFlowlessListView(List<T> items) {
        setItems(items);
    }

    //================================================================================
    // Abstract Methods
    //================================================================================

    /**
     * Abstract method called by the constructor to set a default factory for the cells.
     */
    protected abstract void setDefaultCellFactory();

    /**
     * Abstract method called by the constructor to set a default selection model.
     */
    protected abstract void setDefaultSelectionModel();

    //================================================================================
    // Methods
    //================================================================================
    protected void initialize() {
        setDefaultCellFactory();
        setDefaultSelectionModel();
        addBarsListeners();
    }

    protected void addBarsListeners() {
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
     * Sets the CSS looked-up colors
     */
    protected void setColors() {
        StringBuilder sb = new StringBuilder();
        sb.append("-mfx-track-color: ").append(ColorUtils.rgb((Color) trackColor.get()))
                .append(";\n-mfx-thumb-color: ").append(ColorUtils.rgb((Color) thumbColor.get()))
                .append(";\n-mfx-thumb-hover-color: ").append(ColorUtils.rgb((Color) thumbHoverColor.get()))
                .append(";");
        setStyle(sb.toString());
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    public ObservableList<T> getItems() {
        return items;
    }

    @Override
    public void setItems(List<T> items) {
        this.items.setAll(items);
    }

    @Override
    public Function<T, C> getCellFactory() {
        return cellFactory.get();
    }

    @Override
    public ObjectProperty<Function<T, C>> cellFactoryProperty() {
        return cellFactory;
    }

    @Override
    public void setCellFactory(Function<T, C> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    @Override
    public S getSelectionModel() {
        return selectionModel.get();
    }

    @Override
    public ObjectProperty<S> selectionModelProperty() {
        return selectionModel;
    }

    @Override
    public void setSelectionModel(S selectionModel) {
        this.selectionModel.set(selectionModel);
    }

    //================================================================================
    // ScrollBars Properties
    //================================================================================
    private final ObjectProperty<Paint> trackColor = new SimpleObjectProperty<>(Color.rgb(132, 132, 132));
    private final ObjectProperty<Paint> thumbColor = new SimpleObjectProperty<>(Color.rgb(137, 137, 137));
    private final ObjectProperty<Paint> thumbHoverColor = new SimpleObjectProperty<>(Color.rgb(89, 88, 91));
    private final ObjectProperty<Duration> hideAfter = new SimpleObjectProperty<>(Duration.seconds(1));

    public Paint getTrackColor() {
        return trackColor.get();
    }

    /**
     * Specifies the color of the scrollbars' track.
     */
    public ObjectProperty<Paint> trackColorProperty() {
        return trackColor;
    }

    public void setTrackColor(Paint trackColor) {
        this.trackColor.set(trackColor);
    }

    public Paint getThumbColor() {
        return thumbColor.get();
    }

    /**
     * Specifies the color of the scrollbars' thumb.
     */
    public ObjectProperty<Paint> thumbColorProperty() {
        return thumbColor;
    }

    public void setThumbColor(Paint thumbColor) {
        this.thumbColor.set(thumbColor);
    }

    public Paint getThumbHoverColor() {
        return thumbHoverColor.get();
    }

    /**
     * Specifies the color of the scrollbars' thumb when mouse hover.
     */
    public ObjectProperty<Paint> thumbHoverColorProperty() {
        return thumbHoverColor;
    }

    public void setThumbHoverColor(Paint thumbHoverColor) {
        this.thumbHoverColor.set(thumbHoverColor);
    }

    public Duration getHideAfter() {
        return hideAfter.get();
    }

    /**
     * Specifies the time after which the scrollbars are hidden.
     */
    public ObjectProperty<Duration> hideAfterProperty() {
        return hideAfter;
    }

    public void setHideAfter(Duration hideAfter) {
        this.hideAfter.set(hideAfter);
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableBooleanProperty hideScrollBars = new SimpleStyleableBooleanProperty(
            StyleableProperties.HIDE_SCROLLBARS,
            this,
            "hideScrollBars",
            false
    );

    private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
            StyleableProperties.DEPTH_LEVEL,
            this,
            "depthLevel",
            DepthLevel.LEVEL2
    );

    public boolean isHideScrollBars() {
        return hideScrollBars.get();
    }

    /**
     * Specifies if the scrollbars should be hidden when the mouse is not on the list.
     */
    public StyleableBooleanProperty hideScrollBarsProperty() {
        return hideScrollBars;
    }

    public void setHideScrollBars(boolean hideScrollBars) {
        this.hideScrollBars.set(hideScrollBars);
    }

    public DepthLevel getDepthLevel() {
        return depthLevel.get();
    }

    /**
     * Specifies the shadow strength around the control.
     */
    public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
        return depthLevel;
    }

    public void setDepthLevel(DepthLevel depthLevel) {
        this.depthLevel.set(depthLevel);
    }

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<AbstractMFXFlowlessListView<?, ?, ?>, Boolean> HIDE_SCROLLBARS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-hide-scrollbars",
                        AbstractMFXFlowlessListView::hideScrollBarsProperty,
                        false
                );

        private static final CssMetaData<AbstractMFXFlowlessListView<?, ?, ?>, DepthLevel> DEPTH_LEVEL =
                FACTORY.createEnumCssMetaData(
                        DepthLevel.class,
                        "-mfx-depth-level",
                        AbstractMFXFlowlessListView::depthLevelProperty,
                        DepthLevel.LEVEL2
                );


        static {
            cssMetaDataList = List.of(HIDE_SCROLLBARS, DEPTH_LEVEL);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return AbstractMFXFlowlessListView.getControlCssMetaDataList();
    }
}
