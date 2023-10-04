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

package io.github.palexdev.mfxcore.controls;

import io.github.palexdev.mfxcore.base.properties.functional.SupplierProperty;
import io.github.palexdev.mfxcore.behavior.BehaviorBase;
import io.github.palexdev.mfxcore.behavior.WithBehavior;
import javafx.scene.Node;
import javafx.scene.control.Skin;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base class that can be used as a starting point to implement UI components that perfectly integrate with the new Behavior
 * API, see {@link BehaviorBase}.
 * <p>
 * Extends {@link javafx.scene.control.Control} and implements, {@link WithBehavior}.
 * <p>
 * The integration with the new Behavior API is achieved by having a specific property, {@link #behaviorProviderProperty()},
 * which allows to change at any time the component's behavior. The property automatically handles initialization and disposal
 * of behaviors. A reference to the current built behavior object is kept to be retrieved via {@link #getBehavior()}.
 * <p></p>
 * Enforces the use of {@link SkinBase} instances as Skin implementations and makes the {@link #createDefaultSkin()}
 * final thus denying users to override it. To set custom skins, you should override the new provided method {@link #buildSkin()}.
 * <p>
 * I wanted to avoid adding a listener of the skin property for memory and performance reasons. Every time a skin is created,
 * it's needed to pass the current built behavior to the skin for initialization. A good hook place for this call was the
 * {@link #createDefaultSkin()} method, but this would make it harder for users to override it because then you would also
 * have to take into account the behavior initialization. Having a new method maintains the usual simplicity of setting
 * custom skins while avoiding listeners for better performance. For this reason, if you want to change the skin while still
 * making use of the behavior API, then <b>the correct way</b> to do it is to use {@link #changeSkin(SkinBase)} as it will
 * ensure the initialization of the behavior and overall the correct state of the component.
 * <p>
 * As a consequence, components that inherit from this do not support the "-fx-skin" CSS property. You'll have to do it in code.
 * <p>
 * Unfortunately, I cannot prevent users from still using the aforementioned method, but I can guarantee you using that
 * will cause issues and undesired behaviors. You have been warned.
 *
 * @param <B> the behavior type used by the component
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Control<B extends BehaviorBase<? extends Node>> extends javafx.scene.control.Control implements WithBehavior<B> {
	//================================================================================
	// Properties
	//================================================================================
	private B behavior;
	private final SupplierProperty<B> behaviorProvider = new SupplierProperty<>() {
		@Override
		protected void invalidated() {
			if (behavior != null) behavior.dispose();
			behavior = Optional.ofNullable(get()).map(Supplier::get).orElse(null);
			SkinBase skin = ((SkinBase) getSkin());
			if (skin != null && behavior != null) skin.initBehavior(behavior);
		}
	};

	//================================================================================
	// Abstract Methods
	//================================================================================

	/**
	 * Create a new instance of the default skin for this component.
	 */
	protected abstract SkinBase<?, ?> buildSkin();

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Since this is deeply integrated with the new behavior API, and since the {@link #setSkin(Skin)} method cannot
	 * be overridden, and finally to avoid adding listeners, this is the method to use when you want to change the skin.
	 * <p></p>
	 * Unfortunately, I cannot prevent users from still using the aforementioned method, but I can guarantee you using that
	 * will cause issues and undesired behaviors. You have been warned.
	 */
	public void changeSkin(SkinBase<?, ?> skin) {
		if (skin == null)
			throw new IllegalArgumentException("The new skin cannot be null!");
		if (behavior != null) behavior.dispose();
		behavior = getBehaviorProvider().get();
		((SkinBase) skin).initBehavior(behavior);
		setSkin(skin);
	}

	/**
	 * Subclasses can change the actions to perform if the component is being used in SceneBuilder
	 * by overriding this method. Typically called automatically on components' initialization.
	 */
	protected void sceneBuilderIntegration() {}


	//================================================================================
	// Overridden Methods
	//================================================================================

	@Override
	protected final SkinBase<?, ?> createDefaultSkin() {
		SkinBase skin = buildSkin();
		if (behavior != null) skin.initBehavior(behavior);
		return skin;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	@Override
	public B getBehavior() {
		return behavior;
	}

	@Override
	public Supplier<B> getBehaviorProvider() {
		return behaviorProvider.get();
	}

	@Override
	public SupplierProperty<B> behaviorProviderProperty() {
		return behaviorProvider;
	}

	@Override
	public void setBehaviorProvider(Supplier<B> behaviorProvider) {
		this.behaviorProvider.set(behaviorProvider);
	}
}
