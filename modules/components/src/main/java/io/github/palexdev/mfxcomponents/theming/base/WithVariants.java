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

import io.github.palexdev.mfxcomponents.controls.base.MFXStyleable;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	/**
	 * Adds the given variants to the component.
	 */
	N addVariants(V... variants);

	/**
	 * Clears the component's variants then adds all the provided ones.
	 */
	N setVariants(V... variants);

	/**
	 * Removes all the given variants from the component.
	 */
	N removeVariants(V... variants);

	/**
	 * @return a {@link Set} containing all the applied variants, useful for such queries since it is way faster than
	 * checking the {@link N#getStyleClass()} list
	 */
	Set<V> getAppliedVariants();

	/**
	 * @return whether the given variant is contained in {@link #getAppliedVariants()}
	 */
	default boolean isVariantApplied(V variant) {
		return getAppliedVariants().contains(variant);
	}

	/**
	 * Adds all the given variants to the given node and to the set returned by {@link #getAppliedVariants()}.
	 */
	@SafeVarargs
	static <V extends Variant, N extends Node & WithVariants<N, V>> N addVariants(N node, V... variants) {
		Set<V> appliedVariants = node.getAppliedVariants();
		List<String> classes = new ArrayList<>();
		for (V v : variants) {
			if (appliedVariants.add(v))
				classes.add(v.variantStyleClass());
		}
		node.getStyleClass().addAll(classes);
		return node;
	}

	/**
	 * First removes all currently applied variants, then adds the new given ones with {@link #addVariants(Node, Variant[])}.
	 */
	@SafeVarargs
	static <V extends Variant, N extends Node & WithVariants<N, V>> N setVariants(N node, V... variants) {
		Set<V> appliedVariants = node.getAppliedVariants();
		// Remove all applied previously applied variants
		String[] toRemove = appliedVariants.stream().map(Variant::variantStyleClass).toArray(String[]::new);
		node.getStyleClass().removeAll(toRemove);
		// Update applied variants Ser
		appliedVariants.clear();
		Collections.addAll(appliedVariants, variants);

		// Add all new variants
		return addVariants(node, variants);
	}

	/**
	 * Removes all the given variants from the given node.
	 */
	@SafeVarargs
	static <V extends Variant, N extends Node & WithVariants<N, V>> N removeVariants(N node, V... variants) {
		Set<V> appliedVariants = node.getAppliedVariants();
		for (V v : variants) {
			if (appliedVariants.remove(v))
				node.getStyleClass().remove(v.variantStyleClass());
		}
		return node;
	}
}
