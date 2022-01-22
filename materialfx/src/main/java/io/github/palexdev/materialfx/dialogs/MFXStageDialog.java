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

package io.github.palexdev.materialfx.dialogs;

import io.github.palexdev.materialfx.beans.SizeBean;
import io.github.palexdev.materialfx.effects.MFXScrimEffect;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.TimelineBuilder;
import javafx.animation.Interpolator;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * Dialog implementation that simply extends {@link Stage}.
 * <p>
 * The main purpose of this is to wrap/show {@link AbstractMFXDialog} by
 * setting the {@link #contentProperty()}.
 * <p>
 * This stage dialog is also draggable with the mouse (can be enabled/disabled),
 * can be closed when pressing on the owner node (overlay close feature),
 * can "scrim" the owner node on open. To make these last two features work
 * it's necessary to specify who is the owner node, {@link #setOwnerNode(Pane)}.
 * <p></p>
 * To make the scrim effect work, the show/close methods of {@link Stage} have been
 * overridden, however the {@link #show()} method is final, to properly show the dialog
 * please use {@link #showDialog()} instead.
 * <p></p>
 * A side note on usage:
 * <p>
 * Unfortunately in JavaFX there is still no concept of "low-weight" dialog.
 * Even the JavaFX's default implementation is just a {@link Stage}. The issue
 * with that is that creating Stages is a quite expensive task, cannot be done on separate
 * threads dor whatever reason, and therefore can make the application unresponsive,
 * even if for a short time.
 * <p>
 * So, the advice is to build this dialogs ahead of time, keep them in memory if you can,
 * so that you can use them whenever you need and just change  their content if needed,
 */
public class MFXStageDialog extends Stage {
	//================================================================================
	// Properties
	//================================================================================
	private final ObjectProperty<AbstractMFXDialog> content = new SimpleObjectProperty<>();

	private boolean draggable = false;
	private double xPos;
	private double yPos;
	private EventHandler<MouseEvent> mousePressed = event -> {
		xPos = getX() - event.getScreenX();
		yPos = getY() - event.getScreenY();
	};
	private EventHandler<MouseEvent> mouseDragged = event -> {
		setX(event.getScreenX() + xPos);
		setY(event.getScreenY() + yPos);
	};

	private boolean overlayClose = false;
	private EventHandler<MouseEvent> overlayCloseHandler = event -> close();

	private Pane ownerNode;
	private final BooleanProperty centerInOwnerNode = new SimpleBooleanProperty(true);
	private final BooleanProperty scrimOwner = new SimpleBooleanProperty(false);
	private final DoubleProperty scrimStrength = new SimpleDoubleProperty(0.5);
	private MFXScrimEffect scrimEffect = new MFXScrimEffect();
	private ScrimPriority scrimPriority = ScrimPriority.NODE;

	//================================================================================
	// Constructors
	//================================================================================
	public MFXStageDialog() {
		this(null);
	}

	public MFXStageDialog(AbstractMFXDialog content) {
		setContent(content);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		initStyle(StageStyle.TRANSPARENT);
		scrimEffect.getScrimNode().setOpacity(0.0);

		if (getContent() != null) {
			setScene(buildScene(getContent()));
			initDraggable();
		}

		if (getOwnerNode() != null) {
			initOverlayClose();
		}

		contentProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
				oldValue.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
			}

			setScene(buildScene(getContent()));
			initDraggable();
		});

		addEventHandler(WindowEvent.WINDOW_SHOWN, event -> centerInOwner());
	}

	/**
	 * Builds the dialog's scene
	 * for the given content.
	 */
	protected Scene buildScene(AbstractMFXDialog content) {
		Scene scene = new Scene(content);
		scene.setFill(Color.TRANSPARENT);
		return scene;
	}

	/**
	 * Enables/Disabled the draggable feature.
	 */
	protected void initDraggable() {
		if (getContent() == null) return;

		AbstractMFXDialog content = getContent();
		if (isDraggable()) {
			content.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
			content.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
		} else {
			content.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
			content.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
		}
	}

	/**
	 * Enables/Disables the overlay close feature.
	 */
	protected void initOverlayClose() {
		if (getOwnerNode() == null) return;

		if (isOverlayClose()) {
			ownerNode.addEventFilter(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
		} else {
			ownerNode.removeEventFilter(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
		}
	}

	/**
	 * Calls {@link #scrimOwner()} than shows the dialog.
	 */
	public void showDialog() {
		scrimOwner();
		super.show();
	}

	/**
	 * Calls {@link #scrimOwner()} then shows the dialog and waits.
	 */
	@Override
	public void showAndWait() {
		scrimOwner();
		super.showAndWait();
	}

	/**
	 * Calls {@link #unScrimOwner()} then closes the dialog.
	 */
	@Override
	public void hide() {
		unScrimOwner();
		super.hide();
	}

	/**
	 * This is responsible for applying the scrim effect (if enabled) according to
	 * the {@link #getScrimPriority()}.
	 */
	protected void scrimOwner() {
		if (!isScrimOwner()) return;

		switch (scrimPriority) {
			case NODE: {
				if (ownerNode != null) scrimEffect.modalScrim(ownerNode, getScrimStrength());
				break;
			}
			case WINDOW: {
				if (getOwner() != null) scrimEffect.scrimWindow(getOwner(), getScrimStrength());
				break;
			}
		}

		TimelineBuilder.build()
				.add(KeyFrames.of(200, scrimEffect.getScrimNode().opacityProperty(), getScrimStrength(), Interpolator.EASE_BOTH))
				.getAnimation()
				.play();
	}

	/**
	 * If {@link #getOwnerNode()} is not null removes the scrim effect from it.
	 */
	protected void unScrimOwner() {
		if (ownerNode != null) scrimEffect.removeEffect(ownerNode);
		if (getOwner() != null) scrimEffect.removeEffect(getOwner());
		TimelineBuilder.build()
				.add(KeyFrames.of(200, scrimEffect.getScrimNode().opacityProperty(), 0, Interpolator.EASE_BOTH))
				.getAnimation()
				.play();
	}

	/**
	 * This is responsible for centering the dialog on the {@link #getOwnerNode()} (if enabled).
	 */
	protected void centerInOwner() {
		if (!isCenterInOwnerNode() || ownerNode == null) return;

		Bounds screenBounds = ownerNode.localToScreen(ownerNode.getBoundsInLocal());
		double startX = screenBounds.getMinX();
		double startY = screenBounds.getMinY();
		SizeBean dialogSize = SizeBean.of(getWidth(), getHeight());
		SizeBean nodeSize = SizeBean.of(ownerNode.getWidth(), ownerNode.getHeight());
		double x = startX + (nodeSize.getWidth() / 2 - dialogSize.getWidth() / 2);
		double y = startY + (nodeSize.getHeight() / 2 - dialogSize.getHeight() / 2);
		setX(x);
		setY(y);
	}

	/**
	 * Disposes the dialog, making it not reusable anymore!
	 */
	public void dispose() {
		AbstractMFXDialog content = getContent();
		if (content != null) {
			content.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
			content.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragged);
		}
		if (ownerNode != null) {
			ownerNode.removeEventFilter(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
		}
		mousePressed = null;
		mouseDragged = null;
		overlayCloseHandler = null;
		ownerNode = null;
		scrimEffect = null;
	}

	//================================================================================
	// Static Methods
	//================================================================================

	/**
	 * Convenience method to create a {@link MFXStageDialog} from an FXML file.
	 * <p>
	 * Two notes:
	 * <p> - the file is loaded using the given class, so make sure the FXML file
	 * is in the right directory
	 * <p> - the method expects to load a {@link AbstractMFXDialog} as root
	 */
	public static MFXStageDialog load(Class<?> clazz, String fxmlPath) throws IOException {
		AbstractMFXDialog root = FXMLLoader.load(clazz.getResource(fxmlPath));
		return new MFXStageDialog(root);
	}

	/**
	 * Same as {@link #load(Class, String)}, but in case of fail it will return null.
	 */
	public static MFXStageDialog loadOrNull(Class<?> clazz, String fxmlPath) {
		try {
			return load(clazz, fxmlPath);
		} catch (IOException ex) {
			return null;
		}
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public AbstractMFXDialog getContent() {
		return content.get();
	}

	/**
	 * Specifies the dialog' scene root node.
	 */
	public ObjectProperty<AbstractMFXDialog> contentProperty() {
		return content;
	}

	public void setContent(AbstractMFXDialog content) {
		this.content.set(content);
	}

	/**
	 * @return whether the dialog is draggable
	 */
	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
		initDraggable();
	}

	/**
	 * @return whether the dialog will be closed when pressing on the specified
	 * {@link #getOwnerNode()}
	 */
	public boolean isOverlayClose() {
		return overlayClose;
	}

	public void setOverlayClose(boolean overlayClose) {
		this.overlayClose = overlayClose;
		initOverlayClose();
	}

	/**
	 * @return the node which "owns" the dialog
	 */
	public Pane getOwnerNode() {
		return ownerNode;
	}

	public void setOwnerNode(Pane ownerNode) {
		if (this.ownerNode != null) {
			this.ownerNode.removeEventFilter(MouseEvent.MOUSE_PRESSED, overlayCloseHandler);
		}
		this.ownerNode = ownerNode;
		initOverlayClose();
	}

	public boolean isCenterInOwnerNode() {
		return centerInOwnerNode.get();
	}

	/**
	 * Specifies whether the dialog should be centered on the {@link #getOwnerNode()}
	 * when shown.
	 */
	public BooleanProperty centerInOwnerNodeProperty() {
		return centerInOwnerNode;
	}

	public void setCenterInOwnerNode(boolean centerInOwnerNode) {
		this.centerInOwnerNode.set(centerInOwnerNode);
	}

	public boolean isScrimOwner() {
		return scrimOwner.get();
	}

	/**
	 * Specifies whether to scrim the {@link #getOwnerNode()} when showing the dialog.
	 */
	public BooleanProperty scrimOwnerProperty() {
		return scrimOwner;
	}

	public void setScrimOwner(boolean scrimOwner) {
		this.scrimOwner.set(scrimOwner);
	}

	public double getScrimStrength() {
		return scrimStrength.get();
	}

	/**
	 * Specifies the strength(opacity, so values from 0.0 to 1.0) of the scrim effect, by default it is 0.5
	 */
	public DoubleProperty scrimStrengthProperty() {
		return scrimStrength;
	}

	public void setScrimStrength(double scrimStrength) {
		this.scrimStrength.set(scrimStrength);
	}

	/**
	 * @return the enum constant used to specify how to apply the scrim effect.
	 * You can have two owners, one is the stage owner(Window) and the other is the dialog owner(Pane).
	 * Sometimes it's better to apply the scrim to the window (for example the owner node would not allow to apply the
	 * scrim effect, for example AnchorPanes, VBoxes, HBoxes...), but you still want to center the dialog in the owner node.
	 * Setting this to {@link ScrimPriority#WINDOW} will tell the dialog to apply the effect to {@link #getOwner()},
	 * setting this to {@link ScrimPriority#NODE} will tell the dialog to apply the effect to {@link #getOwnerNode()}.
	 */
	public ScrimPriority getScrimPriority() {
		return scrimPriority;
	}

	/**
	 * Sets the enum constant used to specify how to apply the scrim effect.
	 *
	 * @see #getScrimPriority()
	 */
	public void setScrimPriority(ScrimPriority scrimPriority) {
		this.scrimPriority = scrimPriority;
	}
}
