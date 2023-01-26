module mfx.effects {
	requires transitive javafx.controls;

	// Base
	exports io.github.palexdev.mfxeffects;

	// Beans
	exports io.github.palexdev.mfxeffects.beans;

	// Builders
	exports io.github.palexdev.mfxeffects.builders;

	// Ripple
	exports io.github.palexdev.mfxeffects.ripple;
	exports io.github.palexdev.mfxeffects.ripple.base;
}