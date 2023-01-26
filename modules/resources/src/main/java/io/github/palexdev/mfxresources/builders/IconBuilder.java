/*
 * Copyright (C) 2022 Parisi Alessandro - alessandro.parisi406@gmail.com
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

package io.github.palexdev.mfxresources.builders;

import io.github.palexdev.mfxresources.fonts.IconsProviders;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.NodeOrientation;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Function;

/**
 * A commodity class to build {@link MFXFontIcon}s with fluent API.
 */
public class IconBuilder {
	//================================================================================
	// Properties
	//================================================================================
	protected final MFXFontIcon icon;

	//================================================================================
	// Constructors
	//================================================================================
	public IconBuilder() {
		this.icon = new MFXFontIcon();
	}

	public IconBuilder(MFXFontIcon icon) {
		this.icon = icon;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static IconBuilder build() {
		return new IconBuilder();
	}

	public static IconBuilder build(MFXFontIcon icon) {
		return new IconBuilder(icon);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public IconBuilder setColor(Color color) {
		icon.setColor(color);
		return this;
	}

	public IconBuilder setDescription(String code) {
		icon.setDescription(code);
		return this;
	}

	public IconBuilder setSize(double size) {
		icon.setSize(size);
		return this;
	}

	public IconBuilder setIconsProvider(IconsProviders provider) {
		icon.setIconsProvider(provider);
		return this;
	}

	public IconBuilder setIconsProvider(Font font, Function<String, Character> converter) {
		icon.setIconsProvider(font, converter);
		return this;
	}

	public IconBuilder setDescriptionConverter(Function<String, Character> descriptionConverter) {
		icon.setDescriptionConverter(descriptionConverter);
		return this;
	}

	public IconWrapperBuilder wrap() {
		return new IconWrapperBuilder(icon.wrap());
	}

	//================================================================================
	// Node Delegate Methods
	//================================================================================
	public IconBuilder setId(String id) {
		icon.setId(id);
		return this;
	}

	public IconBuilder setStyle(String style) {
		icon.setStyle(style);
		return this;
	}

	public IconBuilder addStyleClasses(String... styleClasses) {
		icon.getStyleClass().addAll(styleClasses);
		return this;
	}

	public IconBuilder setStyleClasses(String... styleClasses) {
		icon.getStyleClass().setAll(styleClasses);
		return this;
	}

	public IconBuilder pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {
		icon.pseudoClassStateChanged(pseudoClass, active);
		return this;
	}

	public IconBuilder setVisible(boolean visible) {
		icon.setVisible(visible);
		return this;
	}

	public IconBuilder setCursor(Cursor cursor) {
		icon.setCursor(cursor);
		return this;
	}

	public IconBuilder setOpacity(double opacity) {
		icon.setOpacity(opacity);
		return this;
	}

	public IconBuilder setClip(Node clip) {
		icon.setClip(clip);
		return this;
	}

	public IconBuilder setCache(boolean cache) {
		icon.setCache(cache);
		return this;
	}

	public IconBuilder setCacheHint(CacheHint cacheHint) {
		icon.setCacheHint(cacheHint);
		return this;
	}

	public IconBuilder setEffect(Effect effect) {
		icon.setEffect(effect);
		return this;
	}

	public IconBuilder setDisable(boolean disable) {
		icon.setDisable(disable);
		return this;
	}

	public IconBuilder setManaged(boolean managed) {
		icon.setManaged(managed);
		return this;
	}

	public IconBuilder relocate(double x, double y) {
		icon.relocate(x, y);
		return this;
	}

	public IconBuilder resize(double width, double height) {
		icon.resize(width, height);
		return this;
	}

	public IconBuilder resizeRelocate(double x, double y, double width, double height) {
		icon.resizeRelocate(x, y, width, height);
		return this;
	}

	public IconBuilder autosize() {
		icon.autosize();
		return this;
	}

	public IconBuilder setViewOrder(double viewOrder) {
		icon.setViewOrder(viewOrder);
		return this;
	}

	public IconBuilder setTranslateX(double translateX) {
		icon.setTranslateX(translateX);
		return this;
	}

	public IconBuilder setTranslateY(double translateY) {
		icon.setTranslateY(translateY);
		return this;
	}

	public IconBuilder setTranslateZ(double translateZ) {
		icon.setTranslateZ(translateZ);
		return this;
	}

	public IconBuilder setScaleX(double scaleX) {
		icon.setScaleX(scaleX);
		return this;
	}

	public IconBuilder setScaleY(double scaleY) {
		icon.setScaleY(scaleY);
		return this;
	}

	public IconBuilder setScaleZ(double scaleZ) {
		icon.setScaleZ(scaleZ);
		return this;
	}

	public IconBuilder setRotate(double rotate) {
		icon.setRotate(rotate);
		return this;
	}

	public IconBuilder setNodeOrientation(NodeOrientation orientation) {
		icon.setNodeOrientation(orientation);
		return this;
	}

	public IconBuilder setMouseTransparent(boolean mouseTransparent) {
		icon.setMouseTransparent(mouseTransparent);
		return this;
	}

	public IconBuilder setFocusTraversable(boolean focusTraversable) {
		icon.setFocusTraversable(focusTraversable);
		return this;
	}

	public IconBuilder requestFocus() {
		icon.requestFocus();
		return this;
	}

	public <T extends Event> IconBuilder addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		icon.addEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> IconBuilder removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		icon.removeEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> IconBuilder addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		icon.addEventFilter(eventType, eventFilter);
		return this;
	}

	public <T extends Event> IconBuilder removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		icon.removeEventFilter(eventType, eventFilter);
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================
	public MFXFontIcon get() {
		return icon;
	}
}
