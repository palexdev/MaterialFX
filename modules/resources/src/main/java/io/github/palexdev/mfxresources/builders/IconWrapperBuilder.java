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

package io.github.palexdev.mfxresources.builders;

import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxresources.fonts.IconDescriptor;
import io.github.palexdev.mfxresources.fonts.IconsProviders;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper;
import io.github.palexdev.mfxresources.fonts.MFXIconWrapper.AnimationPresets;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.NodeOrientation;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;

import java.util.function.Function;

/**
 * A commodity class to build {@link MFXIconWrapper}s with fluent API.
 */
public class IconWrapperBuilder {
	//================================================================================
	// Properties
	//================================================================================
	protected final MFXIconWrapper wrapper;

	//================================================================================
	// Constructors
	//================================================================================
	public IconWrapperBuilder() {
		this.wrapper = new MFXIconWrapper();
	}

	public IconWrapperBuilder(MFXIconWrapper wrapper) {
		this.wrapper = wrapper;
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static IconWrapperBuilder build() {
		return new IconWrapperBuilder();
	}

	public static IconWrapperBuilder build(MFXIconWrapper wrapper) {
		return new IconWrapperBuilder(wrapper);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public IconWrapperBuilder enableRippleGenerator(boolean enable) {
		wrapper.enableRippleGenerator(enable);
		return this;
	}

	public IconWrapperBuilder enableRippleGenerator(boolean enable, Function<MouseEvent, Position> positionFunction) {
		wrapper.enableRippleGenerator(enable, positionFunction);
		return this;
	}

	public IconWrapperBuilder makeRound(boolean state) {
		wrapper.makeRound(state);
		return this;
	}

	public IconWrapperBuilder makeRound(boolean state, double radius) {
		wrapper.makeRound(state, radius);
		return this;
	}

	public IconWrapperBuilder setSize(double size) {
		wrapper.setSize(size);
		return this;
	}

	public IconWrapperBuilder setIcon(MFXFontIcon icon) {
		wrapper.setIcon(icon);
		return this;
	}

	public IconWrapperBuilder setIcon(String desc) {
		wrapper.setIcon(desc);
		return this;
	}

	public IconWrapperBuilder setIcon(IconsProviders provider, String desc) {
		wrapper.setIcon(provider, desc);
		return this;
	}

	public IconWrapperBuilder setIcon(Font font, Function<String, Character> converter, String desc) {
		wrapper.setIcon(font, converter, desc);
		return this;
	}

	public MFXIconWrapper setAnimated(boolean animated) {
		return wrapper.setAnimated(animated);
	}

	public MFXIconWrapper setIcon(IconDescriptor descriptor) {
		return wrapper.setIcon(descriptor);
	}

	public MFXIconWrapper setAnimationProvider(AnimationPresets preset) {
		return wrapper.setAnimationProvider(preset);
	}

	//================================================================================
	// Node Delegate Methods
	//================================================================================
	public IconWrapperBuilder setId(String id) {
		wrapper.setId(id);
		return this;
	}

	public IconWrapperBuilder setStyle(String style) {
		wrapper.setStyle(style);
		return this;
	}

	public IconWrapperBuilder addStyleClasses(String... styleClasses) {
		wrapper.getStyleClass().addAll(styleClasses);
		return this;
	}

	public IconWrapperBuilder setStyleClasses(String... styleClasses) {
		wrapper.getStyleClass().setAll(styleClasses);
		return this;
	}

	public IconWrapperBuilder pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {
		wrapper.pseudoClassStateChanged(pseudoClass, active);
		return this;
	}

	public IconWrapperBuilder setVisible(boolean visible) {
		wrapper.setVisible(visible);
		return this;
	}

	public IconWrapperBuilder setCursor(Cursor cursor) {
		wrapper.setCursor(cursor);
		return this;
	}

	public IconWrapperBuilder setOpacity(double opacity) {
		wrapper.setOpacity(opacity);
		return this;
	}

	public IconWrapperBuilder setClip(Node clip) {
		wrapper.setClip(clip);
		return this;
	}

	public IconWrapperBuilder setCache(boolean cache) {
		wrapper.setCache(cache);
		return this;
	}

	public IconWrapperBuilder setCacheHint(CacheHint cacheHint) {
		wrapper.setCacheHint(cacheHint);
		return this;
	}

	public IconWrapperBuilder setEffect(Effect effect) {
		wrapper.setEffect(effect);
		return this;
	}

	public IconWrapperBuilder setDisable(boolean disable) {
		wrapper.setDisable(disable);
		return this;
	}

	public IconWrapperBuilder setManaged(boolean managed) {
		wrapper.setManaged(managed);
		return this;
	}

	public IconWrapperBuilder relocate(double x, double y) {
		wrapper.relocate(x, y);
		return this;
	}

	public IconWrapperBuilder resize(double width, double height) {
		wrapper.resize(width, height);
		return this;
	}

	public IconWrapperBuilder resizeRelocate(double x, double y, double width, double height) {
		wrapper.resizeRelocate(x, y, width, height);
		return this;
	}

	public IconWrapperBuilder autosize() {
		wrapper.autosize();
		return this;
	}

	public IconWrapperBuilder setViewOrder(double viewOrder) {
		wrapper.setViewOrder(viewOrder);
		return this;
	}

	public IconWrapperBuilder setTranslateX(double translateX) {
		wrapper.setTranslateX(translateX);
		return this;
	}

	public IconWrapperBuilder setTranslateY(double translateY) {
		wrapper.setTranslateY(translateY);
		return this;
	}

	public IconWrapperBuilder setTranslateZ(double translateZ) {
		wrapper.setTranslateZ(translateZ);
		return this;
	}

	public IconWrapperBuilder setScaleX(double scaleX) {
		wrapper.setScaleX(scaleX);
		return this;
	}

	public IconWrapperBuilder setScaleY(double scaleY) {
		wrapper.setScaleY(scaleY);
		return this;
	}

	public IconWrapperBuilder setScaleZ(double scaleZ) {
		wrapper.setScaleZ(scaleZ);
		return this;
	}

	public IconWrapperBuilder setRotate(double rotate) {
		wrapper.setRotate(rotate);
		return this;
	}

	public IconWrapperBuilder setNodeOrientation(NodeOrientation orientation) {
		wrapper.setNodeOrientation(orientation);
		return this;
	}

	public IconWrapperBuilder setMouseTransparent(boolean mouseTransparent) {
		wrapper.setMouseTransparent(mouseTransparent);
		return this;
	}

	public IconWrapperBuilder setFocusTraversable(boolean focusTraversable) {
		wrapper.setFocusTraversable(focusTraversable);
		return this;
	}

	public IconWrapperBuilder requestFocus() {
		wrapper.requestFocus();
		return this;
	}

	public <T extends Event> IconWrapperBuilder addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		wrapper.addEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> IconWrapperBuilder removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		wrapper.removeEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> IconWrapperBuilder addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		wrapper.addEventFilter(eventType, eventFilter);
		return this;
	}

	public <T extends Event> IconWrapperBuilder removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		wrapper.removeEventFilter(eventType, eventFilter);
		return this;
	}

	//================================================================================
	// Methods
	//================================================================================
	public MFXIconWrapper get() {
		return wrapper;
	}
}
