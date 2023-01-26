module mfx.resources {
	requires mfx.effects;

	requires transitive javafx.graphics;

	// Base
	exports io.github.palexdev.mfxresources;

	// Builders
	exports io.github.palexdev.mfxresources.builders;

	// Fonts
	exports io.github.palexdev.mfxresources.fonts;
}