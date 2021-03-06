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

import io.github.palexdev.materialfx.beans.MFXLoadItem;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.LoaderUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Convenience class for creating dashboards, no more hassle on managing multiple views.
 * <p>
 * This control extends {@code HBox} and has a {@code ThreadExecutorService} for loading fxml files in background
 * leaving the UI responsive
 * <p></p>
 * Every time an fxml file is submitted with '{@code addItem}' a wrapper class (MFXItem) is created,
 * then it's sent to the '{@code load}' method which creates a {@code Task} and submits it to the executor.
 * <p>
 * Everytime an MFXItem is loaded, a new ToggleButton is added to the {@code HBox}, the button already
 * has a listener on the selectedProperty for switching the view and since nodes are cached the transition is faster
 * than loading the fxml again.
 * <p></p>
 * Every toggle button is part of a ToggleGroup which is modified with
 * the {@code ToggleButtonsUtil} class  to add 'always one selected' support
 */
public class MFXHLoader extends HBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-hloader";
    private Pane contentPane;
    private final ToggleGroup toggleGroup;

    private final BooleanProperty isAnimated = new SimpleBooleanProperty(false);
    private final DoubleProperty animationMillis = new SimpleDoubleProperty(800);
    private MFXAnimationFactory animationType = MFXAnimationFactory.FADE_IN;

    private final ObservableMap<String, MFXLoadItem> bindMap;
    private final ThreadPoolExecutor executor;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXHLoader() {
        this(null);
    }

    public MFXHLoader(Pane contentPane) {
        initialize();
        this.contentPane = contentPane;
        this.setPrefSize(Region.USE_COMPUTED_SIZE, 60);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setSpacing(20);
        this.setAlignment(Pos.CENTER);

        this.toggleGroup = new ToggleGroup();

        this.bindMap = FXCollections.observableHashMap();
        this.executor = new ThreadPoolExecutor(
                2,
                4,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                runnable -> {
                    Thread t = Executors.defaultThreadFactory().newThread(runnable);
                    t.setName("MFXHLoaderThread");
                    t.setDaemon(true);
                    return t;
                }
        );
        this.executor.allowCoreThreadTimeOut(true);

        this.bindMap.addListener((MapChangeListener<String, MFXLoadItem>) change -> {
            if (change.wasAdded()) {
                MFXLoadItem item = change.getValueAdded();
                load(item);
            }
        });
    }

    //================================================================================
    // Methods
    //================================================================================
    private void initialize() {
        getStyleClass().add(STYLE_CLASS);
    }

    /**
     * After calling 'addItem' a new task is created and submitted to the executor
     * to load the given {@code MFXItem}.
     *
     * @param item The given item
     */
    private void load(MFXLoadItem item) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    FXMLLoader loader = new FXMLLoader(item.getFxmlURL());
                    if (item.getControllerFactory() != null) {
                        loader.setControllerFactory(item.getControllerFactory());
                    }
                    Node root = loader.load();
                    item.setRoot(root);

                    item.getButton().selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if (isAnimated.get()) {
                            animationType.build(item.getRoot(), animationMillis.doubleValue()).play();
                        }
                        if (newValue) {
                            try {
                                contentPane.getChildren().set(0, item.getRoot());
                            } catch (IndexOutOfBoundsException ex) {
                                contentPane.getChildren().add(0, item.getRoot());
                            }
                        }
                    });
                    Platform.runLater(() -> MFXHLoader.this.getChildren().set(item.getIndex(), item.getButton()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
        this.executor.submit(task);
    }

    /**
     * Checks if the given fxml file is valid,
     * then adds a new {@code MFXItem} to the map with a default key.
     *
     * @param index    The position of the button in the {@code HBox}
     * @param button   The given button
     * @param fxmlFile The given fxml file
     */
    public void addItem(int index, ToggleButton button, URL fxmlFile) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        addItem(index, LoaderUtils.generateKey(fxmlFile), button, fxmlFile);
    }

    /**
     * Checks if the given fxml file is valid,
     * then adds a new {@code MFXItem} to the map with the given key.
     *
     * @param index    The position of the button in the {@code HBox}
     * @param key      The given key
     * @param button   The given button
     * @param fxmlFile The given fxml file
     */
    public void addItem(int index, String key, ToggleButton button, URL fxmlFile) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        this.getChildren().add(button);
        button.setToggleGroup(toggleGroup);
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
        this.bindMap.putIfAbsent(key, new MFXLoadItem(index, button, fxmlFile));
    }

    /**
     * Checks if the given fxml file is valid,
     * then adds a new {@code MFXItem} to the map with a default key.
     *
     * @param index             The position of the button in the {@code HBox}
     * @param button            The given button
     * @param fxmlFile          The given fxml file
     * @param controllerFactory The given controller factory
     */
    public void addItem(int index, ToggleButton button, URL fxmlFile, Callback<Class<?>, Object> controllerFactory) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        addItem(index, LoaderUtils.generateKey(fxmlFile), button, fxmlFile, controllerFactory);
    }

    /**
     * Checks if the given fxml file is valid,
     * then adds a new {@code MFXItem} to the map with the given key.
     *
     * @param index             The position of the button in the {@code HBox}
     * @param key               The given key
     * @param button            The given button
     * @param fxmlFile          The given fxml file
     * @param controllerFactory The given controller factory
     */
    public void addItem(int index, String key, ToggleButton button, URL fxmlFile, Callback<Class<?>, Object> controllerFactory) {
        LoaderUtils.checkFxmlFile(fxmlFile);
        this.getChildren().add(button);
        button.setToggleGroup(toggleGroup);
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
        this.bindMap.putIfAbsent(key, new MFXLoadItem(index, button, fxmlFile, controllerFactory));
    }

    /**
     * Sets the pane in which switching views.
     * This method MUST be called before loading any item.
     */
    public void setContentPane(Pane contentPane) {
        this.contentPane = contentPane;
    }

    /**
     * Sets the default view to set on show.
     * This method should be called after adding any item.
     *
     * @param key The key of the wanted item
     */
    public void setDefault(String key) {
        MFXLoadItem item = getLoadItem(key);
        Task<Void> nullCheckTask = new Task<>() {
            @Override
            protected Void call() {
                if (item.getRoot() == null) {
                    this.runAndReset();
                } else {
                    Platform.runLater(() -> item.getButton().setSelected(true));
                }
                return null;
            }
        };
        this.executor.submit(nullCheckTask);
    }

    public MFXLoadItem getLoadItem(String key) {
        return this.bindMap.get(key);
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
