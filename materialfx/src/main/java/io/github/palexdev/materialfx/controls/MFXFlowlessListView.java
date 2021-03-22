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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.controls.base.AbstractMFXFlowlessListCell;
import io.github.palexdev.materialfx.controls.cell.MFXFlowlessListCell;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.materialfx.selection.ListSelectionModel;
import io.github.palexdev.materialfx.selection.base.IListSelectionModel;
import io.github.palexdev.materialfx.skins.MFXFlowlessListViewSkin;
import io.github.palexdev.materialfx.utils.ColorUtils;
import javafx.beans.property.ObjectProperty;
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

public class MFXFlowlessListView<T> extends Control {
    private static final StyleablePropertyFactory<MFXFlowlessListView<?>> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
    private final String STYLE_CLASS = "mfx-list-view";
    private final String STYLESHEET = MFXResourcesLoader.load("css/mfx-flowless-listview.css").toString();

    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>();
    private final ObjectProperty<Callback<T, AbstractMFXFlowlessListCell<T>>> cellFactory = new SimpleObjectProperty<>();
    private final ObjectProperty<IListSelectionModel<T>> selectionModel = new SimpleObjectProperty<>();

    public MFXFlowlessListView() {
        this(FXCollections.observableArrayList());
    }

    public MFXFlowlessListView(ObservableList<T> items) {
        setItems(items);
        initialize();
    }

    private void initialize() {
        getStyleClass().add(STYLE_CLASS);

        setCellFactory(MFXFlowlessListCell::new);
        setupSelectionModel();
        addListeners();
    }

    protected void setupSelectionModel() {
        IListSelectionModel<T> selectionModel = new ListSelectionModel<>();
        selectionModel.setAllowsMultipleSelection(true);
        setSelectionModel(selectionModel);
    }

    /**
     * Adds listeners for colors change to the scrollbars and calls setColors().
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
     * Sets the CSS looked-up colors
     */
    private void setColors() {
        StringBuilder sb = new StringBuilder();
        sb.append("-mfx-track-color: ").append(ColorUtils.rgb((Color) trackColor.get()))
                .append(";\n-mfx-thumb-color: ").append(ColorUtils.rgb((Color) thumbColor.get()))
                .append(";\n-mfx-thumb-hover-color: ").append(ColorUtils.rgb((Color) thumbHoverColor.get()))
                .append(";");
        setStyle(sb.toString());
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public Callback<T, AbstractMFXFlowlessListCell<T>> getCellFactory() {
        return cellFactory.get();
    }

    public ObjectProperty<Callback<T, AbstractMFXFlowlessListCell<T>>> cellFactoryProperty() {
        return cellFactory;
    }

    public void setCellFactory(Callback<T, AbstractMFXFlowlessListCell<T>> cellFactory) {
        this.cellFactory.set(cellFactory);
    }

    public IListSelectionModel<T> getSelectionModel() {
        return selectionModel.get();
    }

    public ObjectProperty<IListSelectionModel<T>> selectionModelProperty() {
        return selectionModel;
    }

    public void setSelectionModel(IListSelectionModel<T> selectionModel) {
        this.selectionModel.set(selectionModel);
    }

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

        private static final CssMetaData<MFXFlowlessListView<?>, Boolean> HIDE_SCROLLBARS =
                FACTORY.createBooleanCssMetaData(
                        "-mfx-hide-scrollbars",
                        MFXFlowlessListView::hideScrollBarsProperty,
                        false
                );

        private static final CssMetaData<MFXFlowlessListView<?>, DepthLevel> DEPTH_LEVEL =
                FACTORY.createEnumCssMetaData(
                        DepthLevel.class,
                        "-mfx-depth-level",
                        MFXFlowlessListView::depthLevelProperty,
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
    protected Skin<?> createDefaultSkin() {
        return new MFXFlowlessListViewSkin<>(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLESHEET;
    }

    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return MFXFlowlessListView.getControlCssMetaDataList();
    }
}
