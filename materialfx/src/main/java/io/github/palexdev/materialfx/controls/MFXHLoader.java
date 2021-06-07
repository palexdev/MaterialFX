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
import io.github.palexdev.materialfx.controls.enums.LoaderCacheLevel;
import io.github.palexdev.materialfx.controls.factories.MFXAnimationFactory;
import io.github.palexdev.materialfx.utils.LoaderUtils;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Convenience class for creating dashboards, no more hassle on managing multiple views.
 * <p>
 * This control extends {@code HBox} and relies on {@link LoaderUtils} for loading fxml files in the background
 * leaving the UI responsive.
 * <p></p>
 * Once everything is set up and the fxml files have been added with the various {@code addItem} methods
 * to start loading the views invoke the {@link #start()} method. That method then will get all the
 * {@code MFXLoaderBeans} in the views map and for each of them checks if the root has not been loaded yet,
 * creates the load callable by calling {@link #buildLoadCallable(MFXLoaderBean)} and submits it to the executor in
 * {@link LoaderUtils}.
 * <p></p>
 * Once everything is loaded: {@link ToggleButtonsUtil#addAlwaysOneSelectedSupport(ToggleGroup)} is called, the loaded
 * toggles are added to the children list and {@link #setDefault()} is called.
 *
 * <b>NOTE: the cache level must be set before invoking the {@link #start()} method.</b>
 * <p>
 * By default it is set to: {@link LoaderCacheLevel#SCENE_CACHE}
 *
 * @see LoaderUtils
 * @see MFXLoaderBean
 * @see ToggleButtonsUtil
 */
public class MFXHLoader extends HBox {
    //================================================================================
    // Properties
    //================================================================================
    private final String STYLE_CLASS = "mfx-hloader";
    private Pane contentPane;
    private final ToggleGroup toggleGroup;

    private final BooleanProperty animated = new SimpleBooleanProperty(false);
    private final DoubleProperty animationMillis = new SimpleDoubleProperty(800);
    private MFXAnimationFactory animationType = MFXAnimationFactory.FADE_IN;

    private final IntegerProperty loadedItems = new SimpleIntegerProperty(0);
    private final Map<String, MFXLoaderBean> idViewMap;
    private Supplier<FXMLLoader> fxmlLoaderSupplier;

    private LoaderCacheLevel cacheLevel = LoaderCacheLevel.SCENE_CACHE;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXHLoader() {
        this(null);
    }

    public MFXHLoader(Pane contentPane) {
        this(contentPane, null);
    }

    public MFXHLoader(Pane contentPane, Supplier<FXMLLoader> fxmlLoaderSupplier) {
        this.setPrefSize(Region.USE_COMPUTED_SIZE, 60);
        this.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        this.setSpacing(20);
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

    /**
     * Adds a listener to loadedItems to check when all the views have been loaded.
     * Then: calls {@link ToggleButtonsUtil#addAlwaysOneSelectedSupport(ToggleGroup)},
     * adds all the toggles to the children list, calls {@link #setDefault()}
     */
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

    /**
     * Gets the built {@code MFXLoaderBean} from the builder,
     * adds the toggle to the loader's toggle group and puts
     * the bean in the {@code idViewMap} with a key generated by
     * {@link LoaderUtils#generateKey(URL)}.
     */
    public void addItem(MFXLoaderBean.Builder builder) {
        MFXLoaderBean loaderBean = builder.get();
        LoaderUtils.checkFxmlFile(loaderBean.getFxmlURL());
        loaderBean.getButton().setToggleGroup(toggleGroup);
        idViewMap.put(LoaderUtils.generateKey(loaderBean.getFxmlURL()), loaderBean);
    }

    /**
     * Gets the built {@code MFXLoaderBean} from the builder,
     * adds the toggle to the loader's toggle group and puts
     * the bean in the {@code idViewMap} with the specified key.
     */
    public void addItem(String key, MFXLoaderBean.Builder builder) {
        MFXLoaderBean loaderBean = builder.get();
        LoaderUtils.checkFxmlFile(loaderBean.getFxmlURL());
        loaderBean.getButton().setToggleGroup(toggleGroup);
        idViewMap.put(key, loaderBean);
    }

    /**
     * Checks if there is a {@code MFXLoaderBean} which has the defaultRoot flag set to true
     * and sets the bean's toggle state to selected so that the view is shown, this is handled on
     * the JavaFX's thread.
     */
    private void setDefault() {
        idViewMap.values().stream()
                .filter(MFXLoaderBean::isDefault)
                .findFirst().ifPresent(loaderBean -> Platform.runLater(() -> loaderBean.getButton().setSelected(true)));
    }

    /**
     * Starts the loading process.
     * <p></p>
     * Retrieves the {@code MFXLoaderBeans} in the idViewMap, for each of them
     * checks if the node has been already loaded, if not then calls {@link #load(MFXLoaderBean)}
     *
     * @see MFXLoaderBean
     */
    public void start() {
        if (contentPane == null) {
            throw new NullPointerException("Content pane has not been set!");
        }

        List<MFXLoaderBean> loaderBeans = new ArrayList<>(idViewMap.values());
        for (MFXLoaderBean loaderBean : loaderBeans) {
            if (loaderBean.getRoot() == null) {
                load(loaderBean);
            }
        }
    }

    /**
     * Loads the root node of a {@link MFXLoaderBean}. The load process depends
     * on the set cache level. If the level is set to {@link LoaderCacheLevel#NONE}
     * the load task built by {@link #buildLoadCallable(MFXLoaderBean)} is submitted
     * to the loader executor {@link LoaderUtils#submit(Callable)}.
     * In the other cases the task is submitted to the executor but the {@link Future#get()} method
     * is invoked which causes the thread to wait until the fxml is loaded.
     *
     * @see #cacheParent(Parent)
     */
    private void load(MFXLoaderBean loaderBean) {
        if (cacheLevel != LoaderCacheLevel.NONE) {
            try {
                Parent loaded = LoaderUtils.submit(buildLoadCallable(loaderBean)).get();
                cacheParent(loaded);
                loadedItems.set(loadedItems.get() + 1);
            } catch (InterruptedException | ExecutionException ex) {
                loadedItems.set(loadedItems.get() + 1);
                ex.printStackTrace();
            }
        } else {
            LoaderUtils.submit(buildLoadCallable(loaderBean));
            loadedItems.set(loadedItems.get() + 1);
        }
    }

    /**
     * Called if the cache level is not set to {@link LoaderCacheLevel#NONE}.
     *
     * @see LoaderCacheLevel
     */
    private void cacheParent(Parent parent) {
        if (cacheLevel == LoaderCacheLevel.SCENE_JAVAFX_CACHE) {
            parent.setCache(true);
            parent.setCacheHint(CacheHint.SPEED);
        }

        StackPane pane = new StackPane();
        pane.getChildren().setAll(parent);
        Scene scene = new Scene(pane);
        pane.applyCss();
        pane.layout();
    }

    /**
     * Builds the callable which loads the fxmlFile with {@link LoaderUtils#fxmlLoad(FXMLLoader, MFXLoaderBean)}
     * or {@link LoaderUtils#fxmlLoad(MFXLoaderBean)} if the FXMLLoader supplier is null.
     * When the file is loaded adds a listener to the toggle's selected property to handle the view switching.
     */
    private Callable<Parent> buildLoadCallable(MFXLoaderBean loaderBean) {
        return () -> {
            Parent root;
            if (fxmlLoaderSupplier != null) {
                root = LoaderUtils.fxmlLoad(fxmlLoaderSupplier.get(), loaderBean);
            } else {
                root = LoaderUtils.fxmlLoad(loaderBean);
            }
            loaderBean.setRoot(root);

            loaderBean.getButton().selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (isAnimated()) {
                    animationType.build(loaderBean.getRoot(), animationMillis.doubleValue()).play();
                }
                if (newValue) {
                    contentPane.getChildren().setAll(loaderBean.getRoot());
                }
            });
            return root;
        };
    }

    /**
     * @return the {@code MFXLoaderBean} to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     */
    public MFXLoaderBean getLoadItem(String key) {
        return this.idViewMap.get(key);
    }

    /**
     * @return the pane on which the views are switched.
     */
    public Pane getContentPane() {
        return contentPane;
    }

    /**
     * Sets the pane on which the views are switched.
     */
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

    public boolean isAnimated() {
        return animated.get();
    }

    /**
     * Specifies if the view switching is animated.
     */
    public BooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getAnimationMillis() {
        return animationMillis.get();
    }

    /**
     * Specified the switch animation duration.
     */
    public DoubleProperty animationMillisProperty() {
        return animationMillis;
    }

    public void setAnimationMillis(double animationMillis) {
        this.animationMillis.set(animationMillis);
    }

    public MFXAnimationFactory getAnimationType() {
        return animationType;
    }

    /**
     * Sets the switch animation type.
     */
    public void setAnimationType(MFXAnimationFactory animationType) {
        this.animationType = animationType;
    }

    public LoaderCacheLevel getCacheLevel() {
        return cacheLevel;
    }

    public void setCacheLevel(LoaderCacheLevel cacheLevel) {
        this.cacheLevel = cacheLevel;
    }
}
