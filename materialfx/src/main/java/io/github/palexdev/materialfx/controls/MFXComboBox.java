package io.github.palexdev.materialfx.controls;

import io.github.palexdev.materialfx.MFXResourcesLoader;
import io.github.palexdev.materialfx.beans.Alignment;
import io.github.palexdev.materialfx.beans.PositionBean;
import io.github.palexdev.materialfx.beans.properties.functional.BiFunctionProperty;
import io.github.palexdev.materialfx.beans.properties.functional.ConsumerProperty;
import io.github.palexdev.materialfx.beans.properties.functional.FunctionProperty;
import io.github.palexdev.materialfx.controls.base.MFXCombo;
import io.github.palexdev.materialfx.controls.cell.MFXComboBoxCell;
import io.github.palexdev.materialfx.selection.ComboBoxSelectionModel;
import io.github.palexdev.materialfx.skins.MFXComboBoxSkin;
import io.github.palexdev.materialfx.utils.ListChangeProcessor;
import io.github.palexdev.materialfx.utils.NodeUtils;
import io.github.palexdev.materialfx.utils.NumberUtils;
import io.github.palexdev.materialfx.utils.others.FunctionalStringConverter;
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
import javafx.css.PseudoClass;
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
 * <p> - Allows to fully control the popup (offset, height, alignment)
 * <p> - Automatically handles selection when the item's list is modified
 * <p> - Allows to set the combo as editable or not, and in case of changed text
 * to commit the change (pressing enter by default) and specify how to treat the
 * typed text
 * <p> - Also adds a new PseudoClass that activates when the popup opens
 */
public class MFXComboBox<T> extends MFXTextField implements MFXCombo<T> {
	//================================================================================
	// Properties
	//================================================================================
	private final String STYLE_CLASS = "mfx-combo-box";
	private final String STYLESHEET = MFXResourcesLoader.load("css/MFXComboBoxStyle1.css");

	private final ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(false);
	private final ObjectProperty<Alignment> popupAlignment = new SimpleObjectProperty<>(Alignment.of(HPos.CENTER, VPos.BOTTOM));
	private final DoubleProperty maxPopupHeight = new SimpleDoubleProperty(200);
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

	// TODO implement validation
	// TODO add context menu

	protected static final PseudoClass POPUP_OPEN_PSEUDO_CLASS = PseudoClass.getPseudoClass("popup");

	//================================================================================
	// Constructors
	//================================================================================
	public MFXComboBox() {
		this(FXCollections.observableArrayList());
	}

	public MFXComboBox(ObservableList<T> items) {
		setItems(items);
		setFloatingText("ComboBox");
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
			RotateTransition transition = new RotateTransition(Duration.millis(150), node);
			transition.setInterpolator(Interpolator.EASE_OUT);
			transition.setToAngle(showing ? 180 : 0);
			return transition;
		});

		// Default converter
		setConverter(FunctionalStringConverter.converter(s -> {
			throw new UnsupportedOperationException();
		}, Object::toString));

		showing.addListener(invalidated -> pseudoClassStateChanged(POPUP_OPEN_PSEUDO_CLASS, showing.get()));

		items.addListener((observable, oldValue, newValue) -> {
			oldValue.removeListener(itemsChanged);
			newValue.addListener(itemsChanged);
		});
		getItems().addListener(this::itemsChanged);
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
	 * Responsible for updating the selection when the items list changes.
	 */
	protected void itemsChanged(ListChangeListener.Change<? extends T> change) {
		if (getSelectionModel().getSelectedIndex() == -1) return;

		if (change.getList().isEmpty()) {
			getSelectionModel().clearSelection();
			return;
		}

		ListChangeHelper.Change c = ListChangeHelper.processChange(change, NumberRange.of(0, Integer.MAX_VALUE));
		Set<Integer> indexes = new HashSet<>();
		indexes.add(getSelectionModel().getSelectedIndex());
		ListChangeProcessor updater = new ListChangeProcessor(indexes);
		c.processReplacement((changed, removed) -> {
			int selected = getSelectionModel().getSelectedIndex();
			if (changed.contains(selected) || removed.contains(selected)) {
				getSelectionModel().selectItem(getItems().get(selected));
			}
		});
		c.processAddition((from, to, added) -> {
			updater.computeAddition(added.size(), from);
			getSelectionModel().selectIndex(updater.getIndexes().toArray(new Integer[0])[0]);
		});
		c.processRemoval((from, to, removed) -> {
			updater.computeRemoval(removed, from);
			int index = NumberUtils.clamp(updater.getIndexes().toArray(new Integer[0])[0], 0, getItems().size() - 1);
			getSelectionModel().selectIndex(index);
		});

		setValue(getSelectionModel().getSelectedItem());
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

	public double getMaxPopupHeight() {
		return maxPopupHeight.get();
	}

	/**
	 * Specifies the max popup's height.
	 */
	public DoubleProperty maxPopupHeightProperty() {
		return maxPopupHeight;
	}

	public void setMaxPopupHeight(double maxPopupHeight) {
		this.maxPopupHeight.set(maxPopupHeight);
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

	private final ObjectProperty<EventHandler<Event>> onShowing = new SimpleObjectProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_SHOWING, get());
		}
	};
	private final ObjectProperty<EventHandler<Event>> onShown = new SimpleObjectProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_SHOWN, get());
		}
	};
	private final ObjectProperty<EventHandler<Event>> onHiding = new SimpleObjectProperty<>() {
		@Override
		protected void invalidated() {
			setEventHandler(ON_HIDING, get());
		}
	};
	private final ObjectProperty<EventHandler<Event>> onHidden = new SimpleObjectProperty<>() {
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
	public ObjectProperty<EventHandler<Event>> onShowingProperty() {
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
	public ObjectProperty<EventHandler<Event>> onShownProperty() {
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
	public ObjectProperty<EventHandler<Event>> onHidingProperty() {
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
	public ObjectProperty<EventHandler<Event>> onHiddenProperty() {
		return onHidden;
	}

	public void setOnHidden(EventHandler<Event> onHidden) {
		this.onHidden.set(onHidden);
	}

	//================================================================================
	// Overridden Methods
	//================================================================================
	@Override
	protected Skin<?> createDefaultSkin() {
		return new MFXComboBoxSkin<>(this, floating);
	}

	@Override
	public String getUserAgentStylesheet() {
		return STYLESHEET;
	}
}
