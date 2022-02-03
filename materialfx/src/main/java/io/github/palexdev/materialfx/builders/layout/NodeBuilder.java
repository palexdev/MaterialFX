/*
 * Copyright (C) 2022 Parisi Alessandro
 * This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 * MaterialFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.builders.layout;

import io.github.palexdev.materialfx.builders.base.INodeBuilder;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.NodeOrientation;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.Effect;

public class NodeBuilder<N extends Node> implements INodeBuilder<N> {
	//================================================================================
	// Properties
	//================================================================================
	protected final N node;

	//================================================================================
	// Constructors
	//================================================================================
	public NodeBuilder(N node) {
		this.node = node;
	}

	public static NodeBuilder<Node> node(Node node) {
		return new NodeBuilder<>(node);
	}

	//================================================================================
	// Delegate Methods
	//================================================================================
	public NodeBuilder<N> setId(String id) {
		node.setId(id);
		return this;
	}

	public NodeBuilder<N> setStyle(String style) {
		node.setStyle(style);
		return this;
	}
	
	public NodeBuilder<N> addStyleClasses(String... styleClasses) {
		node.getStyleClass().addAll(styleClasses);
		return this;
	}
	
	public NodeBuilder<N> setStyleClasses(String... styleClasses) {
		node.getStyleClass().setAll(styleClasses);
		return this;
	}

	public NodeBuilder<N> pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {
		node.pseudoClassStateChanged(pseudoClass, active);
		return this;
	}

	public NodeBuilder<N> setVisible(boolean visible) {
		node.setVisible(visible);
		return this;
	}

	public NodeBuilder<N> setCursor(Cursor cursor) {
		node.setCursor(cursor);
		return this;
	}

	public NodeBuilder<N> setOpacity(double opacity) {
		node.setOpacity(opacity);
		return this;
	}

	public NodeBuilder<N> setClip(Node clip) {
		node.setClip(clip);
		return this;
	}

	public NodeBuilder<N> setCache(boolean cache) {
		node.setCache(cache);
		return this;
	}

	public NodeBuilder<N> setCacheHint(CacheHint cacheHint) {
		node.setCacheHint(cacheHint);
		return this;
	}

	public NodeBuilder<N> setEffect(Effect effect) {
		node.setEffect(effect);
		return this;
	}

	public NodeBuilder<N> setDisable(boolean disable) {
		node.setDisable(disable);
		return this;
	}

	public NodeBuilder<N> setManaged(boolean managed) {
		node.setManaged(managed);
		return this;
	}

	public NodeBuilder<N> relocate(double x, double y) {
		node.relocate(x, y);
		return this;
	}

	public NodeBuilder<N> resize(double width, double height) {
		node.resize(width, height);
		return this;
	}

	public NodeBuilder<N> resizeRelocate(double x, double y, double width, double height) {
		node.resizeRelocate(x, y, width, height);
		return this;
	}

	public NodeBuilder<N> autosize() {
		node.autosize();
		return this;
	}

	public NodeBuilder<N> setViewOrder(double viewOrder) {
		node.setViewOrder(viewOrder);
		return this;
	}

	public NodeBuilder<N> setTranslateX(double translateX) {
		node.setTranslateX(translateX);
		return this;
	}

	public NodeBuilder<N> setTranslateY(double translateY) {
		node.setTranslateY(translateY);
		return this;
	}

	public NodeBuilder<N> setTranslateZ(double translateZ) {
		node.setTranslateZ(translateZ);
		return this;
	}

	public NodeBuilder<N> setScaleX(double scaleX) {
		node.setScaleX(scaleX);
		return this;
	}

	public NodeBuilder<N> setScaleY(double scaleY) {
		node.setScaleY(scaleY);
		return this;
	}

	public NodeBuilder<N> setScaleZ(double scaleZ) {
		node.setScaleZ(scaleZ);
		return this;
	}

	public NodeBuilder<N> setRotate(double rotate) {
		node.setRotate(rotate);
		return this;
	}

	public NodeBuilder<N> setNodeOrientation(NodeOrientation orientation) {
		node.setNodeOrientation(orientation);
		return this;
	}

	public NodeBuilder<N> setMouseTransparent(boolean mouseTransparent) {
		node.setMouseTransparent(mouseTransparent);
		return this;
	}

	public NodeBuilder<N> setFocusTraversable(boolean focusTraversable) {
		node.setFocusTraversable(focusTraversable);
		return this;
	}

	public NodeBuilder<N> requestFocus() {
		node.requestFocus();
		return this;
	}

	public <T extends Event> NodeBuilder<N> addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		node.addEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> NodeBuilder<N> removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
		node.removeEventHandler(eventType, eventHandler);
		return this;
	}

	public <T extends Event> NodeBuilder<N> addEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		node.addEventFilter(eventType, eventFilter);
		return this;
	}

	public <T extends Event> NodeBuilder<N> removeEventFilter(EventType<T> eventType, EventHandler<? super T> eventFilter) {
		node.removeEventFilter(eventType, eventFilter);
		return this;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public N getNode() {
		return node;
	}
}
