package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.controls.MFXCircleToggleNode;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.effects.ripple.MFXCircleRippleGenerator;
import io.github.palexdev.materialfx.enums.TextPosition;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * This is the default skin for every {@link MFXCircleToggleNode}.
 */
public class MFXCircleToggleNodeSkin extends SkinBase<MFXCircleToggleNode> {
	private final VBox topContainer;
	private final StackPane rippleContainer;
	private final Circle circle;
	private final MFXTextField label;
	private final MFXCircleRippleGenerator rippleGenerator;

	public MFXCircleToggleNodeSkin(MFXCircleToggleNode toggleNode) {
		super(toggleNode);
		
		circle = new Circle();
		circle.getStyleClass().add("circle");
		circle.radiusProperty().bind(toggleNode.sizeProperty());
		
		label = new MFXTextField() {
			@Override
			public String getUserAgentStylesheet() {
				return toggleNode.getUserAgentStylesheet();
			}
		};
		label.alignmentProperty().bind(toggleNode.alignmentProperty());
		label.fontProperty().bind(toggleNode.fontProperty());
		label.graphicTextGapProperty().bind(toggleNode.graphicTextGapProperty());
		label.textProperty().bind(toggleNode.textProperty());
		label.leadingIconProperty().bind(toggleNode.labelLeadingIconProperty());
		label.trailingIconProperty().bind(toggleNode.labelTrailingIconProperty());
		label.setEditable(false);
		label.setSelectable(false);

		rippleContainer = new StackPane();
		
		rippleGenerator = new MFXCircleRippleGenerator(rippleContainer);
		rippleGenerator.setMouseTransparent(true);
		rippleGenerator.setManaged(false);

		Node graphic = toggleNode.getGraphic();
		if (graphic != null) {
			graphic.setMouseTransparent(true);
			rippleContainer.getChildren().setAll(circle, rippleGenerator, graphic);
		} else {
			rippleContainer.getChildren().setAll(circle, rippleGenerator);
		}
		
		topContainer = new VBox();
		topContainer.setAlignment(Pos.TOP_CENTER);
		topContainer.spacingProperty().bind(toggleNode.gapProperty());

		if (toggleNode.getTextPosition() == TextPosition.TOP) {
			topContainer.getChildren().setAll(label, rippleContainer);
		} else {
			topContainer.getChildren().setAll(rippleContainer, label);
		}
		
		setupRippleGenerator();
		addListeners();
		getChildren().setAll(topContainer);
	}

	private void addListeners() {
		MFXCircleToggleNode toggleNode = getSkinnable();

		toggleNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (!NodeUtils.inHierarchy(event, circle)) {
				return;
			}

			Node leadingIcon = label.getLeadingIcon();
			Node trailingIcon = label.getTrailingIcon();

			if (leadingIcon != null &&
					NodeUtils.inHierarchy(event, leadingIcon) ||
					trailingIcon != null &&
							NodeUtils.inHierarchy(event, trailingIcon) ||
					NodeUtils.inHierarchy(event, label)) {
				return;
			}

			toggleNode.setSelected(!toggleNode.isSelected());
			rippleGenerator.generateRipple(event);
		});

		toggleNode.graphicProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				rippleContainer.getChildren().remove(oldValue);
			}
			if (newValue != null) {
				newValue.setMouseTransparent(true);
				rippleContainer.getChildren().add(newValue);
			}
		});

		toggleNode.textPositionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == TextPosition.TOP) {
				topContainer.getChildren().setAll(label, rippleContainer);
			} else {
				topContainer.getChildren().setAll(rippleContainer, label);
			}
		});
	}

	/**
	 * Sets up the ripple generator.
	 */
	protected void setupRippleGenerator() {
		rippleGenerator.setAnimateBackground(false);
		rippleGenerator.setAnimationSpeed(1.3);
		rippleGenerator.setClipSupplier(() -> {
			Circle clip = new Circle();
			clip.radiusProperty().bind(circle.radiusProperty());
			clip.centerXProperty().bind(Bindings.createDoubleBinding(
					() -> circle.getBoundsInParent().getCenterX(),
					circle.boundsInParentProperty()
			));
			clip.centerYProperty().bind(Bindings.createDoubleBinding(
					() -> circle.getBoundsInParent().getCenterY(),
					circle.boundsInParentProperty()
			));
			return clip;
		});
		rippleGenerator.setRipplePositionFunction(event -> PositionBean.of(event.getX(), event.getY()));
		rippleGenerator.rippleRadiusProperty().bind(circle.radiusProperty().add(5));
	}

	//================================================================================
	// Override Methods
	//================================================================================
	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}
}
