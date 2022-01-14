module MaterialFX {
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;
	requires transitive java.desktop;

	requires virtualizedfx;

	exports io.github.palexdev.materialfx;

	// Beans Package
	exports io.github.palexdev.materialfx.beans;
	exports io.github.palexdev.materialfx.beans.properties;
	exports io.github.palexdev.materialfx.beans.properties.base;
	exports io.github.palexdev.materialfx.beans.properties.functional;
	exports io.github.palexdev.materialfx.beans.properties.resettable;
	exports io.github.palexdev.materialfx.beans.properties.styleable;
	exports io.github.palexdev.materialfx.beans.properties.synced;

	// Bindings Package
	exports io.github.palexdev.materialfx.bindings;
	exports io.github.palexdev.materialfx.bindings.base;

	// Collections Package
	exports io.github.palexdev.materialfx.collections;

	// Controls Package
	exports io.github.palexdev.materialfx.controls;
	exports io.github.palexdev.materialfx.controls.base;
	exports io.github.palexdev.materialfx.controls.cell;
	exports io.github.palexdev.materialfx.controls.legacy;

	// CSS Package
	exports io.github.palexdev.materialfx.css;

	// Dialogs Package
	exports io.github.palexdev.materialfx.dialogs;

	// Effects Package
	exports io.github.palexdev.materialfx.effects;
	exports io.github.palexdev.materialfx.effects.ripple;
	exports io.github.palexdev.materialfx.effects.ripple.base;

	// Enums Package
	exports io.github.palexdev.materialfx.enums;

	// Factories Package
	exports io.github.palexdev.materialfx.factories;

	// Filter Package
	exports io.github.palexdev.materialfx.filter;
	exports io.github.palexdev.materialfx.filter.base;

	// Font Package
	exports io.github.palexdev.materialfx.font;

	// Notifications Package
	exports io.github.palexdev.materialfx.notifications;
	exports io.github.palexdev.materialfx.notifications.base;

	// Selection Package
	exports io.github.palexdev.materialfx.selection;
	exports io.github.palexdev.materialfx.selection.base;

	// Skins Package
	exports io.github.palexdev.materialfx.skins;
	exports io.github.palexdev.materialfx.skins.base;
	exports io.github.palexdev.materialfx.skins.legacy;

	// Utils Package
	exports io.github.palexdev.materialfx.utils;
	exports io.github.palexdev.materialfx.utils.others;

	// Validation Package
	exports io.github.palexdev.materialfx.validation;
}