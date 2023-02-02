module mfx.components {
	requires transitive javafx.controls;

	requires transitive mfx.core;
	requires transitive mfx.effects;
	requires transitive mfx.localization;
	requires transitive mfx.resources;

	// Behaviors
	exports io.github.palexdev.materialfx.behaviors;

	// Controls
	exports io.github.palexdev.materialfx.controls.buttons;

	// Skins
	exports io.github.palexdev.materialfx.skins;

	// Theming
	exports io.github.palexdev.materialfx.theming;
}