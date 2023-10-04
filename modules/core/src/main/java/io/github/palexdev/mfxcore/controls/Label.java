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

package io.github.palexdev.mfxcore.controls;

import io.github.palexdev.mfxcore.base.properties.styleable.StyleableDoubleProperty;
import io.github.palexdev.mfxcore.base.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.mfxcore.observables.When;
import io.github.palexdev.mfxcore.utils.fx.StyleUtils;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Simple extension of {@link javafx.scene.control.Label} to set the wrapping width in a more intuitive way.
 * By default, only {@link Text} has the capability of specifying the wrapping width. For {@code Labels}
 * this should be handled by setting its max width and by enabling the {@link #wrapTextProperty()}. However, this may not
 * lead to the desired behavior, and it's not intuitive as well. Let me explain, by setting the max width, you are limiting
 * the label's width regardless the state of {@link #wrapTextProperty()}. However, there are cases in which you may want
 * to limit the width only if the text should be wrapped. And here's when this comes in handy. The property can be set by
 * code or CSS ('-fx-wrapping-width' property), and it's implemented by overriding the {@link #computeMaxWidth(double)}
 * method. If the text should be wrapped and the specified wrapping width is greater than 0, then the latter will be used
 * as the label's max width. Otherwise, use the default computation.
 * <p></p>
 * This also adds a new feature/workaround. In JavaFX Labels are composed by two nodes at max: the icon/graphic and the
 * text. For performance reasons, probably the text node is not added to the control until the text is not null and not empty.
 * A mechanism to detect the addition and retrieval of such node has been added, allowing custom text-based controls to
 * take full control of the text node itself rather than the label as a whole.
 * <p>
 * This allows to implement three other useful tricks:
 * <p> 1) 'Backport' the {@link Text#fontSmoothingTypeProperty()} here, allowing to set the antialiasing
 * method directly on the label. The default font smoothing type for this is set to {@link FontSmoothingType#LCD}.
 * <p> 2) A way to completely disable the text truncation by always showing the full text and removing the clip
 * <p> 3) A way to detect when the label's text is truncated, {@link #truncatedProperty()}
 */
public class Label extends javafx.scene.control.Label {
    //================================================================================
    // Properties
    //================================================================================
    protected Node textNode;
	private Consumer<Node> onSetTextNode = n -> {};

	private When<?> whenFDTE;
	private boolean forceDisableTextEllipsis = false;

	private final ReadOnlyBooleanWrapper truncated = new ReadOnlyBooleanWrapper(false);

    //================================================================================
    // Constructors
    //================================================================================
	public Label() {}

    public Label(String text) {
        super(text);
    }

    public Label(String text, Node graphic) {
        super(text, graphic);
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Responsible for setting the text node instance as well as running the user specified callback,
	 * {@link #onSetTextNode(Consumer)}, and invoking {@link #updateFDTE()}.
     */
    protected void setTextNode(Node textNode) {
        this.textNode = textNode;
		if (textNode instanceof Text) {
			Text tn = (Text) textNode;
			tn.fontSmoothingTypeProperty().bind(fontSmoothingTypeProperty());
            onSetTextNode.accept(textNode);
			updateFDTE();
			truncated.bind(tn.textProperty().map(s -> {
				if (forceDisableTextEllipsis) return false;
				return !Objects.equals(s, getText());
			}));
        }
    }

    /**
	 * This is responsible for completely removing the text truncation capability of the label. Runs only after the text
	 * node has been retrieved by {@link #setTextNode(Node)}.
	 * <p></p>
	 * <b>How does it work?</b>
	 * <p>
	 * First things first, how the JavaFX truncation mechanism works. There are effectively two separate text properties:
	 * one comes from the label itself, and the other is from the text node in its skin. The two are not bound. In fact,
	 * the property that specifies what's being shown by the label is the one from the text node (yeah, the one we forcefully
	 * retrieve here). When the text is truncated, the property from the label will return the full text, instead the one from
	 * the text node will return the truncated text.
	 * <p>
	 * Knowing this, we use a {@link When} construct (so a listener) on the text node's property so that every time it
	 * changes (it is truncated) we set it back to the full string. Yes, it's a brute force approach, but as far as I know
	 * it's the only way, you know how it is JavaFX... private, final, immutable, boring...
	 * <p>
	 * Additionally, the listener is also responsible for removing the clip applied to the text node. It appears that
	 * the text is not only truncated but also clipped for some reason, so restoring the full text may not be enough in some
	 * cases.
     */
	protected void updateFDTE() {
		if (!forceDisableTextEllipsis) {
			if (whenFDTE != null) {
				whenFDTE.dispose();
				whenFDTE = null;
			}
			return;
		}

		Text textNode = (Text) this.textNode;
		if (textNode == null || whenFDTE != null) return;
		whenFDTE = When.onChanged(textNode.textProperty())
			.then((o, n) -> {
				textNode.setClip(null);
				textNode.setText(getText());
			})
			.invalidating(textNode.clipProperty())
			.executeNow()
			.listen();
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected double computeMaxWidth(double height) {
        double maxW = super.computeMaxWidth(height);
        double ww = getWrappingWidth();
        if (isWrapText() && ww > 0) return Math.min(maxW, ww);
        return maxW;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LabelSkin(this) {
            @Override
            protected void updateChildren() {
                super.updateChildren();
                if (textNode != null) return;

                if (getChildren().size() == 1 && getGraphic() == null) {
                    setTextNode(getChildren().get(0));
                } else if (getChildren().size() > 1) {
                    setTextNode(getChildren().get(1));
                }
            }
        };
    }

    //================================================================================
    // Styleable Properties
    //================================================================================
    private final StyleableObjectProperty<FontSmoothingType> fontSmoothingType = new StyleableObjectProperty<>(
        StyleableProperties.FONT_SMOOTHING_TYPE,
        this,
        "fontSmoothingType",
        FontSmoothingType.LCD
    );

    private final StyleableDoubleProperty wrappingWidth = new StyleableDoubleProperty(
        StyleableProperties.WRAPPING_WIDTH,
        this,
        "wrappingWidth",
        USE_COMPUTED_SIZE
    );

    public FontSmoothingType getFontSmoothingType() {
        return fontSmoothingType.get();
    }

    /**
     * Specifies the font smoothing algorithm for the text node of this label, see {@link FontSmoothingType} and
     * {@link Text#fontSmoothingTypeProperty()}.
     * <p>
     * Can be set in CSS via the property: '-fx-font-smoothing-type'.
     */
    public StyleableObjectProperty<FontSmoothingType> fontSmoothingTypeProperty() {
        return fontSmoothingType;
    }

    public void setFontSmoothingType(FontSmoothingType fontSmoothingType) {
        this.fontSmoothingType.set(fontSmoothingType);
    }

    public double getWrappingWidth() {
        return wrappingWidth.get();
    }

    /**
	 * Allows specifying a maximum width for the label that is applied only when it is greater than 0 and
     * {@link #wrapTextProperty()} set to true.
     * <p>
     * Can be set in CSS via the property: '-fx-wrapping-width'.
     */
    public StyleableDoubleProperty wrappingWidthProperty() {
        return wrappingWidth;
    }

    public void setWrappingWidth(double wrappingWidth) {
        this.wrappingWidth.set(wrappingWidth);
    }

    //================================================================================
    // CssMetaData
    //================================================================================
    private static class StyleableProperties {
        private static final StyleablePropertyFactory<Label> FACTORY = new StyleablePropertyFactory<>(javafx.scene.control.Label.getClassCssMetaData());
        private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

        private static final CssMetaData<Label, FontSmoothingType> FONT_SMOOTHING_TYPE =
            FACTORY.createEnumCssMetaData(
                FontSmoothingType.class,
                "-fx-font-smoothing-type",
                Label::fontSmoothingTypeProperty,
                FontSmoothingType.LCD
            );

        private static final CssMetaData<Label, Number> WRAPPING_WIDTH =
            FACTORY.createSizeCssMetaData(
                "-fx-wrapping-width",
                Label::wrappingWidthProperty,
                USE_COMPUTED_SIZE
            );

        static {
            cssMetaDataList = StyleUtils.cssMetaDataList(
                javafx.scene.control.Label.getClassCssMetaData(),
                FONT_SMOOTHING_TYPE, WRAPPING_WIDTH
            );
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.cssMetaDataList;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

	//================================================================================
	// Getters/Setters
	//================================================================================

	/**
	 * Null-safe getter for retrieving the instance of the text node for this label.
	 */
	public Optional<Node> getTextNode() {
		return Optional.ofNullable(textNode);
	}

	/**
	 * Sets the callback that executes when the text node is detected and stored.
	 */
	public void onSetTextNode(Consumer<Node> action) {
		this.onSetTextNode = action;
	}

	public boolean isForceDisableTextEllipsis() {
		return forceDisableTextEllipsis;
	}

	/**
	 * Enables/disables the listener responsible for completely removing the text truncation capabilities from the label.
	 *
	 * @see #updateFDTE()
	 */
	public void setForceDisableTextEllipsis(boolean forceDisableTextEllipsis) {
		this.forceDisableTextEllipsis = forceDisableTextEllipsis;
		updateFDTE();
	}

	public boolean isTruncated() {
		return truncated.get();
	}

	/**
	 * This property allows user's to observe the text property that corresponds to the visualized string, to check
	 * whether the full text is truncated or not
	 */
	public ReadOnlyBooleanProperty truncatedProperty() {
		return truncated.getReadOnlyProperty();
	}
}
