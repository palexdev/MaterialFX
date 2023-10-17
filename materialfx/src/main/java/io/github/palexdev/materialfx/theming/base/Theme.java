/*
 * Copyright (C) 2023 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.materialfx.theming.base;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.theming.Deployer;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Public API for all MaterialFX themes/stylesheets. Ideally every theme should have: a name that identifies it,
 * and the path at which it is located.
 * <p>
 * Very important is the load mechanism. This interface assumes that stylesheets reside in the MaterialFX module, and
 * thus uses {@link MFXResourcesLoader} to load them. Custom user themes must override {@link #get()}!
 * <p></p>
 * Until JavaFX adds support for Themes and multiple user agent stylesheets, this in combination with {@link UserAgentBuilder},
 * offers a workaround for it. I noticed JavaFX themes were correctly merged but were still missing something: assets.
 * Their themes use images that unfortunately cannot be retrieved after the merge unless...we deploy them.
 * <p>
 * I was thinking about a possible solution and the only idea I came up with was to copy the necessary assets on the disk,
 * and then during post-processing correct the relative paths to point to the resources on the disk.
 * <p>
 * So, for now, the API has been extended to allow themes to deploy any kind of resources they need. The assets should
 * all be contained in a zip file as then the {@link Deployer} class will extract its contents when {@link #deploy()} is
 * invoked. All the deployment methods have been made {@code default}, in other words optional. The {@link #assets()} method
 * by default returns null, this indicates to the {@link Deployer} that there's nothing to do.
 *
 * @see Deployer
 */
public interface Theme {

	/**
	 * @return the theme's name
	 */
	String name();

	/**
	 * @return the path at which the stylesheet is located as a string
	 */
	String path();

	/**
	 * Responsible for loading the stylesheet specified by {@link #path()}.
	 * <p>
	 * This assumes that the theme comes from the MFXResources module and thus uses {@link MFXResourcesLoader} to load it.
	 * Custom user themes must override this method!.
	 * <p></p>
	 * Last but not least, themes loaded through this are automatically cached for faster subsequent loadings.
	 */
	default URL get() {
		if (Helper.isCached(this) && Helper.getCachedTheme(this) != null)
			return Helper.getCachedTheme(this);
		return Helper.cacheTheme(this, MFXResourcesLoader.loadURL(path()));
	}

	/**
	 * @return the {@link URL} loaded through {@link #get()} as a string, see {@link URL#toExternalForm()}
	 */
	default String toData() {
		return get().toExternalForm();
	}

	/**
	 * Applies the theme as the global user agent stylesheet, see {@link Application#setUserAgentStylesheet(String)}.
	 */
	default void applyGlobal() {
		Application.setUserAgentStylesheet(toData());
	}

	/**
	 * Adds the loaded theme ({@link #toData()}) to the given {@link Scene}.
	 */
	default void applyOn(Scene scene) {
		scene.getStylesheets().add(toData());
	}

	/**
	 * Adds the loaded theme ({@link #toData()}) to the given {@link Parent}.
	 */
	default void applyOn(Parent parent) {
		parent.getStylesheets().add(toData());
	}

	/**
	 * @return the stream to the theme's assets, these are expected to be contained in a zip file
	 */
	default InputStream assets() {
		return null;
	}

	/**
	 * Asks the {@link Deployer} to deploy this theme's resources.
	 *
	 * @see Deployer#deploy(Theme)
	 */
	default void deploy() {
		try {
			Deployer.instance().deploy(this);
		} catch (Exception ex) {
			System.err.println("Failed to deploy theme: " + name() + ", because: " + ex.getMessage());
		}
	}

	/**
	 * This is used by the {@link Deployer} to identify the theme in its cache map, and it is also the parent folder
	 * in which assets will be extracted on the disk.
	 */
	default String deployName() {
		return name().toLowerCase();
	}

	/**
	 * Removes any deployed files from the disk and memory.
	 *
	 * @see Deployer#clean(Theme)
	 */
	default void clean() {
		Deployer.instance().clean(this);
	}

	/**
	 * @return whether the theme has been already deployed before by the {@link Deployer}. Beware, this is just a check
	 * to see if the deployment is in the cache map. No checks are done on the file system as it would be too costly.
	 * And this is true for {@link #deploy()} too, files will be extracted not matter if they are/are not on the disk
	 */
	default boolean isDeployed() {
		return Deployer.instance().getDeployed(this) != null;
	}

	class Helper {
		private static final Map<Theme, URL> CACHE = new HashMap<>();

		public static boolean isCached(Theme theme) {
			return CACHE.containsKey(theme);
		}

		public static URL cacheTheme(Theme theme, URL url) {
			CACHE.put(theme, url);
			return url;
		}

		public static URL getCachedTheme(Theme theme) {
			return CACHE.get(theme);
		}
	}
}
