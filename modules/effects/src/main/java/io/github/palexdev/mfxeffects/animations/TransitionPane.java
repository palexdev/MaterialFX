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

package io.github.palexdev.mfxeffects.animations;

import io.github.palexdev.mfxeffects.animations.base.ITransitionType;
import io.github.palexdev.mfxeffects.beans.Position;
import io.github.palexdev.mfxeffects.beans.Size;
import io.github.palexdev.mfxeffects.utils.LayoutUtils;
import io.github.palexdev.mfxeffects.utils.TriFunction;
import javafx.animation.Animation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.function.Supplier;

public class TransitionPane extends StackPane {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "transition-pane";
	public static final PseudoClass CLOSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("closed");
	public static final PseudoClass OPEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("open");

	private final ObjectProperty<Node> closedNode = new SimpleObjectProperty<>() {
		@Override
		public void set(Node newValue) {
			Node oldValue = get();
			if (oldValue != null)
				TransitionPane.super.getChildren().remove(oldValue);

			if (newValue != null)
				TransitionPane.super.getChildren().add(newValue);
			super.set(newValue);
		}
	};
	private final ObjectProperty<Node> openNode = new SimpleObjectProperty<>() {
		@Override
		public void set(Node newValue) {
			Node oldValue = get();
			if (oldValue != null)
				TransitionPane.super.getChildren().remove(oldValue);

			if (newValue != null) {
				TransitionPane.super.getChildren().add(newValue);
				newValue.setManaged(false);
				newValue.setVisible(false);
				newValue.setOpacity(0.0);
			}
			super.set(newValue);
		}
	};
	private final ReadOnlyBooleanWrapper open = new ReadOnlyBooleanWrapper(false);

	private final ObjectProperty<Supplier<Size>> targetSize = new SimpleObjectProperty<>() {
		@Override
		protected void invalidated() {
			cachedClosedSize = null;
			cachedOpenSize = null;
		}
	};
	private final ObjectProperty<Supplier<Position>> targetOffset = new SimpleObjectProperty<>(() -> Position.of(0, 0));
	private Size cachedClosedSize;
	private Size cachedOpenSize;

	private final ObjectProperty<TriFunction<TransitionPane, Node, Node, Animation>> closeAnimationFactory = new SimpleObjectProperty<>();
	private final ObjectProperty<TriFunction<TransitionPane, Node, Node, Animation>> openAnimationFactory = new SimpleObjectProperty<>();
	private Animation cachedCloseAnimation;
	private Animation cachedOpenAnimation;

	//================================================================================
	// Constructors
	//================================================================================
	public TransitionPane() {
		this(null, null);
	}

	public TransitionPane(Node closedNode, Node openNode) {
		setClosedNode(closedNode);
		setOpenNode(openNode);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, true);
		setTargetSize(() -> {
			Node node = getOpenNode();
			if (node == null) return Size.of(-1, -1);
			return Size.of(
					LayoutUtils.boundWidth(node),
					LayoutUtils.boundHeight(node)
			);
		});

		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
	}

	public void open() {
		if (isOpen()) return;
		Node openNode = getOpenNode();
		Size size = getOpenSize();
		if (openNode == null || Size.of(-1, -1).equals(size)) return;

		setOpen(true);
		if (cachedOpenAnimation == null)
			cachedOpenAnimation = getOpenAnimationFactory().apply(this, openNode, getClosedNode());
		if (cachedCloseAnimation != null) cachedCloseAnimation.stop();
		cachedOpenAnimation.playFromStart();
	}

	public void close() {
		if (!isOpen()) return;
		Node closedNode = getClosedNode();
		if (closedNode == null) return;
		getClosedSize();

		setOpen(false);
		if (cachedCloseAnimation == null)
			cachedCloseAnimation = getCloseAnimationFactory().apply(this, getOpenNode(), closedNode);
		if (cachedOpenAnimation != null) cachedOpenAnimation.stop();
		cachedCloseAnimation.playFromStart();
	}

	public void setAnimationType(ITransitionType type) {
		setOpenAnimationFactory(type::open);
		setCloseAnimationFactory(type::close);
	}

	public Size getOpenSize() {
		if (cachedOpenSize == null || Size.of(-1, -1).equals(cachedOpenSize)) {
			cachedOpenSize = getTargetSize().get();
		}
		return cachedOpenSize;
	}

	public Size getClosedSize() {
		if (cachedClosedSize == null || Size.of(-1, -1).equals(cachedClosedSize)) {
			Node closedNode = getClosedNode();
			cachedClosedSize = (closedNode != null) ?
					Size.of(
							LayoutUtils.boundWidth(closedNode),
							LayoutUtils.boundHeight(closedNode)
					) :
					Size.of(-1, -1);
		}
		return cachedClosedSize;
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public ObservableList<Node> getChildren() {
		return getChildrenUnmodifiable();
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		Node node = getOpenNode();
		if (node == null) return;
		node.resizeRelocate(0, 0, getWidth(), getHeight());
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	public Node getClosedNode() {
		return closedNode.get();
	}

	public ObjectProperty<Node> closedNodeProperty() {
		return closedNode;
	}

	public void setClosedNode(Node closedNode) {
		this.closedNode.set(closedNode);
	}

	public Node getOpenNode() {
		return openNode.get();
	}

	public ObjectProperty<Node> openNodeProperty() {
		return openNode;
	}

	public void setOpenNode(Node openNode) {
		this.openNode.set(openNode);
	}

	public boolean isOpen() {
		return open.get();
	}

	public ReadOnlyBooleanProperty openProperty() {
		return open.getReadOnlyProperty();
	}

	protected void setOpen(boolean open) {
		this.open.set(open);
		pseudoClassStateChanged(CLOSED_PSEUDO_CLASS, !open);
		pseudoClassStateChanged(OPEN_PSEUDO_CLASS, open);
	}

	public Supplier<Size> getTargetSize() {
		return targetSize.get();
	}

	public ObjectProperty<Supplier<Size>> targetSizeProperty() {
		return targetSize;
	}

	public void setTargetSize(Supplier<Size> targetSize) {
		this.targetSize.set(targetSize);
	}

	public Supplier<Position> getTargetOffset() {
		return targetOffset.get();
	}

	public ObjectProperty<Supplier<Position>> targetOffsetProperty() {
		return targetOffset;
	}

	public void setTargetOffset(Supplier<Position> targetOffset) {
		this.targetOffset.set(targetOffset);
	}

	public TriFunction<TransitionPane, Node, Node, Animation> getCloseAnimationFactory() {
		return closeAnimationFactory.get();
	}

	public ObjectProperty<TriFunction<TransitionPane, Node, Node, Animation>> closeAnimationFactoryProperty() {
		return closeAnimationFactory;
	}

	public void setCloseAnimationFactory(TriFunction<TransitionPane, Node, Node, Animation> closeAnimationFactory) {
		this.closeAnimationFactory.set(closeAnimationFactory);
	}

	public TriFunction<TransitionPane, Node, Node, Animation> getOpenAnimationFactory() {
		return openAnimationFactory.get();
	}

	public ObjectProperty<TriFunction<TransitionPane, Node, Node, Animation>> openAnimationFactoryProperty() {
		return openAnimationFactory;
	}

	public void setOpenAnimationFactory(TriFunction<TransitionPane, Node, Node, Animation> openAnimationFactory) {
		this.openAnimationFactory.set(openAnimationFactory);
	}
}
