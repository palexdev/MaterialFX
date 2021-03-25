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

import io.github.palexdev.materialfx.beans.MFXLoaderBean;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.LoaderUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Convenience class for creating dashboards, no more hassle on managing multiple views.
 * <p>
 * This control extends {@code VBox} and has a {@code ThreadExecutorService} for loading fxml files in background
 * leaving the UI responsive
 * <p></p>
 * Every time an fxml file is submitted with '{@code addItem}' a wrapper class (MFXItem) is created,
 * then it's sent to the '{@code load}' method which creates a {@code Task} and submits it to the executor.
 * <p>
 * Everytime an MFXItem is loaded, a new ToggleButton is added to the {@code VBox}, the button already
 * has a listener on the selectedProperty for switching the view and since nodes are cached the transition is faster
 * than loading the fxml again.
 * <p></p>
 * Every toggle button is part of a ToggleGroup which is modified with
 * the {@code ToggleButtonsUtil} class  to add 'always one selected' support
 */
public class MFXVLoader extends VBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-vloader";
    private Pane contentPane;
    private final ToggleGroup toggleGroup;

    private final BooleanProperty isAnimated = new SimpleBooleanProperty(false);
    private final DoubleProperty animationMillis = new SimpleDoubleProperty(800);
    private MFXAnimationFactory animationType = MFXAnimationFactory.FADE_IN;

    private final IntegerProperty loadedItems = new SimpleIntegerProperty(0);
    private final Map<String, MFXLoaderBean> idViewMap;
    private Supplier<FXMLLoader> fxmlLoaderSupplier;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXVLoader() {
        this(null);
    }

    public MFXVLoader(Pane contentPane) {
        this(contentPane, null);
    }

    public MFXVLoader(Pane contentPane, Supplier<FXMLLoader> fxmlLoaderSupplier) {
        this.setPrefSize(Region.USE_COMPUTED_SIZE, 60);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);

        this.contentPane = contentPane;
        this.toggleGroup = new ToggleGroup();

        this.idViewMap = new LinkedHashMap<>();

        this.fxmlLoaderSupplier = fxmlLoaderSupplier;
        initialize();
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
        addListeners();
    }
    
    private void addListeners() {
        loadedItems.addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == idViewMap.size()) {
                ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
                getChildren().setAll(idViewMap.values().stream()
                        .map(MFXLoaderBean::getButton)
                        .collect(Collectors.toList())
                );
                setDefault();
            }
        });
    }
    public void addItem(MFXLoaderBean.Builder builder) {
        MFXLoaderBean loaderBean = builder.get();
        LoaderUtils.checkFxmlFile(loaderBean.getFxmlURL());
        loaderBean.getButton().setToggleGroup(toggleGroup);
        idViewMap.put(LoaderUtils.generateKey(loaderBean.getFxmlURL()), loaderBean);
    }

    public void addItem(String key, MFXLoaderBean.Builder builder) {
        MFXLoaderBean loaderBean = builder.get();
        LoaderUtils.checkFxmlFile(loaderBean.getFxmlURL());
        loaderBean.getButton().setToggleGroup(toggleGroup);
        idViewMap.put(key, loaderBean);
    }

    private void setDefault() {
        idViewMap.values().stream()
                .filter(MFXLoaderBean::isDefault)
                .findFirst().ifPresent(loaderBean -> Platform.runLater(() -> loaderBean.getButton().setSelected(true)));
    }

    public void start() {
        List<MFXLoaderBean> loaderBeans = new ArrayList<>(idViewMap.values());
        for (MFXLoaderBean loaderBean : loaderBeans) {
            if (loaderBean.getRoot() == null) {
                try {
                    LoaderUtils.submit(buildLoadCallable(loaderBean)).get();
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                }
                loadedItems.set(loadedItems.get() + 1);
            }
        }
    }

    private Callable<Node> buildLoadCallable(MFXLoaderBean loaderBean) {
        return () -> {
            Node root;
            if (fxmlLoaderSupplier != null) {
                root = LoaderUtils.fxmlLoad(fxmlLoaderSupplier.get(), loaderBean);
            } else {
                root = LoaderUtils.fxmlLoad(loaderBean);
            }
            loaderBean.setRoot(root);

            loaderBean.getButton().selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (isAnimated.get()) {
                    animationType.build(loaderBean.getRoot(), animationMillis.doubleValue()).play();
                }
                if (newValue) {
                    try {
                        contentPane.getChildren().set(0, loaderBean.getRoot());
                    } catch (IndexOutOfBoundsException ex) {
                        contentPane.getChildren().add(0, loaderBean.getRoot());
                    }
                }
            });
            return root;
        };
    }
    
    public MFXLoaderBean getLoadItem(String key) {
        return this.idViewMap.get(key);
    }

    public Pane getContentPane() {
        return contentPane;
    }

    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    /**
     * Sets the fxmlLoaderSupplier to the given parameter.
     * <p>
     * <b>NOTICE: this method won't do anything if the fxmlLoaderSupplier is not null.
     * This method is intended to be used when the loader is used in SceneBuilder which requires a no-arg constructor.</b>
     */
    public void setFxmlLoaderSupplier(Supplier<FXMLLoader> fxmlLoaderSupplier) {
        if (this.fxmlLoaderSupplier == null) {
            this.fxmlLoaderSupplier = fxmlLoaderSupplier;
        }
    }

    public boolean isIsAnimated() {
        return isAnimated.get();
    }

    public BooleanProperty isAnimatedProperty() {
        return isAnimated;
    }

    public void setIsAnimated(boolean isAnimated) {
        this.isAnimated.set(isAnimated);
    }

    public double getAnimationMillis() {
        return animationMillis.get();
    }

    public DoubleProperty animationMillisProperty() {
        return animationMillis;
    }

    public void setAnimationMillis(double animationMillis) {
        this.animationMillis.set(animationMillis);
    }

    public MFXAnimationFactory getAnimationType() {
        return animationType;
    }

    public void setAnimationType(MFXAnimationFactory animationType) {
        this.animationType = animationType;
    }
}
