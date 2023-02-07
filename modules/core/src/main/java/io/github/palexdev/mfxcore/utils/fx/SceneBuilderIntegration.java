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

package io.github.palexdev.mfxcore.utils.fx;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.Set;

/**
 * This utility can be used by libraries that implement custom controls to allow them to detect whether they
 * are being loaded by the SceneBuilder app. This allows to perform whatever action the user desires in such case.
 * <p>
 * For example MaterialFX uses this to set the buttons text to "Button" only if they are used in SceneBuilder, the caveat
 * of such trick is that for some reason properties in the right pane are not updated, the Text property will appear blank,
 * if you want to set a empty text, just use a whitespace character.
 * <p></p>
 * MaterialFX also does another interesting thing with this, since SceneBuilder doesn't offer any way to add custom themes,
 * this utility helps with that by detecting the app and adding the stylesheet to the root Scene.
 * <p></p>
 * The utility is optimized to not run the detection more than one time, the result is cached after the first time.
 * The 'search' can become invalid and thus repeated if the user changes the 'search' depth. What is the depth?
 * This uses the {@link StackWalker} API to detect the caller of constructors/methods, the depth is the estimate number
 * of {@link StackFrame}s to process.
 */
public class SceneBuilderIntegration {
	//================================================================================
	// Static Properties
	//================================================================================
	private static Boolean inSceneBuilder = null;
	private static int depth = 10;
	private static boolean isDepthInvalid = false;

	//================================================================================
	// Constructors
	//================================================================================
	private SceneBuilderIntegration() {
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * If {@link #isInSceneBuilder()} returns true executes the given action.
	 */
	public static void ifInSceneBuilder(Runnable action) {
		if (isInSceneBuilder()) action.run();
	}

	/**
	 * This is the core method responsible for detecting the caller of a constructor/method.
	 * The {@link StackWalker} API used to do so, will detect all the classes involved in the calling.
	 * SceneBuilder can be detected because classes loading custom controls are in a package containing the
	 * following string: ".javafx.scenebuilder.kit."
	 * <p></p>
	 * Subsequent calls to this will return the cached result (if it was called at least once of course), so there
	 * should be little to no performance impact in the app. If the search depth is changed the cached result is ignored
	 * and the research is repeated.
	 */
	public static boolean isInSceneBuilder() {
		if (inSceneBuilder == null || isDepthInvalid) {
			StackWalker sw = StackWalker.getInstance(
					Set.of(Option.RETAIN_CLASS_REFERENCE, Option.SHOW_REFLECT_FRAMES),
					depth
			);
			inSceneBuilder = sw.walk(sfs -> sfs.anyMatch(sf ->
					sf.getClassName() != null && sf.getClassName().contains(".javafx.scenebuilder.kit.")));
			isDepthInvalid = false;
		}
		return inSceneBuilder;
	}

	/**
	 * Sets the 'search' depth used by the {@link StackWalker} API.
	 */
	public static void setDepth(int depth) {
		SceneBuilderIntegration.depth = depth;
		SceneBuilderIntegration.isDepthInvalid = true;
	}
}
