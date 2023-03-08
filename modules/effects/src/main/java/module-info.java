module mfx.effects {
	requires transitive javafx.controls;

	// Animations
	exports io.github.palexdev.mfxeffects.animations;
	exports io.github.palexdev.mfxeffects.animations.motion;
	exports io.github.palexdev.mfxeffects.animations.motion.base;

	// Base
	exports io.github.palexdev.mfxeffects;

	// Beans
	exports io.github.palexdev.mfxeffects.beans;
	exports io.github.palexdev.mfxeffects.beans.base;

	// Builders
	exports io.github.palexdev.mfxeffects.builders;

	// Enums
	exports io.github.palexdev.mfxeffects.enums;

	// Ripple
	exports io.github.palexdev.mfxeffects.ripple;
	exports io.github.palexdev.mfxeffects.ripple.base;
}