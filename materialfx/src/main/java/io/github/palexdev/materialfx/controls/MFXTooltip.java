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

package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.skins.MFXTooltipSkin;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * {@link MFXTooltip} is a special case of {@link MFXPopup}.
 * The content is determined by its skin, and also has a userAgentStylesheet to define
 * the default style for all {@code MFXTooltips}.
 * <p>
 * This tooltip also allows you to add an icon!
 * <p></p>
 * This popup adds an handler to the owner node to track the mouse location on the node
 * and then uses those coordinated to show itself.
 * <p>
 * This is the default behavior but it can be easily changed by setting the {@link #showActionProperty()}.
 * <p></p>
 * <b>NOTE</b> that since the content of the context menu is entirely determined by its skin, the {@link #contentProperty()}
 * will always be null. As a result methods involving {@link Alignment}, {@link HPos} or {@link VPos} will fail with a
 * NullPointerException.
 */
public class MFXTooltip extends MFXPopup {
	//================================================================================
	// Static Properties
	//================================================================================
	private static final WeakHashMap<Node, MFXTooltip> tooltipsMap = new WeakHashMap<>();

	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-tooltip";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTooltip.css");
	private final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
	private final StringProperty text = new SimpleStringProperty();

	private Node owner;
	private final PositionBean mousePosition = PositionBean.of(0, 0);
	private final ConsumerProperty<PositionBean> showAction = new ConsumerProperty<>(position -> show(owner, position.getX(), position.getY()));
	private EventHandler<MouseEvent> mouseTracker;
	private EventHandler<MouseEvent> mouseEntered;
	private EventHandler<MouseEvent> mouseExited;
	private EventHandler<MouseEvent> mousePressed;

	private PauseTransition delayAnimation;
	private PauseTransition hideAnimation;
	private Duration showDelay = Duration.seconds(1);
	private Duration hideAfter = Duration.seconds(30);

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTooltip(Node owner) {
		this(owner, "");
	}

	public MFXTooltip(Node owner, String text) {
		if (owner == null) {
			throw new NullPointerException("Owner node cannot be null!");
		}
		if (tooltipsMap.getOrDefault(owner, null) != null) {
			throw new IllegalArgumentException("A tooltip for the given node already exists!");
		}

		this.owner = owner;
		setText(text);

		hideAnimation = AnimationUtils.PauseBuilder.build()
				.setOnFinished(event -> hide())
				.getAnimation();
		delayAnimation = AnimationUtils.PauseBuilder.build()
				.setOnFinished(event -> {
					if (owner instanceof Control) {
						Control control = (Control) owner;
						if (control.getTooltip() != null) {
							throw new IllegalStateException("The given control already has a JavaFX Tooltip installed!");
						}
					}

					getShowAction().accept(mousePosition);
					if (hideAfter == Duration.INDEFINITE) return;
					hideAnimation.setDuration(hideAfter);
					hideAnimation.playFromStart();
				})
				.getAnimation();

		mouseTracker = event -> {
			mousePosition.setX(event.getScreenX() + 10);
			mousePosition.setY(event.getScreenY() + 10);
		};

		mouseEntered = event -> {
			if (isShowing() || AnimationUtils.isPlaying(delayAnimation)) {
				hideAnimation.stop();
				return;
			}

			hideAnimation.stop();
			delayAnimation.setDuration(showDelay);
			delayAnimation.playFromStart();
		};

		mouseExited = event -> {
			if (AnimationUtils.isPlaying(hideAnimation)) hideAnimation.stop();
			if (AnimationUtils.isPlaying(delayAnimation)) delayAnimation.stop();
			hide();
		};

		mousePressed = event -> {
			if (AnimationUtils.isPlaying(hideAnimation)) hideAnimation.stop();
			if (AnimationUtils.isPlaying(delayAnimation)) delayAnimation.stop();
			hide();
		};

		initialize();
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Static method to create a new {@code MFXTooltip} with the given parameters.
	 */
	public static MFXTooltip of(Node owner, String text) {
		return new MFXTooltip(owner, text);
	}

	/**
	 * Convenience method to remove a {@code MFXTooltip} from the given
	 * node.
	 */
	public static void disposeFor(Node node) {
		MFXTooltip tooltip = tooltipsMap.remove(node);
		if (tooltip != null) tooltip.dispose();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
	}

	/**
	 * Adds the needed handlers on the owner node.
	 */
	public MFXTooltip install() {
		owner.addEventFilter(MouseEvent.MOUSE_MOVED, mouseTracker);
		owner.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEntered);
		owner.addEventFilter(MouseEvent.MOUSE_EXITED, mouseExited);
		owner.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressed);
		return this;
	}

	/**
	 * Removes any added handler from the owner node.
	 */
	public void uninstall() {
		owner.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseTracker);
		owner.removeEventFilter(MouseEvent.MOUSE_ENTERED, mouseEntered);
		owner.removeEventFilter(MouseEvent.MOUSE_EXITED, mouseExited);
		owner.removeEventFilter(MouseEvent.MOUSE_PRESSED, mousePressed);
	}

	/**
	 * Calls {@link #uninstall()} but also sets all the handlers and the owner
	 * node to null, making this context menu not usable anymore.
	 */
	public void dispose() {
		if (owner != null) {
			uninstall();
			mouseTracker = null;
			mouseEntered = null;
			mouseExited = null;
			mousePressed = null;
			delayAnimation = null;
			hideAnimation = null;
			tooltipsMap.remove(owner);
			owner = null;
		}
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTooltipSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * @return this context menu's owner
	 */
	public Node getOwner() {
		return owner;
	}

	public PositionBean getMousePosition() {
		return mousePosition;
	}

	public Consumer<PositionBean> getShowAction() {
		return showAction.get();
	}

	/**
	 * This consumer allows the user to decide how to show the tooltip.
	 * The consumer carries the tracked mouse position, see {@link #getMousePosition()}.
	 * <p>
	 * By default, calls {@link #show(Node, double, double)}.
	 */
	public ConsumerProperty<PositionBean> showActionProperty() {
		return showAction;
	}

	public void setShowAction(Consumer<PositionBean> showAction) {
		this.showAction.set(showAction);
	}

	public Node getIcon() {
		return icon.get();
	}

	/**
	 * Specifies the tooltip's icon.
	 */
	public ObjectProperty<Node> iconProperty() {
		return icon;
	}

	public void setIcon(Node icon) {
		this.icon.set(icon);
	}

	public String getText() {
		return text.get();
	}

	/**
	 * Specifies the tooltip's text.
	 */
	public StringProperty textProperty() {
		return text;
	}

	public void setText(String text) {
		this.text.set(text);
	}

	/**
	 * @return the amount of time after which the tooltip is shown
	 */
	public Duration getShowDelay() {
		return showDelay;
	}

	/**
	 * Sets the amount of time after which the tooltip is shown
	 */
	public void setShowDelay(Duration showDelay) {
		this.showDelay = showDelay;
	}

	/**
	 * @return the amount of time after which the tooltip is automatically hidden
	 */
	public Duration getHideAfter() {
		return hideAfter;
	}

	/**
	 * Sets the amount of time after which the tooltip is automatically hidden.
	 */
	public void setHideAfter(Duration hideAfter) {
		this.hideAfter = hideAfter;
	}
}
