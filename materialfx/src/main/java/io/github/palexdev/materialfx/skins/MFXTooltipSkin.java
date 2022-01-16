package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PopupPositionBean;
import io.github.palexdev.materialfx.controls.MFXTooltip;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.WindowEvent;

/**
 * Skin associated with every {@link MFXTooltip} by default.
 * <p></p>
 * This skin is composed of a top container, which is a {@link StackPane},
 * ans a {@link Label} to show the tooltip's text;
 */
public class MFXTooltipSkin implements Skin<MFXTooltip> {
	//================================================================================
	// Properties
	//================================================================================
	private MFXTooltip tooltip;
	private final StackPane container;
	private final Label label;
	private final Scale scale;

	private Animation animation;
	private EventHandler<WindowEvent> initHandler;
	private EventHandler<WindowEvent> closeHandler;

	public MFXTooltipSkin(MFXTooltip tooltip) {
		this.tooltip = tooltip;

		label = new Label();
		label.setWrapText(true);
		label.textProperty().bind(tooltip.textProperty());
		label.graphicProperty().bind(tooltip.iconProperty());

		scale = new Scale(0.1, 0.1, 0, 0);
		container = new StackPane(label) {
			@Override
			public String getUserAgentStylesheet() {
				return tooltip.getUserAgentStylesheet();
			}
		};
		container.getStyleClass().add("container");
		container.getTransforms().addAll(scale);
		tooltip.setContent(container);

		initHandler = event -> {
			init();
			if (tooltip.isAnimated()) {
				animation = tooltip.getAnimationProvider().apply(container, scale);
				animation.play();
			} else {
				scale.setX(1);
				scale.setY(1);
				container.setOpacity(1);
			}
		};

		closeHandler = event -> {
			container.setOpacity(0.0);
			scale.setX(0.1);
			scale.setY(0.1);
		};

		tooltip.addEventHandler(WindowEvent.WINDOW_SHOWN, initHandler);
		tooltip.addEventHandler(WindowEvent.WINDOW_HIDDEN, closeHandler);
		Bindings.bindContent(container.getStylesheets(), tooltip.getStyleSheets());
	}

	/**
	 * Positions the popup.
	 */
	protected void init() {
		container.setOpacity(0.0);

		PopupPositionBean position = tooltip.getPosition();
		if (position == null) return;

		if (position.getAlignment() != null) {
			double containerW = container.prefWidth(-1);
			double containerH = container.prefHeight(-1);

			HPos hPos = position.getHPos();
			VPos vPos = position.getVPos();
			double xOffset = position.getXOffset();
			double yOffset = position.getYOffset();

			double px = hPos == HPos.RIGHT ? xOffset : containerW + xOffset;
			double py = vPos == VPos.BOTTOM ? yOffset : containerH + yOffset;
			scale.setPivotX(px);
			scale.setPivotY(py);
		}
		tooltip.reposition();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	public MFXTooltip getSkinnable() {
		return tooltip;
	}

	@Override
	public Node getNode() {
		return container;
	}

	@Override
	public void dispose() {
		animation.stop();
		animation = null;
		tooltip.removeEventHandler(WindowEvent.WINDOW_SHOWN, initHandler);
		tooltip.removeEventHandler(WindowEvent.WINDOW_HIDDEN, closeHandler);
		initHandler = null;
		closeHandler = null;
		tooltip = null;
	}
}
