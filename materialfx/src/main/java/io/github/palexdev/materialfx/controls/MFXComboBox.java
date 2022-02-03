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
import io.github.palexdev.materialfx.beans.properties.EventHandlerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.BiFunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.beans.properties.styleable.StyleableBooleanProperty;
import io.github.palexdev.materialfx.controls.base.MFXCombo;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.font.MFXFontIcon;
import io.github.palexdev.materialfx.i18n.I18N;
import io.github.palexdev.materialfx.selection.ComboBoxSelectionModel;
import io.github.palexdev.materialfx.skins.MFXComboBoxSkin;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import io.github.palexdev.materialfx.utils.StyleablePropertiesUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
import io.github.palexdev.materialfx.validation.MFXValidator;
import io.github.palexdev.virtualizedfx.beans.NumberRange;
import io.github.palexdev.virtualizedfx.cell.Cell;
import io.github.palexdev.virtualizedfx.utils.ListChangeHelper;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A new, completely remade from scratch {@code ComboBox} for JavaFX.
 * <p>
 * A combo box is basically a text field which shows a menu of items and allows to select
 * them and set the text accordingly.
 * <p>
 * That's why my implementation extends {@link MFXTextField} and implements {@link MFXCombo}.
 * <p>
 * The major features of this new combo are:
 * <p> - Floating text (inherited from {@link MFXTextField})
 * <p> - Allows to fully control the popup (offset, alignment)
 * <p> - Automatically handles selection when the item's list is modified
 * <p> - Allows to set the combo as editable or not, and in case of changed text
 * to commit the change (pressing enter by default) and specify how to treat the
 * typed text, or cancel the change (pressing Ctrl+Shift+Z by default).
 * <p> - Also adds a new PseudoClass that activates when the popup opens
 */
public class MFXComboBox<T> extends MFXTextField implements MFXCombo<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-combo-box";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXComboBox.css");

	private final ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(false);
	private final ObjectProperty<Alignment> popupAlignment = new SimpleObjectProperty<>(Alignment.of(HPos.CENTER, VPos.BOTTOM));
	private final DoubleProperty popupOffsetX = new SimpleDoubleProperty(0);
	private final DoubleProperty popupOffsetY = new SimpleDoubleProperty(3);
	private final BiFunctionProperty<Node, Boolean, Animation> animationProvider = new BiFunctionProperty<>();

	private final ObjectProperty<T> value = new SimpleObjectProperty<>();
	private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>();
	private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>();
	private final ComboBoxSelectionModel<T> selectionModel = new ComboBoxSelectionModel<>(items);
	private final FunctionProperty<T, Cell<T>> cellFactory = new FunctionProperty<>(t -> new MFXComboBoxCell<>(this, t));
	private final ListChangeListener<? super T> itemsChanged = this::itemsChanged;
	private final ConsumerProperty<String> onCommit = new ConsumerProperty<>();
	private final ConsumerProperty<String> onCancel = new ConsumerProperty<>();

	protected static final PseudoClass POPUP_OPEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("popup");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXComboBox() {
		this(FXCollections.observableArrayList());
	}

	public MFXComboBox(ObservableList<T> items) {
		setItems(items);
		initialize();
	}

	//================================================================================
	// Methods
	//================================================================================

	/**
	 * Sets the style class, the default icon to open the popup and its animation, the
	 * default {@link StringConverter} and some needed listeners.
	 */
	private void initialize() {
		getStyleClass().add(STYLE_CLASS);
		setAllowEdit(false);
		setSelectable(false);

		// Icon
		MFXIconWrapper icon = new MFXIconWrapper("mfx-caret-down", 12, 24);
		icon.rippleGeneratorBehavior(event -> {
			double x = event != null ? event.getX() : icon.getSize() / 2;
			double y = event != null ? event.getY() : icon.getSize() / 2;
			return PositionBean.of(x, y);
		});
		NodeUtils.makeRegionCircular(icon);
		setTrailingIcon(icon);
		icon.getStyleClass().add("caret");

		// Default animation
		setAnimationProvider((node, showing) -> {
			RotateTransition transition = new RotateTransition(Duration.millis(200), node);
			transition.setInterpolator(Interpolator.EASE_OUT);
			transition.setToAngle(showing ? 180 : 0);
			return transition;
		});

		// Default converter
		setConverter(FunctionalStringConverter.to(t -> t != null ? t.toString() : ""));

		showing.addListener(invalidated -> pseudoClassStateChanged(POPUP_OPEN_PSEUDO_CLASS, showing.get()));

		items.addListener((observable, oldValue, newValue) -> {
			oldValue.removeListener(itemsChanged);
			newValue.addListener(itemsChanged);
		});
		getItems().addListener(this::itemsChanged);
	}

	@Override
	public void defaultContextMenu() {
		MFXContextMenuItem selectFirst = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-first-page", 16))
				.setText(I18N.getOrDefault("comboBox.contextMenu.selectFirst"))
				.setOnAction(event -> selectFirst())
				.get();

		MFXContextMenuItem selectNext = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-next", 18))
				.setText(I18N.getOrDefault("comboBox.contextMenu.selectNext"))
				.setOnAction(event -> selectNext())
				.get();

		MFXContextMenuItem selectPrevious = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-back", 18))
				.setText(I18N.getOrDefault("comboBox.contextMenu.selectPrevious"))
				.setOnAction(event -> selectPrevious())
				.get();

		MFXContextMenuItem selectLast = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-last-page", 16))
				.setText(I18N.getOrDefault("comboBox.contextMenu.selectLast"))
				.setOnAction(event -> selectLast())
				.get();

		MFXContextMenuItem resetSelection = MFXContextMenuItem.Builder.build()
				.setIcon(new MFXFontIcon("mfx-x", 16))
				.setText(I18N.getOrDefault("comboBox.contextMenu.clearSelection"))
				.setOnAction(event -> clearSelection())
				.get();

		contextMenu = MFXContextMenu.Builder.build(this)
				.addItems(selectFirst, selectNext, selectPrevious, selectLast)
				.addLineSeparator()
				.addItem(resetSelection)
				.installAndGet();
	}

	@Override
	public void show() {
		showing.set(true);
	}

	@Override
	public void hide() {
		showing.set(false);
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * By default this implementation calls the specified {@link #onCommitProperty()} consumer
	 * to perform an action on commit. So, instead of overriding the method you can easily modify
	 * its behavior by changing the consumer.
	 */
	@Override
	public void commit(String text) {
		if (getOnCommit() != null) {
			getOnCommit().accept(text);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p></p>
	 * By default this implementation calls the specified {@link #onCancelProperty()} consumer
	 * to perform an action on cancel. So, instead of overriding the method you can easily modify
	 * its behavior by changing the consumer.
	 */
	@Override
	public void cancel(String text) {
		if (getOnCancel() != null) {
			getOnCancel().accept(text);
		}
	}

	/**
	 * Responsible for updating the selection when the items list changes.
	 */
	protected void itemsChanged(ListChangeListener.Change<? extends T> change) {
		if (getSelectedIndex() == -1) return;

		if (change.getList().isEmpty()) {
			clearSelection();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.processChange(change, NumberRange.of(0, Integer.MAX_VALUE));
		Set<Integer> indexes = new HashSet<>();
		indexes.add(getSelectedIndex());
		ListChangeProcessor updater = new ListChangeProcessor(indexes);
		c.processReplacement((changed, removed) -> {
			int selected = getSelectedIndex();
			if (changed.contains(selected) || removed.contains(selected)) {
				selectItem(getItems().get(selected));
			}
		});
		c.processAddition((from, to, added) -> {
			updater.computeAddition(added.size(), from);
			selectIndex(updater.getIndexes().toArray(new Integer[0])[0]);
		});
		c.processRemoval((from, to, removed) -> {
			updater.computeRemoval(removed, from);
			int index = NumberUtils.clamp(updater.getIndexes().toArray(new Integer[0])[0], 0, getItems().size() - 1);
			selectIndex(index);
		});

		setValue(getSelectedItem());
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXComboBoxSkin<>(this, boundField);
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
		return MFXComboBox.getClassCssMetaData();
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}

	//================================================================================
	// Delegate Methods
	//================================================================================

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectFirst()}.
	 */
	public void selectFirst() {
		selectionModel.selectFirst();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectNext()}.
	 */
	public void selectNext() {
		selectionModel.selectNext();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectPrevious()}.
	 */
	public void selectPrevious() {
		selectionModel.selectPrevious();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectLast()}.
	 */
	public void selectLast() {
		selectionModel.selectLast();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#clearSelection()}.
	 */
	public void clearSelection() {
		selectionModel.clearSelection();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectIndex(int)}.
	 */
	public void selectIndex(int index) {
		selectionModel.selectIndex(index);
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectItem(Object)}.
	 */
	public void selectItem(T item) {
		selectionModel.selectItem(item);
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#getSelectedIndex()}.
	 */
	public int getSelectedIndex() {
		return selectionModel.getSelectedIndex();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectedIndexProperty()}.
	 */
	public ReadOnlyIntegerProperty selectedIndexProperty() {
		return selectionModel.selectedIndexProperty();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#getSelectedItem()}.
	 */
	public T getSelectedItem() {
		return selectionModel.getSelectedItem();
	}

	/**
	 * Delegate for {@link ComboBoxSelectionModel#selectedItemProperty()}.
	 */
	public ReadOnlyObjectProperty<T> selectedItemProperty() {
		return selectionModel.selectedItemProperty();
	}

	//================================================================================
	// Validation
	//================================================================================
	@Override
	public MFXValidator getValidator() {
		return validator;
	}

	//================================================================================
	// Styleable Properties
	//================================================================================
	private final StyleableBooleanProperty scrollOnOpen = new StyleableBooleanProperty(
			StyleableProperties.SCROLL_ON_OPEN,
			this,
			"scrollOnOpen",
			false
	);

	public boolean isScrollOnOpen() {
		return scrollOnOpen.get();
	}

	/**
	 * Specifies whether the combo box list should scroll to the current
	 * selected value on open.
	 */
	public StyleableBooleanProperty scrollOnOpenProperty() {
		return scrollOnOpen;
	}

	public void setScrollOnOpen(boolean scrollOnOpen) {
		this.scrollOnOpen.set(scrollOnOpen);
	}

	//================================================================================
	// CSSMetaData
	//================================================================================
	private static class StyleableProperties {
		private static final StyleablePropertyFactory<MFXComboBox<?>> FACTORY = new StyleablePropertyFactory<>(MFXTextField.getClassCssMetaData());
		private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList;

		private static final CssMetaData<MFXComboBox<?>, Boolean> SCROLL_ON_OPEN =
				FACTORY.createBooleanCssMetaData(
						"-mfx-scroll-on-open",
						MFXComboBox::scrollOnOpenProperty,
						false
				);

		static {
			cssMetaDataList = StyleablePropertiesUtils.cssMetaDataList(
					MFXTextField.getClassCssMetaData(),
					SCROLL_ON_OPEN
			);
		}
	}

	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.cssMetaDataList;
	}

	//================================================================================
	// Getters/Setters
	//================================================================================
	public boolean isShowing() {
		return showing.get();
	}

	/**
	 * Specifies whether the popup is showing.
	 */
	public ReadOnlyBooleanProperty showingProperty() {
		return showing.getReadOnlyProperty();
	}

	private void setShowing(boolean showing) {
		this.showing.set(showing);
	}

	public Alignment getPopupAlignment() {
		return popupAlignment.get();
	}

	/**
	 * Specifies the popup's alignment.
	 */
	public ObjectProperty<Alignment> popupAlignmentProperty() {
		return popupAlignment;
	}

	public void setPopupAlignment(Alignment popupAlignment) {
		this.popupAlignment.set(popupAlignment);
	}

	public double getPopupOffsetX() {
		return popupOffsetX.get();
	}

	/**
	 * Specifies the popup's x offset.
	 */
	public DoubleProperty popupOffsetXProperty() {
		return popupOffsetX;
	}

	public void setPopupOffsetX(double popupOffsetX) {
		this.popupOffsetX.set(popupOffsetX);
	}

	public double getPopupOffsetY() {
		return popupOffsetY.get();
	}

	/**
	 * Specifies the popup's y offset.
	 */
	public DoubleProperty popupOffsetYProperty() {
		return popupOffsetY;
	}

	public void setPopupOffsetY(double popupOffsetY) {
		this.popupOffsetY.set(popupOffsetY);
	}

	public BiFunction<Node, Boolean, Animation> getAnimationProvider() {
		return animationProvider.get();
	}

	/**
	 * Specifies the animation of the trailing icon used to open the popup.
	 */
	public BiFunctionProperty<Node, Boolean, Animation> animationProviderProperty() {
		return animationProvider;
	}

	public void setAnimationProvider(BiFunction<Node, Boolean, Animation> animationProvider) {
		this.animationProvider.set(animationProvider);
	}

	@Override
	public T getValue() {
		return value.get();
	}

	@Override
	public ObjectProperty<T> valueProperty() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value.set(value);
	}

	@Override
	public StringConverter<T> getConverter() {
		return converter.get();
	}

	@Override
	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	@Override
	public void setConverter(StringConverter<T> converter) {
		this.converter.set(converter);
	}

	@Override
	public Consumer<String> getOnCommit() {
		return onCommit.get();
	}

	@Override
	public ConsumerProperty<String> onCommitProperty() {
		return onCommit;
	}

	@Override
	public void setOnCommit(Consumer<String> onCommit) {
		this.onCommit.set(onCommit);
	}

	@Override
	public Consumer<String> getOnCancel() {
		return onCancel.get();
	}

	@Override
	public ConsumerProperty<String> onCancelProperty() {
		return onCancel;
	}

	public void setOnCancel(Consumer<String> onCancel) {
		this.onCancel.set(onCancel);
	}

	@Override
	public ObservableList<T> getItems() {
		return items.get();
	}

	@Override
	public ObjectProperty<ObservableList<T>> itemsProperty() {
		return items;
	}

	@Override
	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}

	@Override
	public Function<T, Cell<T>> getCellFactory() {
		return cellFactory.get();
	}

	@Override
	public ObjectProperty<Function<T, Cell<T>>> cellFactoryProperty() {
		return cellFactory;
	}

	@Override
	public void setCellFactory(Function<T, Cell<T>> cellFactory) {
		this.cellFactory.set(cellFactory);
	}

	@Override
	public ComboBoxSelectionModel<T> getSelectionModel() {
		return selectionModel;
	}

	//================================================================================
	// Events
	//================================================================================
	public static final EventType<Event> ON_SHOWING = new EventType<>(Event.ANY, "ON_SHOWING");
	public static final EventType<Event> ON_SHOWN = new EventType<>(Event.ANY, "ON_SHOWN");
	public static final EventType<Event> ON_HIDING = new EventType<>(Event.ANY, "ON_HIDING");
	public static final EventType<Event> ON_HIDDEN = new EventType<>(Event.ANY, "ON_HIDDEN");

	private final EventHandlerProperty<Event> onShowing = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_SHOWING, get());
		}
	};
	private final EventHandlerProperty<Event> onShown = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_SHOWN, get());
		}
	};
	private final EventHandlerProperty<Event> onHiding = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_HIDING, get());
		}
	};
	private final EventHandlerProperty<Event> onHidden = new EventHandlerProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_HIDDEN, get());
		}
	};

	@Override
	public EventHandler<Event> getOnShowing() {
		return onShowing.get();
	}

	@Override
	public EventHandlerProperty<Event> onShowingProperty() {
		return onShowing;
	}

	@Override
	public void setOnShowing(EventHandler<Event> onShowing) {
		this.onShowing.set(onShowing);
	}

	@Override
	public EventHandler<Event> getOnShown() {
		return onShown.get();
	}

	@Override
	public EventHandlerProperty<Event> onShownProperty() {
		return onShown;
	}

	public void setOnShown(EventHandler<Event> onShown) {
		this.onShown.set(onShown);
	}

	@Override
	public EventHandler<Event> getOnHiding() {
		return onHiding.get();
	}

	@Override
	public EventHandlerProperty<Event> onHidingProperty() {
		return onHiding;
	}

	public void setOnHiding(EventHandler<Event> onHiding) {
		this.onHiding.set(onHiding);
	}

	@Override
	public EventHandler<Event> getOnHidden() {
		return onHidden.get();
	}

	@Override
	public EventHandlerProperty<Event> onHiddenProperty() {
		return onHidden;
	}

	public void setOnHidden(EventHandler<Event> onHidden) {
		this.onHidden.set(onHidden);
	}
}
