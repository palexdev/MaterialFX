import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;

import java.util.List;

public class MFXLabelRework extends Labeled {
	private final String STYLE_CLASS = "mfx-label";
	private final String STYLESHEET = "";

	private final StringProperty floatingText = new SimpleStringProperty("");
	private final ObjectProperty<Node> trailingIcon = new SimpleObjectProperty<>();

	protected static final PseudoClass EDITING_PSEUDO_CLASS = PseudoClass.getPseudoClass("editing");
	private final BooleanProperty editing = new SimpleBooleanProperty();

	public MFXLabelRework() {
		this("");
	}

	public MFXLabelRework(String text) {
		this(text, null);
	}

	public MFXLabelRework(String text, String floatingText) {
		this(text, floatingText, null);
	}

	public MFXLabelRework(String text, String floatingText, Node graphic) {
		super(text, graphic);
		setFloatingText(floatingText);
		initialize();
	}

	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		editing.addListener(invalidated -> pseudoClassStateChanged(EDITING_PSEUDO_CLASS, editing.get()));
	}

	public String getFloatingText() {
		return floatingText.get();
	}

	public StringProperty floatingTextProperty() {
		return floatingText;
	}

	public void setFloatingText(String floatingText) {
		this.floatingText.set(floatingText);
	}

	public Node getTrailingIcon() {
		return trailingIcon.get();
	}

	public ObjectProperty<Node> trailingIconProperty() {
		return trailingIcon;
	}

	public void setTrailingIcon(Node trailingIcon) {
		this.trailingIcon.set(trailingIcon);
	}

	public boolean isEditing() {
		return editing.get();
	}

	public BooleanProperty editingProperty() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing.set(editing);
	}

	private final StyleableBooleanProperty editable = new SimpleStyleableBooleanProperty(
			StyleableProperties.EDITABLE,
			this,
			"editable",
			false
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	private final StyleableDoubleProperty gap = new SimpleStyleableDoubleProperty(
			StyleableProperties.GAP,
			this,
			"gap",
			3.0
	) {
		@Override
		public StyleOrigin getStyleOrigin() {
			return StyleOrigin.USER_AGENT;
		}
	};

	public boolean isEditable() {
		return editable.get();
	}

	public StyleableBooleanProperty editableProperty() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable.set(editable);
	}

	public double getGap() {
		return gap.get();
	}

	public StyleableDoubleProperty gapProperty() {
		return gap;
	}

	public void setGap(double gap) {
		this.gap.set(gap);
	}

	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXLabelRework> FACTORY = new StyleablePropertyFactory<>(Labeled.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXLabelRework, Boolean> EDITABLE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-editable",
						MFXLabelRework::editableProperty,
						false
				);

		private static final CssMetaData<MFXLabelRework, Number> GAP =
				FACTORY.createSizeCssMetaData(
						"-mfx-gap",
						MFXLabelRework::gapProperty,
						3.0
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Labeled.getClassCssMetaData(),
					EDITABLE, GAP
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getControlCssMetaDataList() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXLabelSkinRework(this);
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXLabelRework.getControlCssMetaDataList();
	}
}
