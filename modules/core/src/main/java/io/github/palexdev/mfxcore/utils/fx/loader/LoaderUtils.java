/*
 * Copyright (C) 2026 Parisi Alessandro - alessandro.parisi406@gmail.com
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX)
 *
 * MaterialFX is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.mfxcore.utils.fx.loader;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

/// Utils class to load FXML views.
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

    public static Future<Parent> submit(Callable<Parent> task) {
        return executor.submit(task);
    }

    /// Creates a new FXMLLoader with location [MFXLoaderBean#getFxmlFile()] and
    /// controller [MFXLoaderBean#getControllerFactory()] (if not null) and loads the fxml file.
    ///
    /// @return the loaded object hierarchy from the fxml
    /// @see #fxmlLoad(FXMLLoader, URL)
    /// @see #fxmlLoad(FXMLLoader, URL, Callback)
    public static Parent fxmlLoad(MFXLoaderBean loaderBean) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        if (loaderBean.getControllerFactory() != null) {
            return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile(), loaderBean.getControllerFactory());
        }
        return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile());
    }

    /// Sets the location and the controller factory (if not null) for the given [FXMLLoader] with
    /// [MFXLoaderBean#getFxmlFile()] and [MFXLoaderBean#getControllerFactory()], and loads the fxml file.
    ///
    /// This method is useful, for example, when using a DI framework with JavaFX.
    ///
    /// @param fxmlLoader the [FXMLLoader] instance to use
    /// @return the loaded object hierarchy from the fxml
    /// @see #fxmlLoad(FXMLLoader, URL)
    /// @see #fxmlLoad(FXMLLoader, URL, Callback)
    public static Parent fxmlLoad(FXMLLoader fxmlLoader, MFXLoaderBean loaderBean) throws IOException {
        if (loaderBean.getControllerFactory() != null) {
            return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile(), loaderBean.getControllerFactory());
        }
        return fxmlLoad(fxmlLoader, loaderBean.getFxmlFile());
    }

    /// Sets the location for the given [FXMLLoader] and loads the fxml file.
    ///
    /// @param fxmlURL the fxml file to load
    /// @return the loaded object hierarchy from the fxml
    private static Parent fxmlLoad(FXMLLoader fxmlLoader, URL fxmlURL) throws IOException {
        fxmlLoader.setLocation(fxmlURL);
        return fxmlLoader.load();
    }

    /// Sets the location and the controller factory for the given [FXMLLoader] and loads the fxml file.
    ///
    /// @param fxmlURL the fxml file to load
    /// @param controllerFactory the controller object to set
    /// @return the loaded object hierarchy from the fxml
    private static Parent fxmlLoad(FXMLLoader fxmlLoader, URL fxmlURL, Callback<Class<?>, Object> controllerFactory) throws IOException {
        fxmlLoader.setLocation(fxmlURL);
        fxmlLoader.setControllerFactory(controllerFactory);
        return fxmlLoader.load();
    }

    /// Check if the given [URL] is a fxml file.
    public static void checkFxmlFile(URL fxmlFile) {
        if (!fxmlFile.toString().endsWith(".fxml")) {
            throw new IllegalArgumentException("The URL is invalid, doesn't end with '.fxml'!");
        }
    }

    /// If no key is specified when calling 'addItem', then a default key is generated,
    /// corresponds to the fxml file name without the extension.
    ///
    /// @param fxmlFile The given fxml file
    /// @return The generated key
    public static String generateKey(URL fxmlFile) {
        String url = fxmlFile.toString();
        int lastSlash = url.lastIndexOf("/");
        int lastDot = url.lastIndexOf(".");
        return url.substring(lastSlash + 1, lastDot);
    }
}
