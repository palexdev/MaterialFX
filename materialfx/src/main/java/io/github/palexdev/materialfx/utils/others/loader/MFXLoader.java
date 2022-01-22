/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.utils.others.loader;

import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.enums.LoaderCacheLevel;
import io.github.palexdev.materialfx.utils.LoaderUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Convenience class for creating dashboards, no more hassle on managing multiple views.
 * <p>
 * This control makes use of {@link LoaderUtils} and it's capable of loading multiple FXML views with {@link ExecutorService}s.
 * <p>
 * The informations about a view are stored in bean classes, {@link MFXLoaderBean}. Those can be added to this loader
 * by using the various "addView(...)" methods, and will be stored in a {@code Map}, every bean is associated to a {@code String},
 * which should be the identifier of the view, you can also use {@link LoaderUtils#generateKey(URL)} to automatically generate a key.
 * <p></p>
 * Once every view has been added you can start the loader with either {@link #start()} or {@link #startWith(ExecutorService)}.
 * <p>
 * After all views have been loaded the {@link #onLoaded(List)} method is called, see also {@link #setOnLoadedAction(Consumer)}.
 * <p></p>
 * This loader has two other notable features:
 * <p> 1) To load FXML files it uses a {@link FXMLLoader} of course. But, in some cases (like DI framework) you want to use
 * a specific {@link FXMLLoader}, so the loader creates them with a {@link Supplier}, see {@link #fxmlLoaderSupplierProperty()}.
 * The default supplier is just "FXMLLoader::new", but you can easily change that if you need to.
 * <p> 2) To make the views ready for switching the loader can "preload" them (compute both CSS and Layout), you can
 * manage this behavior by setting the "cache level", see {@link #setCacheLevel(LoaderCacheLevel)} and {@link LoaderCacheLevel}
 * <p>
 * <b>NOTE: the cache level must be set before invoking the {@link #start()} method.</b>
 * <p>
 * By default it is set to: {@link LoaderCacheLevel#SCENE_CACHE}
 *
 * @see LoaderUtils
 * @see MFXLoaderBean
 */
public class MFXLoader {
	//================================================================================
	// Properties
	//================================================================================
	private final Map<String, MFXLoaderBean> viewMap = new LinkedHashMap<>();
	private final SupplierProperty<FXMLLoader> fxmlLoaderSupplier = new SupplierProperty<>(FXMLLoader::new) {
		@Override
		public void set(Supplier<FXMLLoader> newValue) {
			if (newValue == null) {
				super.set(FXMLLoader::new);
				return;
			}
			super.set(newValue);
		}
	};
	private final AtomicInteger loadedCount = new AtomicInteger(0);
	private Consumer<List<MFXLoaderBean>> onLoadedAction;
	private LoaderCacheLevel cacheLevel = LoaderCacheLevel.SCENE_CACHE;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLoader() {
		this(null);
	}

	public MFXLoader(Consumer<List<MFXLoaderBean>> onLoadedAction) {
		this.onLoadedAction = onLoadedAction;
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Starts the loading process by collecting all the views from the map
	 * that are still not loaded. Then for each {@link MFXLoaderBean} builds
	 * the {@link Callable} used to load the FXML root, see {@link #buildTask(MFXLoaderBean)},
	 * and sends it to the {@link LoaderUtils}'s executor, to load the view uses
	 * {@link Future#get()}.
	 * Once the fxml has been loaded invokes {@link #cacheParent(Parent)} then finally
	 * increments the number that keeps track of how many views have been loaded.
	 * <p>
	 * At the end of the loop calls {@link #onLoaded(List)}, the list
	 * is given by {@link Map#values()} (wrapped in an ArrayList).
	 */
	public void start() {
		List<MFXLoaderBean> toLoad = viewMap.values().stream()
				.filter(bean -> !bean.isLoaded())
				.collect(Collectors.toList());
		for (MFXLoaderBean bean : toLoad) {
			try {
				Callable<Parent> task = buildTask(bean);
				Parent loaded = LoaderUtils.submit(task).get();
				cacheParent(loaded);
				bean.setLoaded(true);
				loadedCount.addAndGet(1);
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			} finally {
				loadedCount.addAndGet(1);
			}
		}
		onLoaded(new ArrayList<>(viewMap.values()));
	}

	/**
	 * Same as {@link #start()} but the load tasks are submitted to the given
	 * {@link ExecutorService}.
	 */
	public void startWith(ExecutorService executorService) {
		List<MFXLoaderBean> toLoad = viewMap.values().stream()
				.filter(bean -> !bean.isLoaded())
				.collect(Collectors.toList());
		for (MFXLoaderBean bean : toLoad) {
			try {
				Callable<Parent> task = buildTask(bean);
				Parent loaded = executorService.submit(task).get();
				cacheParent(loaded);
				bean.setLoaded(true);
				loadedCount.addAndGet(1);
			} catch (InterruptedException | ExecutionException ex) {
				ex.printStackTrace();
			} finally {
				loadedCount.addAndGet(1);
			}
		}
		onLoaded(new ArrayList<>(viewMap.values()));
	}

	/**
	 * Adds the given view to the views map.
	 */
	public MFXLoader addView(MFXLoaderBean bean) {
		LoaderUtils.checkFxmlFile(bean.getFxmlFile());
		viewMap.put(bean.getViewName(), bean);
		return this;
	}

	/**
	 * Builds a new {@link MFXLoaderBean} with the given identifier and FXML file,
	 * then adds it to the views map.
	 */
	public MFXLoader addView(String viewName, URL fxmlFile) {
		LoaderUtils.checkFxmlFile(fxmlFile);
		viewMap.put(viewName, new MFXLoaderBean(viewName, fxmlFile));
		return this;
	}

	/**
	 * Builds a new {@link MFXLoaderBean} with the given identifier, FXML file and
	 * controller factory, then adds it to the views map.
	 */
	public MFXLoader addView(String viewName, URL fxmlFile, Callback<Class<?>, Object> controllerFactory) {
		LoaderUtils.checkFxmlFile(fxmlFile);
		viewMap.put(viewName, new MFXLoaderBean(viewName, fxmlFile, controllerFactory, false, null));
		return this;
	}

	/**
	 * @return a view for the given identifier, or null if no view is found
	 */
	public MFXLoaderBean getView(String viewName) {
		return viewMap.getOrDefault(viewName, null);
	}

	/**
	 * This method is called once all the views have been loaded by {@link #start()} or
	 * {@link #startWith(ExecutorService)}.
	 * <p>
	 * This simple methods is just responsible for executing the action specified by the user, {@link #setOnLoadedAction(Consumer)},
	 * if not null.
	 */
	protected void onLoaded(List<MFXLoaderBean> beans) {
		if (onLoadedAction != null) {
			onLoadedAction.accept(beans);
		}
	}

	/**
	 * Responsible for building the {@link Callable} which will load the given view.
	 * <p>
	 * First a {@link FXMLLoader} is created by using the specified supplier, {@link #fxmlLoaderSupplierProperty()},
	 * then the location and controller factory are set on it.
	 * <p>
	 * {@link FXMLLoader#load()} is invoked and then the loaded {@link Parent} is set in the bean.
	 */
	private Callable<Parent> buildTask(MFXLoaderBean bean) {
		List<Callable<Parent>> tasks = new ArrayList<>();
		return () -> {
			FXMLLoader loader = getFxmlLoaderSupplier().get();
			URL fxmlFile = bean.getFxmlFile();
			Callback<Class<?>, Object> controllerFactory = bean.getControllerFactory();
			loader.setLocation(fxmlFile);
			loader.setControllerFactory(controllerFactory);

			Parent root = loader.load();
			bean.setRoot(root);
			return root;
		};
	}

	/**
	 * This method is responsible for caching/preloading the loaded views to make them
	 * ready for switching.
	 * For a description of the various cache levels, see {@link LoaderCacheLevel}.
	 * <p>
	 * If the cache level is set to {@link LoaderCacheLevel#NONE} exits immediately.
	 */
	private void cacheParent(Parent parent) {
		if (cacheLevel == LoaderCacheLevel.NONE) return;

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

	//================================================================================
	// Getters/Setters
	//================================================================================
	public Supplier<FXMLLoader> getFxmlLoaderSupplier() {
		return fxmlLoaderSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to build a new {@link FXMLLoader} each time a view has to be loaded.
	 */
	public SupplierProperty<FXMLLoader> fxmlLoaderSupplierProperty() {
		return fxmlLoaderSupplier;
	}

	public MFXLoader setFxmlLoaderSupplier(Supplier<FXMLLoader> fxmlLoaderSupplier) {
		this.fxmlLoaderSupplier.set(fxmlLoaderSupplier);
		return this;
	}

	public Consumer<List<MFXLoaderBean>> getOnLoadedAction() {
		return onLoadedAction;
	}

	/**
	 * Sets the action to perform once all the views have been loaded.
	 * <p>
	 * The action is a {@link Consumer} which carries the list fo loaded views.
	 */
	public MFXLoader setOnLoadedAction(Consumer<List<MFXLoaderBean>> onLoadedAction) {
		this.onLoadedAction = onLoadedAction;
		return this;
	}

	public LoaderCacheLevel getCacheLevel() {
		return cacheLevel;
	}

	/**
	 * Sets the {@link LoaderCacheLevel} for this loader.
	 */
	public MFXLoader setCacheLevel(LoaderCacheLevel cacheLevel) {
		this.cacheLevel = cacheLevel;
		return this;
	}
}
