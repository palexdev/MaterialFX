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

package io.github.palexdev.mfxcomponents.theming.base;

import io.github.palexdev.mfxcomponents.controls.base.MFXControl;
import io.github.palexdev.mfxcomponents.controls.base.MFXLabeled;
import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple API for components that have variants of themselves.
 * <p></p>
 * A clarification must be made here. Components typically have a number of default style classes, for example
 * if they implement {@link MFXStyleable}, and additionally they can have variants that change their properties/features/appearance.
 * This API can be used when creating separate classes is not feasible or just useless.
 * <p>
 * To make this more comprehensible consider this example:
 * <p>
 * Material Design 3 buttons have five variants: elevated, filled, outlined, text, and tonal filled. Apart from the
 * elevated variant that includes the 'shadow elevation mechanism', the other ones do not add/change any feature/behavior.
 * So technically they are good candidates for this API, however they are <b>different</b> types of buttons, each with its
 * usage/use cases.
 * <p>
 * Now on the other hand, consider FABs. Floating Action Buttons mainly have two variants: small and large.
 * They are the same exact component, what changes is just their sizes, in other words it's not worth defining two
 * new classes to represent these variants, it's enough to just define the variants by changing the 'base' style classes.
 * <p></p>
 * Another way to see is as follows:
 * <p> MFXElevatedButton has '.mfx-button.elevated' as selector, MFXFilledButton has '.mfx-button.filled' (same is true
 * for other buttons), notice how the class defining the button style ('.elevated' and '.filled') are different,
 * the difference between the two buttons is more explicit.
 * <p>
 * MFXFab with standard size has '.mfx-button.fab' as selector, and in its small variant has '.mfx-button.fab.small'
 * as selector, notice how the difference between the two is way less emphasized, they are the same component even in style,
 * just different size.
 * <p></p>
 * The cons in using this mechanism is a less 'comfortable' integration with SceneBuilder. Having separate classes
 * allows SceneBuilder to detect the variant and add it to the Custom Controls section. This way variants can still be used
 * by adding the variant style class in the properties inspector, but the user is required to remember/check which
 * are the applicable classes.
 * <p></p>
 * Since components may have multiple variants that can even be combined, this API offers two different ways to apply the
 * variants:
 * <p> - A 'set' method, which is intended to be implemented so that the component style classes are first reset to its base ones
 * and only then the variants are added
 * <p> - An 'add' method, which is intended to be implemented so that the variants are added to the style classes already
 * set on the component. A recommendation, is to filter the style classes in a {@code LinkedHashSet} to avoid duplicates
 * and unwanted behaviors.
 * <p></p>
 * Uses the {@link Variant} API.
 */
public interface WithVariants<N extends Node, V extends Variant> {

	N addVariants(V... variants);

	N setVariants(V... variants);

	N removeVariants(V... variants);

	/**
	 * Adds all the given variants to the given control.
	 * <p>
	 * Style classes are filtered by a {@link LinkedHashSet} to avoid duplicates while keeping the specified order.
	 */
	@SafeVarargs
	static <C extends MFXControl<?>, V extends Variant> C addVariants(C control, V... variants) {
		Set<String> classes = new LinkedHashSet<>(control.getStyleClass());
		for (V variant : variants) {
			classes.add(variant.variantStyleClass());
		}
		control.getStyleClass().setAll(classes);
		return control;
	}

	/**
	 * Replaces the given control' style classes with its base ones, then adds the specified variants.
	 * <p>
	 * Style classes are filtered by a {@link LinkedHashSet} to avoid duplicates while keeping the specified order.
	 */
	@SafeVarargs
	static <C extends MFXControl<?>, V extends Variant> C setVariants(C control, V... variants) {
		Set<String> classes = new LinkedHashSet<>(control.defaultStyleClasses());
		for (V variant : variants) {
			classes.add(variant.variantStyleClass());
		}
		control.getStyleClass().setAll(classes);
		return control;
	}

	/**
	 * Removes all the given variants from the given control.
	 */
	@SafeVarargs
	static <C extends MFXControl<?>, V extends Variant> C removeVariants(C control, V... variants) {
		ObservableList<String> styleClass = control.getStyleClass();
		for (V variant : variants) {
			styleClass.remove(variant.variantStyleClass());
		}
		return control;
	}

	/**
	 * Adds all the given variants to the given labeled.
	 * <p>
	 * Style classes are filtered by a {@link LinkedHashSet} to avoid duplicates while keeping the specified order.
	 */
	@SafeVarargs
	static <L extends MFXLabeled<?>, V extends Variant> L addVariants(L labeled, V... variants) {
		Set<String> classes = new LinkedHashSet<>(labeled.getStyleClass());
		for (V variant : variants) {
			classes.add(variant.variantStyleClass());
		}
		labeled.getStyleClass().setAll(classes);
		return labeled;
	}

	/**
	 * Replaces the given labeled' style classes with its base ones, then adds the specified variants.
	 * <p>
	 * Style classes are filtered by a {@link LinkedHashSet} to avoid duplicates while keeping the specified order.
	 */
	@SafeVarargs
	static <L extends MFXLabeled<?>, V extends Variant> L setVariants(L labeled, V... variants) {
		Set<String> classes = new LinkedHashSet<>(labeled.defaultStyleClasses());
		for (V variant : variants) {
			classes.add(variant.variantStyleClass());
		}
		labeled.getStyleClass().setAll(classes);
		return labeled;
	}

	/**
	 * Removes all the given variants from the given labeled.
	 */
	@SafeVarargs
	static <L extends MFXLabeled<?>, V extends Variant> L removeVariants(L labeled, V... variants) {
		ObservableList<String> styleClass = labeled.getStyleClass();
		for (V variant : variants) {
			styleClass.remove(variant.variantStyleClass());
		}
		return labeled;
	}
}
