package prototipes;

import io.github.palexdev.materialfx.controls.BoundLabel;
import io.github.palexdev.materialfx.effects.ConsumerTransition;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.factories.InsetsFactory;
import io.github.palexdev.materialfx.utils.AnimationUtils.KeyFrames;
import io.github.palexdev.materialfx.utils.AnimationUtils.ParallelBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

public class FloatingTextPrototype extends Labeled {
	private final StringProperty promptText = new SimpleStringProperty("Floating Text");

	public FloatingTextPrototype() {
		this("");
	}

	public FloatingTextPrototype(String text) {
		this(text, null);
	}

	public FloatingTextPrototype(String text, Node graphic) {
		super(text, graphic);
		initialize();
	}

	private void initialize() {
		setPadding(InsetsFactory.of(5, 3, 5, 3));
	}

	public String getPromptText() {
		return promptText.get();
	}

	public StringProperty promptTextProperty() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText.set(promptText);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new FloatingTextPrototypeSkin(this);
	}
}

class FloatingTextPrototypeSkin extends SkinBase<FloatingTextPrototype> {
	private final VBox container;
	private final BoundLabel text;
	private final Label promptText;

	private final double scaleMultiplier = 0.85;
	private final Scale scale = new Scale(1, 1, 0, 0);

	private boolean floating = true;

	public FloatingTextPrototypeSkin(FloatingTextPrototype control) {
		super(control);

		text = new BoundLabel(control);
		text.setStyle("-fx-border-color: red");

		promptText = new Label("Look! It's floating!");
		promptText.setStyle("-fx-border-color: blue");
		promptText.getTransforms().add(scale);

		container = new VBox(promptText, text);
		container.setAlignment(Pos.CENTER_LEFT);
		getChildren().setAll(container);

		setBehavior();
	}

	private void setBehavior() {
		FloatingTextPrototype control = getSkinnable();

		control.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (floating) {
				double promptH = promptText.getHeight();
				double y = (control.getHeight() / 2) - (promptH / 2) + snappedTopInset();
				double translateY = y - promptText.getBoundsInParent().getMinY();
				double mul = scale.getY();
				ParallelBuilder.build()
						.add(
								ConsumerTransition.of(frac -> promptText.setTranslateY((translateY / 2) * frac * mul), 250)
										.setOnFinishedFluent(end -> promptText.setTranslateY(snapPositionY(promptText.getTranslateY())))
										.setInterpolatorFluent(Interpolators.INTERPOLATOR_V1)
						)
						.add(
								KeyFrames.of(250, scale.xProperty(), 1, Interpolators.INTERPOLATOR_V1),
								KeyFrames.of(250, scale.yProperty(), 1, Interpolators.INTERPOLATOR_V1)
						)
						.setOnFinished(end -> System.out.println("Y: " + promptText.getBoundsInParent().getMinY()))
						.getAnimation().play();
				floating = false;
			} else {
				double initialTranslateY = promptText.getTranslateY();
				ParallelBuilder.build()
						.add(
								ConsumerTransition.of(frac -> promptText.setTranslateY(initialTranslateY - (initialTranslateY * frac)), 250)
										.setInterpolatorFluent(Interpolators.INTERPOLATOR_V1)
						)
						.add(
								KeyFrames.of(250, scale.xProperty(), scaleMultiplier, Interpolators.INTERPOLATOR_V1),
								KeyFrames.of(250, scale.yProperty(), scaleMultiplier, Interpolators.INTERPOLATOR_V1)
						)
						.setOnFinished(end -> System.out.println("Scaled Y: " + promptText.getBoundsInParent().getMinY()))
						.getAnimation().play();
				floating = true;
			}
		});
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}
}
