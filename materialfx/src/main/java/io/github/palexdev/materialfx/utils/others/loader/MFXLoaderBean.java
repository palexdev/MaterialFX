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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

import java.net.URL;
import java.util.function.Supplier;

/**
 * Support bean for {@link MFXLoader} to define the properties of a view such as:
 * <p> - An identifier for the view
 * <p> - The FXML file of the view
 * <p> - The root node of the FXML (managed by the loader, set once the view is loaded)
 * <p> - The controller factory in case the controller uses parameterized constructors (optional)
 * <p> - A flag to indicate whether this view should be considered the default one (useful for nav-bars/dashboards)
 * <p> - A flag to indicate whether the view has been loaded (managed by the loader, set to true once the view is loaded)
 * <p> - A {@link Supplier} function to convert the bean to a {@code Node}. This is useful for example in case you want
 * to implement a view switcher, you could produce a {@code Button} or any node you want that will handle the view switching,
 * for a concrete example you could see the MaterialFX's DemoController (in the demo module).
 * <p></p>
 * The bean also offers a {@link Builder} with fluent api, it's suggested to use that instead of constructors.
 * <p>
 * You can also access the builder with the provided static method {@link #of(String, URL)}.
 */
public class MFXLoaderBean {
	//================================================================================
	// Properties
	//================================================================================
	private final String viewName;
	private final URL fxmlFile;
	private Parent root;
	private Callback<Class<?>, Object> controllerFactory;
	private boolean defaultView = false;
	private boolean loaded = false;
	private Supplier<Node> beanToNodeMapper;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXLoaderBean(String viewName, URL fxmlFile) {
		this.viewName = viewName;
		this.fxmlFile = fxmlFile;
	}

	public MFXLoaderBean(String viewName, URL fxmlFile, Callback<Class<?>, Object> controllerFactory, boolean defaultView, Supplier<Node> beanToNodeMapper) {
		this.viewName = viewName;
		this.fxmlFile = fxmlFile;
		this.controllerFactory = controllerFactory;
		this.defaultView = defaultView;
		this.beanToNodeMapper = beanToNodeMapper;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static Builder of(String viewName, URL fxmlFile) {
		return new Builder(viewName, fxmlFile);
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return the view's identifier
	 */
	public String getViewName() {
		return viewName;
	}

	/**
	 * @return the view's FXML file
	 */
	public URL getFxmlFile() {
		return fxmlFile;
	}

	/**
	 * @return the FXML file's root node
	 */
	public Parent getRoot() {
		return root;
	}

	/**
	 * Sets the view's root node.
	 * <p>
	 * Package private, handled by the loader.
	 */
	MFXLoaderBean setRoot(Parent root) {
		this.root = root;
		return this;
	}

	/**
	 * @return the callback used to produce the view's controller
	 */
	public Callback<Class<?>, Object> getControllerFactory() {
		return controllerFactory;
	}

	/**
	 * Sets the callback used to produce the view's controller.
	 */
	public MFXLoaderBean setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
		this.controllerFactory = controllerFactory;
		return this;
	}

	/**
	 * @return whether this view should be considered the default view
	 */
	public boolean isDefaultView() {
		return defaultView;
	}

	/**
	 * Sets whether this view should be considered the default view.
	 */
	public MFXLoaderBean setDefaultView(boolean defaultView) {
		this.defaultView = defaultView;
		return this;
	}

	/**
	 * @return whether the view has been loaded by the loader
	 */
	public boolean isLoaded() {
		return loaded;
	}

	/**
	 * Sets whether the view has been loaded by the loader.
	 * <p>
	 * Package private, handled by the loader.
	 */
	MFXLoaderBean setLoaded(boolean loaded) {
		this.loaded = loaded;
		return this;
	}

	/**
	 * @return the supplier used to convert this view into a {@code Node}
	 */
	public Supplier<Node> getBeanToNodeMapper() {
		return beanToNodeMapper;
	}

	/**
	 * Sets the supplier used to convert this view into a {@code Node}.
	 */
	public MFXLoaderBean setBeanToNodeMapper(Supplier<Node> beanToNodeMapper) {
		this.beanToNodeMapper = beanToNodeMapper;
		return this;
	}

	//================================================================================
	// Builder
	//================================================================================
	public static class Builder {
		private final MFXLoaderBean bean;

		public Builder(String viewName, URL fxmlFile) {
			this.bean = new MFXLoaderBean(viewName, fxmlFile);
		}

		public Builder setControllerFactory(Callback<Class<?>, Object> controllerFactory) {
			bean.setControllerFactory(controllerFactory);
			return this;
		}

		public Builder setDefaultRoot(boolean defaultRoot) {
			bean.setDefaultView(defaultRoot);
			return this;
		}

		public Builder setBeanToNodeMapper(Supplier<Node> beanToNodeMapper) {
			bean.setBeanToNodeMapper(beanToNodeMapper);
			return this;
		}

		public MFXLoaderBean get() {
			return bean;
		}
	}
}
