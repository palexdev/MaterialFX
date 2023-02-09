module mfx.resources {
	requires mfx.effects;

	requires transitive javafx.graphics;

	// Root
	exports io.github.palexdev.mfxresources;

	// Base
	exports io.github.palexdev.mfxresources.base.properties;

	// Builders
	exports io.github.palexdev.mfxresources.builders;

	// Fonts
	exports io.github.palexdev.mfxresources.fonts;
}