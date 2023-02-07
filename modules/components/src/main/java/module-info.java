module mfx.components {
	requires transitive javafx.controls;

	requires transitive mfx.core;
	requires transitive mfx.effects;
	requires transitive mfx.localization;
	requires transitive mfx.resources;

	// Behaviors
	exports io.github.palexdev.mfxcomponents.behaviors;

	// Controls
	exports io.github.palexdev.mfxcomponents.controls.buttons;

	// Skins
	exports io.github.palexdev.mfxcomponents.skins;

	// Theming
	exports io.github.palexdev.mfxcomponents.theming;
}