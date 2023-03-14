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

package io.github.palexdev.mfxcomponents.controls.fab;

import io.github.palexdev.mfxcomponents.theming.base.WithVariants;
import io.github.palexdev.mfxcomponents.theming.enums.FABVariants;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.SceneBuilderIntegration;
import io.github.palexdev.mfxresources.MFXResources;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;

import java.util.List;

/**
 * Extension of {@link MFXFabBase}. This variant only allows icons to be showed, text will always be
 * set to empty.
 * <p></p>
 * Implements the {@link WithVariants} API, since these type of FABs have slightly different versions, the
 * variants are described by {@link FABVariants}.
 */
public class MFXFab extends MFXFabBase implements WithVariants<MFXFab, FABVariants> {

	//================================================================================
	// Constructors
	//================================================================================
	public MFXFab() {
		this("");
	}

	public MFXFab(String text) {
		this(text, null);
	}

	public MFXFab(MFXFontIcon icon) {
		this("", icon);
	}

	public MFXFab(String text, MFXFontIcon icon) {
		super(text, icon);
	}

	/**
	 * Creates a new {@code MFXFab}, small variant.
	 */
	public static MFXFab small() {
		return new MFXFab().setVariants(FABVariants.SMALL);
	}

	/**
	 * Creates a new {@code MFXFab}, large variant.
	 */
	public static MFXFab large() {
		return new MFXFab().setVariants(FABVariants.LARGE);
	}

	/**
	 * Creates a new {@code MFXFab}, surface color scheme variant.
	 */
	public static MFXFab surface() {
		return new MFXFab().setVariants(FABVariants.SURFACE);
	}

	/**
	 * Creates a new {@code MFXFab}, secondary color scheme variant.
	 */
	public static MFXFab secondary() {
		return new MFXFab().setVariants(FABVariants.SECONDARY);
	}

	/**
	 * Creates a new {@code MFXFab}, tertiary color scheme variant.
	 */
	public static MFXFab tertiary() {
		return new MFXFab().setVariants(FABVariants.TERTIARY);
	}

	/**
	 * Creates a new {@code MFXFab} with less shadow emphasis.
	 */
	public static MFXFab lowered() {
		return new MFXFab().setVariants(FABVariants.LOWERED);
	}

	/**
	 * Creates a new {@link MFXFab} which is extended (shows text).
	 */
	public static MFXFab extended() {
		MFXFab fab = new MFXFab();
		fab.setExtended(true);
		return fab;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public List<String> defaultStyleClasses() {
		return List.of("mfx-button", "fab-base", "fab");
	}

	@Override
	public MFXFab addVariants(FABVariants... variants) {
		WithVariants.addVariants(this, variants);
		applyInitSizes(true);
		return this;
	}

	@Override
	public MFXFab setVariants(FABVariants... variants) {
		WithVariants.setVariants(this, variants);
		applyInitSizes(true);
		return this;
	}

	@Override
	protected void sceneBuilderIntegration() {
		SceneBuilderIntegration.ifInSceneBuilder(() -> {
			String theme = MFXResources.load("sass/md3/mfx-light.css");
			When.onChanged(sceneProperty())
					.condition((o, n) -> n != null && !n.getStylesheets().contains(theme))
					.then((o, n) -> n.getStylesheets().add(theme))
					.oneShot()
					.listen();
		});
		// TODO theme integration with SceneBuilder will change once base themes and MFXThemeManager are implemented
	}
}
