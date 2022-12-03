package io.github.palexdev.mfxcore.base.properties.styleable;

import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.css.*;
import javafx.scene.text.Font;

import java.util.Arrays;

/**
 * Convenience {@link StyleableObjectProperty} for {@link Size}, settable via CSS thanks to
 * {@link SizeConverter}.
 */
public class StyleableSizeProperty extends StyleableObjectProperty<Size> {
	public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData) {
		super(cssMetaData);
	}

	public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Size initialValue) {
		super(cssMetaData, initialValue);
	}

	public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Object bean, String name) {
		super(cssMetaData, bean, name);
	}

	public StyleableSizeProperty(CssMetaData<? extends Styleable, Size> cssMetaData, Object bean, String name, Size initialValue) {
		super(cssMetaData, bean, name, initialValue);
	}

	public void setSize(double width, double height) {
		set(Size.of(width, height));
	}

	@Override
	public void applyStyle(StyleOrigin origin, Size v) {
		if (v == null) return;
		super.applyStyle(origin, v);
	}

	//================================================================================
	// Internal Classes
	//================================================================================

	/**
	 * Style converter implementation to make {@link Size} settable via CSS.
	 * The related property is {@link StyleableSizeProperty}.
	 * <p>
	 * For this to properly work, you must use a specific format. The converter expects a string value,
	 * with two double numbers which will be in order the width and the height for the new {@code Size}, so:
	 * <pre>
	 * {@code
	 * .node {
	 *     -fx-property-name: "100 30";
	 * }
	 * }
	 * </pre>
	 */
	public static class SizeConverter extends StyleConverter<String, Size> {

		// lazy, thread-safe instantiation
		private static class Holder {
			static final SizeConverter INSTANCE = new SizeConverter();
		}

		/**
		 * Gets the {@code SizeConverter} instance.
		 *
		 * @return the {@code SizeConverter} instance
		 */
		public static StyleConverter<String, Size> getInstance() {
			return Holder.INSTANCE;
		}

		private SizeConverter() {
			super();
		}

		@Override
		public Size convert(ParsedValue<String, Size> value, Font font) {
			try {
				double[] sizes = Arrays.stream(value.getValue().split(" "))
						.mapToDouble(Double::parseDouble)
						.toArray();
				return Size.of(sizes[0], sizes[1]);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
			}
		}

		@Override
		public String toString() {
			return "SizeConverter";
		}
	}
}
