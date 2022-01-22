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

package io.github.palexdev.materialfx.utils;

import io.github.palexdev.materialfx.utils.others.loader.MFXLoaderBean;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;

/**
 * Utils class to load FXML views.
 */
public class LoaderUtils {
	private static final ThreadPoolExecutor executor;

	static {
		executor = new ThreadPoolExecutor(
				2,
				4,
				5,
				TimeUnit.SECONDS,
				new LinkedBlockingDeque<>(),
				runnable -> {
					Thread thread = Executors.defaultThreadFactory().newThread(runnable);
					thread.setName("MFXLoaderThread");
					thread.setDaemon(true);
					return thread;
				}
		);
		executor.allowCoreThreadTimeOut(true);
	}

	private LoaderUtils() {
	}

	/**
	 * Submits a value-returning task for execution and returns a
	 * Future representing the pending results of the task. The
	 * Future's {@code get} method will return the task's result upon
	 * successful completion.
	 *
	 * <p>
	 * If you would like to immediately block waiting
	 * for a task, you can use constructions of the form
	 * {@code result = exec.submit(aCallable).get();}
	 *
	 * <p>Note: The {@link Executors} class includes a set of methods
	 * that can convert some other common closure-like objects,
	 * for example, {@link java.security.PrivilegedAction} to
	 * {@link Callable} form so they can be submitted.
	 *
	 * @param task the task to submit
	 * @return a Future representing pending completion of the task
	 * @throws RejectedExecutionException if the task cannot be
	 *                                    scheduled for execution
	 * @throws NullPointerException       if the task is null
	 */
	public static Future<Parent> submit(Callable<Parent> task) {
		return executor.submit(task);
	}

	/**
	 * Creates a new FXMLLoader with location {@link MFXLoaderBean#getFxmlFile()} and
	 * controller {@link MFXLoaderBean#getControllerFactory()} (if not null) and loads the fxml file.
	 *
	 * @return the loaded object hierarchy from the fxml
	 * @see #fxmlLoad(FXMLLoader, URL)
	 * @see #fxmlLoad(FXMLLoader, URL, Callback)
	 */
	public static Parent fxmlLoad(MFXLoaderBean loaderBean) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		if (loaderBean.getControllerFactory() != null) {
			return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile(), loaderBean.getControllerFactory());
		}
		return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile());
	}

	/**
	 * Sets the location and the controller factory (if not null) for the given
	 * fxmlLoader with {@link MFXLoaderBean#getFxmlFile()} and {@link MFXLoaderBean#getControllerFactory()},
	 * and loads the fxml file.
	 * <p></p>
	 * This method is useful for example when using a DI framework with JavaFX.
	 *
	 * @param fxmlLoader the FXMLLoader instance to use
	 * @return the loaded object hierarchy from the fxml
	 * @see #fxmlLoad(FXMLLoader, URL)
	 * @see #fxmlLoad(FXMLLoader, URL, Callback)
	 */
	public static Parent fxmlLoad(FXMLLoader fxmlLoader, MFXLoaderBean loaderBean) throws IOException {
		if (loaderBean.getControllerFactory() != null) {
			return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile(), loaderBean.getControllerFactory());
		}
		return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile());
	}

	/**
	 * Sets the location for the given fxmlLoader and loads the fxml file.
	 *
	 * @param fxmlURL the fxml file to load
	 * @return the loaded object hierarchy from the fxml
	 */
	private static Parent fxmlLoad(FXMLLoader fxmlLoader, URL fxmlURL) throws IOException {
		fxmlLoader.setLocation(fxmlURL);
		return fxmlLoader.load();
	}

	/**
	 * Sets the location and the controller factory for the given fxmlLoader and loads the fxml file.
	 *
	 * @param fxmlURL           the fxml file to load
	 * @param controllerFactory the controller object to set
	 * @return the loaded object hierarchy from the fxml
	 */
	private static Parent fxmlLoad(FXMLLoader fxmlLoader, URL fxmlURL, Callback<Class<?>, Object> controllerFactory) throws IOException {
		fxmlLoader.setLocation(fxmlURL);
		fxmlLoader.setControllerFactory(controllerFactory);
		return fxmlLoader.load();
	}

	/**
	 * Check if the given URL is an fxml file.
	 */
	public static void checkFxmlFile(URL fxmlFile) {
		if (!fxmlFile.toString().endsWith(".fxml")) {
			throw new IllegalArgumentException("The URL is invalid, doesn't end with '.fxml'!");
		}
	}

	/**
	 * If no key is specified when calling 'addItem' then a default key is generated,
	 * corresponds to the fxml file name without the extension.
	 *
	 * @param fxmlFile The given fxml file
	 * @return The generated key
	 */
	public static String generateKey(URL fxmlFile) {
		String url = fxmlFile.toString();
		int lastSlash = url.lastIndexOf("/");
		int lastDot = url.lastIndexOf(".");
		return url.substring(lastSlash + 1, lastDot);
	}
}
