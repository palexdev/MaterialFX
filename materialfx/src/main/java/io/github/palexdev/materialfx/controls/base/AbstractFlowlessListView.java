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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.List;

public abstract class AbstractFlowlessListView<T, C extends AbstractMFXFlowlessListCell<T>, S extends IListSelectionModel<T>> extends Control implements IListView<T, C, S> {
    private static final StyleablePropertyFactory<AbstractFlowlessListView<?, ?, ?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    protected final ReadOnlyObjectWrapper<ObservableList<T>> items = new ReadOnlyObjectWrapper<>(FXCollections.observableArrayList());
    protected final ObjectProperty<Callback<T, C>> celFactory = new SimpleObjectProperty<>();
    protected final ObjectProperty<S> selectionModel = new SimpleObjectProperty<>();

    public AbstractFlowlessListView() {
        this(FXCollections.observableArrayList());
    }

    public AbstractFlowlessListView(ObservableList<T> items) {
        setItems(items);
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

    protected abstract void setDefaultCellFactory();
    protected abstract void setDefaultSelectionModel();

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


    @Override
    public ObservableList<T> getItems() {
        return items.get();
    }

    @Override
    public ReadOnlyObjectProperty<ObservableList<T>> itemsProperty() {
        return items.getReadOnlyProperty();
    }

    @Override
    public void setItems(ObservableList<T> items) {
        getItems().setAll(items);
    }

    @Override
    public Callback<T, C> getCellFactory() {
        return celFactory.get();
    }

    @Override
    public ObjectProperty<Callback<T, C>> cellFactoryProperty() {
        return celFactory;
    }

    @Override
    public void setCellFactory(Callback<T, C> cellFactory) {
        this.celFactory.set(cellFactory);
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

    @Override
    protected abstract Skin<?> createDefaultSkin();

    //================================================================================
    // ScrollBars Properties
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

    /**
     * Specifies the time after which the scrollbars are hidden.
     */
    private final ObjectProperty<Duration> hideAfter = new SimpleObjectProperty<>(Duration.seconds(1));

    //================================================================================
    // Styleable Properties
    //================================================================================

    /**
     * Specifies if the scrollbars should be hidden when the mouse is not on the list.
     */
    private final StyleableBooleanProperty hideScrollBars = new SimpleStyleableBooleanProperty(
            StyleableProperties.HIDE_SCROLLBARS,
            this,
            "hideScrollBars",
            false
    );

    /**
     * Specifies the shadow strength around the control.
     */
    private final StyleableObjectProperty<DepthLevel> depthLevel = new SimpleStyleableObjectProperty<>(
            StyleableProperties.DEPTH_LEVEL,
            this,
            "depthLevel",
            DepthLevel.LEVEL2
    );

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

    public Duration getHideAfter() {
        return hideAfter.get();
    }

    public ObjectProperty<Duration> hideAfterProperty() {
        return hideAfter;
    }

    public void setHideAfter(Duration hideAfter) {
        this.hideAfter.set(hideAfter);
    }

    public boolean isHideScrollBars() {
        return hideScrollBars.get();
    }

    public StyleableBooleanProperty hideScrollBarsProperty() {
        return hideScrollBars;
    }

    public void setHideScrollBars(boolean hideScrollBars) {
        this.hideScrollBars.set(hideScrollBars);
    }

    public DepthLevel getDepthLevel() {
        return depthLevel.get();
    }

    public StyleableObjectProperty<DepthLevel> depthLevelProperty() {
        return depthLevel;
    }

    public void setDepthLevel(DepthLevel depthLevel) {
        this.depthLevel.set(depthLevel);
    }

    private static class StyleableProperties {
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<AbstractFlowlessListView<?, ?, ?>, Boolean> HIDE_SCROLLBARS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-hide-scrollbars",
                        AbstractFlowlessListView::hideScrollBarsProperty,
                        false
                );

        private static final CssMetaData<AbstractFlowlessListView<?, ?, ?>, DepthLevel> DEPTH_LEVEL =
                FACTORY.createEnumCssMetaData(
                        DepthLevel.class,
                        "-mfx-depth-level",
                        AbstractFlowlessListView::depthLevelProperty,
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
        return AbstractFlowlessListView.getControlCssMetaDataList();
    }
}
