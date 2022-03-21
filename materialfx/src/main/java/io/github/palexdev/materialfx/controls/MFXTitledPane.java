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
import io.github.palexdev.materialfx.beans.properties.functional.SupplierProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableObjectProperty;
import io.github.palexdev.materialfx.effects.Interpolators;
import io.github.palexdev.materialfx.enums.HeaderPosition;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.skins.MFXTitledPaneSkin;
import io.github.palexdev.materialfx.utils.AnimationUtils;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

/**
 * This is the implementation of a JavaFX's {@link TitledPane} remade from scratch to give it
 * more features, flexibility and of course a modern look.
 * <p></p>
 * Unlike the original pane, this one allows you to set whatever you want as header by setting
 * the {@link #headerSupplierProperty()}, just keep in mind that a {@code null} supplier or a {@code null} return value
 * won't be accepted. When using this constructor, {@link MFXTitledPane#MFXTitledPane(String, Node)}, the supplier
 * will be set to build a new {@link DefaultHeader} pane.
 * <p>
 * So, the {@link #titleProperty()}, is only relevant when using the default header supplier, or (of course) if
 * by making your own header you decide to use it in some way (a Label bound to the title for example).
 * <p></p>
 * There are also three other new features:
 * <p> - You can set the header position wherever you like, TOP/RIGHT/BOTTOM/LEFT
 * <p> - There's no need to use an accordion anymore, you can simply arrange multiple {@code MFXTitledPanes} in a
 * container, like a VBox or HBox, and use an {@link ExpandGroup} to achieve the same behavior
 * <p> - Unlike the original one, you can specify the duration of the expand/collapse animation
 */
public class MFXTitledPane extends Control {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-titled-pane";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXTitledPane.css");

	private final StringProperty title = new SimpleStringProperty();
	private final SupplierProperty<Node> headerSupplier = new SupplierProperty<>();
	private final ObjectProperty<Node> content = new SimpleObjectProperty<>();

	private final BooleanProperty expanded = new SimpleBooleanProperty() {
		@Override
		protected void invalidated() {
			boolean state = get();
			pseudoClassStateChanged(EXPANDED_PSEUDO_CLASS, state);
			pseudoClassStateChanged(COLLAPSED_PSEUDO_CLASS, !state);
		}
	};
	private final ObjectProperty<ExpandGroup> expandGroup = new SimpleObjectProperty<>();

	protected static final PseudoClass EXPANDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("expanded");
	protected static final PseudoClass COLLAPSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("collapsed");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXTitledPane() {
		this("Title", null);
	}

	public MFXTitledPane(String title, Node content) {
		defaultHeaderSupplier();
		setTitle(title);
		setContent(content);
		initialize();
	}

	public MFXTitledPane(Supplier<Node> headerSupplier, Node content) {
		setHeaderSupplier(headerSupplier);
		setContent(content);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);

		expandGroup.addListener((observable, oldGroup, newGroup) -> {
			if (newGroup != null && newGroup.getPanes().contains(this)) {
				if (oldGroup != null) oldGroup.getPanes().remove(this);
				newGroup.getPanes().add(this);
			} else if (newGroup == null) {
				oldGroup.getPanes().remove(this);
			}
		});
		expanded.addListener(invalidated -> {
			ExpandGroup eg = getExpandGroup();
			if (eg != null) {
				if (isExpanded()) {
					eg.setExpandedPane(this);
				} else if (eg.getExpandedPane() == this) {
					eg.clearExpandedPane();
				}
			}
		});
	}

	/**
	 * Resets the {@link #headerSupplierProperty()} to the default supplier.
	 */
	public void defaultHeaderSupplier() {
		setHeaderSupplier(DefaultHeader::new);
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty animated = new StyleableBooleanProperty(
			StyleableProperties.ANIMATED,
			this,
			"animated",
			true
	);

	private final StyleableObjectProperty<Duration> animationDuration = new StyleableObjectProperty<>(
			StyleableProperties.ANIMATION_DURATION,
			this,
			"animationDuration",
			Duration.millis(300)
	);

	private final StyleableBooleanProperty collapsible = new StyleableBooleanProperty(
			StyleableProperties.COLLAPSIBLE,
			this,
			"collapsible",
			true
	);

	private final StyleableObjectProperty<HeaderPosition> headerPos = new StyleableObjectProperty<>(
			StyleableProperties.HEADER_POS,
			this,
			"headerPos",
			HeaderPosition.TOP
	);

	public boolean isAnimated() {
		return animated.get();
	}

	/**
	 * Specifies whether to animate the expand/collapse transition.
	 */
	public StyleableBooleanProperty animatedProperty() {
		return animated;
	}

	public void setAnimated(boolean animated) {
		this.animated.set(animated);
	}

	public Duration getAnimationDuration() {
		return animationDuration.get();
	}

	/**
	 * Specifies the duration of the expand/collapse animation.
	 */
	public StyleableObjectProperty<Duration> animationDurationProperty() {
		return animationDuration;
	}

	public void setAnimationDuration(Duration animationDuration) {
		this.animationDuration.set(animationDuration);
	}

	public boolean isCollapsible() {
		return collapsible.get();
	}

	/**
	 * Specifies whether the pane can be collapsed.
	 */
	public StyleableBooleanProperty collapsibleProperty() {
		return collapsible;
	}

	public void setCollapsible(boolean collapsible) {
		this.collapsible.set(collapsible);
	}

	public HeaderPosition getHeaderPos() {
		return headerPos.get();
	}

	/**
	 * Specifies the position of the header node.
	 */
	public StyleableObjectProperty<HeaderPosition> headerPosProperty() {
		return headerPos;
	}

	public void setHeaderPos(HeaderPosition headerPos) {
		this.headerPos.set(headerPos);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXTitledPane> FACTORY = new StyleablePropertyFactory<>(Control.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXTitledPane, Boolean> ANIMATED =
				FACTORY.createBooleanCssMetaData(
						"-mfx-animated",
						MFXTitledPane::animatedProperty,
						true
				);

		private static final CssMetaData<MFXTitledPane, Duration> ANIMATION_DURATION =
				FACTORY.createDurationCssMetaData(
						"-mfx-animation-duration",
						MFXTitledPane::animationDurationProperty,
						Duration.millis(300)
				);

		private static final CssMetaData<MFXTitledPane, Boolean> COLLAPSIBLE =
				FACTORY.createBooleanCssMetaData(
						"-mfx-collapsible",
						MFXTitledPane::collapsibleProperty,
						true
				);

		private static final CssMetaData<MFXTitledPane, HeaderPosition> HEADER_POS =
				FACTORY.createEnumCssMetaData(
						HeaderPosition.class,
						"-mfx-pos",
						MFXTitledPane::headerPosProperty,
						HeaderPosition.TOP
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					Control.getClassCssMetaData(),
					ANIMATED, ANIMATION_DURATION, COLLAPSIBLE, HEADER_POS
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	@Override
	protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return getClassCssMetaData();
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXTitledPaneSkin(this);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public String getTitle() {
		return title.get();
	}

	/**
	 * Specifies the pane's title.
	 */
	public StringProperty titleProperty() {
		return title;
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public Supplier<Node> getHeaderSupplier() {
		return headerSupplier.get();
	}

	/**
	 * Specifies the {@link Supplier} used to build the header node.
	 * <p></p>
	 * The default one builds a new {@link DefaultHeader}.
	 */
	public SupplierProperty<Node> headerSupplierProperty() {
		return headerSupplier;
	}

	public void setHeaderSupplier(Supplier<Node> headerSupplier) {
		this.headerSupplier.set(headerSupplier);
	}

	public Node getContent() {
		return content.get();
	}

	/**
	 * Specifies the pane's content, can be null and changed at runtime.
	 */
	public ObjectProperty<Node> contentProperty() {
		return content;
	}

	public void setContent(Node content) {
		this.content.set(content);
	}

	public boolean isExpanded() {
		return expanded.get();
	}

	/**
	 * Specifies the expand state of the pane.
	 */
	public BooleanProperty expandedProperty() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded.set(expanded);
	}

	public ExpandGroup getExpandGroup() {
		return expandGroup.get();
	}

	/**
	 * Specifies the {@link ExpandGroup} this pane belongs to.
	 */
	public ObjectProperty<ExpandGroup> expandGroupProperty() {
		return expandGroup;
	}

	public void setExpandGroup(ExpandGroup expandGroup) {
		this.expandGroup.set(expandGroup);
	}

	//================================================================================
	// Default Header Class
	//================================================================================

	/**
	 * Default header used by {@link MFXTitledPane}s.
	 * <p></p>
	 * It basically consists in a {@link Label} which has its text bound to the pane's {@link #titleProperty()},
	 * and a {@link MFXFontIcon} (wrapped in a {@link MFXIconWrapper}) for the arrow, which is responsible
	 * for expanding/collapsing the pane.
	 * <p></p>
	 * This header is capable of rearranging itself when the {@link #headerPosProperty()}, to make things easier
	 * the layout is not manual but automatically managed, see {@link #initializeContainer()} for more info.
	 * <p>
	 * The icon also depends on the {@link #headerPosProperty()}, see {@link #iconForPosition()}.
	 */
	public class DefaultHeader extends StackPane {
		private final Label label;
		private final MFXIconWrapper wrapped;

		public DefaultHeader() {
			label = new Label();
			label.textProperty().bind(titleProperty());
			label.getStyleClass().add("header-label");
			label.setMaxWidth(Double.MAX_VALUE);
			HBox.setHgrow(label, Priority.ALWAYS);

			MFXFontIcon icon = new MFXFontIcon("mfx-chevron-left", 14);
			icon.descriptionProperty().bind(Bindings.createStringBinding(this::iconForPosition, headerPosProperty()));

			wrapped = new MFXIconWrapper(icon, 20).defaultRippleGeneratorBehavior();
			wrapped.setRotate(isExpanded() ? -180 : 0);
			NodeUtils.makeRegionCircular(wrapped);

			wrapped.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				if (event.getButton() != MouseButton.PRIMARY || (isExpanded() && !isCollapsible())) return;
				setExpanded(!isExpanded());
			});

			expanded.addListener((observable, oldValue, newValue) -> {
				double rotate = newValue ? -180 : 0;
				AnimationUtils.TimelineBuilder.build()
						.add(AnimationUtils.KeyFrames.of(200, wrapped.rotateProperty(), rotate, Interpolators.INTERPOLATOR_V1))
						.getAnimation()
						.play();
			});

			headerPos.addListener(invalidated -> initializeContainer());
			initializeContainer();

			getStyleClass().add("header-pane");
		}

		/**
		 * Responsible for rearranging the header's content when the {@link #headerPosProperty()} changes.
		 * <p></p>
		 * When it is LEFT or RIGHT, both the label and icon will be contained by a VBox,
		 * otherwise they will be contained by a HBox.
		 */
		private void initializeContainer() {
			HeaderPosition position = getHeaderPos();
			Node container;
			if (position == HeaderPosition.LEFT || position == HeaderPosition.RIGHT) {
				container = new VBox(label, wrapped);
				((VBox) container).setAlignment(Pos.CENTER);
			} else {
				container = new HBox(label, wrapped);
				((HBox) container).setAlignment(Pos.CENTER);
			}
			getChildren().setAll(container);
		}

		/**
		 * Responsible for changing the icon when {@link #headerPosProperty()} changes.
		 * <p>
		 * <p> - Case RIGHT: "mfx-chevron-left"
		 * <p> - Case BOTTOM: "mfx-chevron-down"
		 * <p> - Case LEFT: "mfx-chevron-right"
		 * <p> - Case TOP: "mfx-chevron-up"
		 */
		protected String iconForPosition() {
			HeaderPosition position = getHeaderPos();
			switch (position) {
				case RIGHT:
					return "mfx-chevron-left";
				case BOTTOM:
					return "mfx-chevron-down";
				case LEFT:
					return "mfx-chevron-right";
				case TOP:
				default:
					return "mfx-chevron-up";
			}
		}
	}
}
