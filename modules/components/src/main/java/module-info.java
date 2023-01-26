module mfx.components {
	requires transitive javafx.controls;

	requires transitive mfx.core;
	requires transitive mfx.localization;
	requires transitive mfx.resources;

	exports io.github.palexdev.materialfx.behaviors;
	exports io.github.palexdev.materialfx.controls;
}