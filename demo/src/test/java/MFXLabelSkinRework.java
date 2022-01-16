import io.github.palexdev.materialfx.controls.BoundLabel;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class MFXLabelSkinRework extends SkinBase<MFXLabelRework> {
	private final BoundLabel text;
	private final Text floatingText;

	private final TextField editor;
	private boolean commitChanges = false;

	public MFXLabelSkinRework(MFXLabelRework label) {
		super(label);

		text = new BoundLabel(label);
		text.graphicProperty().unbind();
		text.setGraphic(null);
		text.setMaxWidth(Double.MAX_VALUE);

		floatingText = new Text();
		floatingText.textProperty().bind(label.floatingTextProperty());

		editor = new TextField();
		editor.getStyleClass().add("editor");
		editor.setManaged(false);
		editor.setVisible(false);

		getChildren().addAll(text, floatingText, editor);
		if (label.getGraphic() != null) getChildren().add(0, label.getGraphic());
		if (label.getTrailingIcon() != null) getChildren().add(0, label.getTrailingIcon());
		addListeners();
	}

	private void addListeners() {
		MFXLabelRework label = getSkinnable();

		label.graphicProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) getChildren().remove(oldValue);
			if (newValue != null) getChildren().add(0, newValue);
		});
		label.trailingIconProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) getChildren().remove(oldValue);
			if (newValue != null) getChildren().remove(newValue);
		});
		label.gapProperty().addListener(invalidated -> label.requestLayout());

		label.editingProperty().addListener((observable, oldValue, newValue) -> showEditor(newValue));
		label.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (!label.isEditable()) return;
			if (event.getClickCount() >= 2 && event.getClickCount() % 2 == 0 && !label.isEditing()) {
				label.setEditing(true);
			}
		});

		label.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER) {
				commitChanges = true;
				label.setEditing(false);
			} else if (event.getCode() == KeyCode.ESCAPE) {
				commitChanges = false;
				label.setEditing(false);
			}
		});
		editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue && label.isEditing()) {
				commitChanges = true;
				label.setEditing(false);
			}
		});

	}

	protected void showEditor(boolean show) {
		MFXLabelRework label = getSkinnable();
		if (show) {
			text.setVisible(false);
			editor.setText(label.getText());
			editor.setVisible(true);
			editor.requestFocus();
			editor.positionCaret(editor.getText().length());
		} else {
			if (commitChanges) label.setText(editor.getText());
			editor.setVisible(false);
			text.setVisible(true);
		}
		commitChanges = false;
	}

	@Override
	protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return topInset +
				floatingText.prefHeight(-1) +
				getSkinnable().getGap() +
				text.prefHeight(-1) +
				bottomInset;
	}

	@Override
	protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		MFXLabelRework label = getSkinnable();
		Node graphic = label.getGraphic();
		Node trailing = label.getTrailingIcon();
		return leftInset +
				((graphic != null) ? graphic.prefWidth(-1) + label.getGraphicTextGap() : 0) +
				Math.max(floatingText.prefWidth(-1), text.prefWidth(-1)) +
				((trailing != null) ? label.getGraphicTextGap() + trailing.prefWidth(-1) : 0) +
				rightInset;
	}

	@Override
	protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefWidth(-1);
	}

	@Override
	protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
		return getSkinnable().prefHeight(-1);
	}

	@Override
	protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
		MFXLabelRework label = getSkinnable();
		text.autosize();
		floatingText.autosize();

		Node graphic = label.getGraphic();
		Node trailingIcon = label.getTrailingIcon();

		// Graphic
		double graphicW = 0;
		double graphicH;
		double graphicX = snappedLeftInset();
		double graphicY;
		if (graphic != null) {
			graphic.autosize();
			graphicW = graphic.prefWidth(-1);
			graphicH = graphic.prefHeight(-1);
			graphicY = (contentHeight / 2) - (graphicH / 2) + snappedTopInset();
			graphic.relocate(snapPositionX(graphicX), snapPositionY(graphicY));
		}

		// Texts and Editor
		double floatW = floatingText.prefWidth(-1);
		double floatH = floatingText.prefHeight(-1);
		double floatX = graphicX + graphicW + ((graphic != null) ? label.getGraphicTextGap() : 0);
		double floatY = snappedTopInset();
		floatingText.relocate(snapPositionX(floatX), snapPositionY(floatY));

		double textW = text.prefWidth(-1);
		double textY = floatY + label.getGap() + floatH;
		text.relocate(snapPositionX(floatX), snapPositionY(textY));

		double editorH = text.prefHeight(-1);
		editor.resizeRelocate(snapPositionX(floatX), snapPositionY(textY), textW, editorH);

		// Trailing
		if (trailingIcon != null) {
			trailingIcon.autosize();
			double trailingH = trailingIcon.prefHeight(-1);
			double trailingX = floatX + Math.max(textW, floatW) + label.getGraphicTextGap();
			double trailingY = (contentHeight / 2) - (trailingH / 2) + snappedTopInset();
			trailingIcon.relocate(snapPositionX(trailingX), snapPositionY(trailingY));
		}
	}
}
